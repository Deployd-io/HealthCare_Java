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
							log.debug(">>> Entering PatientServiceImpl(patientRepository={},billingServiceGrpcClient={},kafkaProducer={})", patientRepository, billingServiceGrpcClient, kafkaProducer);
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
		log.debug("PatientServiceImpl(patientRepository={},billingServiceGrpcClient={},kafkaProducer={}): this.patientRepository → {}", patientRepository, billingServiceGrpcClient, kafkaProducer, this.patientRepository);
        this.kafkaProducer = kafkaProducer;
		log.debug("<<< Exiting PatientServiceImpl(patientRepository={},billingServiceGrpcClient={},kafkaProducer={})", patientRepository, billingServiceGrpcClient, kafkaProducer);
    }
    @Override
    public List<PatientResponseDTO> getPatients() {
		log.debug(">>> Entering getPatients()");
        List<Patient> patients = patientRepository.findAll();
		log.debug("getPatients(): patients → {}", patients);
		log.debug("<<< Exiting getPatients()");
        return patients.stream().map(PatientMapper::toPatientResponseDTO).toList();
    }

    @Override
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
		log.debug(">>> Entering createPatient(patientRequestDTO={})", patientRequestDTO);
        if(patientRepository.existsByEmail(patientRequestDTO.email())) {
            throw new EmailAlreadyExistsException("A patient with this email "
             + "already exists" + patientRequestDTO.email());
        }
        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

		log.debug("createPatient(patientRequestDTO={}): newPatient → {}", patientRequestDTO, newPatient);
        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(),
                newPatient.getName(), newPatient.getEmail());

        kafkaProducer.sendEvent(newPatient);

		log.debug("<<< Exiting createPatient(patientRequestDTO={})", patientRequestDTO);
        return PatientMapper.toPatientResponseDTO(newPatient);
    }

    @Override
    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
		log.debug(">>> Entering updatePatient(id={},patientRequestDTO={})", id, patientRequestDTO);
        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with ID: " + id));

		log.debug("updatePatient(id={},patientRequestDTO={}): patient → {}", id, patientRequestDTO, patient);
        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.email(), id)) {
            throw new EmailAlreadyExistsException("A patient with this email "
                    + "already exists" + patientRequestDTO.email());
        }

        patient.setName(patientRequestDTO.name());
        patient.setEmail(patientRequestDTO.email());
        patient.setAddress(patientRequestDTO.address());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.dateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);
		log.debug("updatePatient(id={},patientRequestDTO={}): updatedPatient → {}", id, patientRequestDTO, updatedPatient);
		log.debug("<<< Exiting updatePatient(id={},patientRequestDTO={})", id, patientRequestDTO);
        return PatientMapper.toPatientResponseDTO(updatedPatient);
    }

    @Override
    public void deletePatient(UUID id) {
		log.debug(">>> Entering deletePatient(id={})", id);
        patientRepository.deleteById(id);
		log.debug("<<< Exiting deletePatient(id={})", id);
    }
}
