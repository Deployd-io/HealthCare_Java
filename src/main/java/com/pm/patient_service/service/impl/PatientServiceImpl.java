package com.pm.patient_service.service.impl;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.exception.EmailAlreadyExistsException;
import com.pm.patient_service.exception.PatientNotFoundException;
import com.pm.patient_service.grpc.BillingServiceGrpcClient;
import com.pm.patient_service.kafka.KafkaProducer;
import com.pm.patient_service.mapper.PatientMapper;
import com.pm.patient_service.model.Patient;
import com.pm.patient_service.repository.PatientRepository;
import com.pm.patient_service.service.PatientService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PatientServiceImpl implements PatientService {
	private static final Logger log = LoggerFactory.getLogger(PatientServiceImpl.class);
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;
    public PatientServiceImpl(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient,
                              KafkaProducer kafkaProducer) {
							log.debug(">>> Entering PatientServiceImpl()");
        this.patientRepository = patientRepository;
		log.debug("PatientServiceImpl(): patientRepository → {}", patientRepository);
		log.debug("PatientServiceImpl(): this.patientRepository → {}", this.patientRepository);
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
		log.debug("<<< Exiting PatientServiceImpl()");
		log.debug("PatientServiceImpl(): billingServiceGrpcClient → {}", billingServiceGrpcClient);
		log.debug("PatientServiceImpl(): this.billingServiceGrpcClient → {}", this.billingServiceGrpcClient);
    }
    @Override
    public List<PatientResponseDTO> getPatients() {
		log.debug(">>> Entering getPatients()");
        List<Patient> patients = patientRepository.findAll();
		log.debug("PatientServiceImpl(): kafkaProducer → {}", kafkaProducer);
		log.debug("getPatients(): patients → {}", patients);
        return patients.stream().map(PatientMapper::toPatientResponseDTO).toList();
    }
	log.debug("PatientServiceImpl(): this.kafkaProducer → {}", this.kafkaProducer);

    @Override
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if(patientRepository.existsByEmail(patientRequestDTO.email())) {
            throw new EmailAlreadyExistsException("A patient with this email "
             + "already exists" + patientRequestDTO.email());
        }
		long start = System.currentTimeMillis();
        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(),
                newPatient.getName(), newPatient.getEmail());

        kafkaProducer.sendEvent(newPatient);

        return PatientMapper.toPatientResponseDTO(newPatient);
    }

    @Override
    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with ID: " + id));
				long start = System.currentTimeMillis();

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.email(), id)) {
            throw new EmailAlreadyExistsException("A patient with this email "
                    + "already exists" + patientRequestDTO.email());
        }

        patient.setName(patientRequestDTO.name());
        patient.setEmail(patientRequestDTO.email());
        patient.setAddress(patientRequestDTO.address());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.dateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toPatientResponseDTO(updatedPatient);
    }

    @Override
    public void deletePatient(UUID id) {
		log.debug(">>> Entering deletePatient(id={})", id);
        patientRepository.deleteById(id);
		log.debug("<<< Exiting deletePatient(id={})", id);
    }
}
