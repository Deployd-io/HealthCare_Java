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
							log.error(">>> Entering PatientServiceImpl(patientRepository={},billingServiceGrpcClient={},kafkaProducer={})", patientRepository, billingServiceGrpcClient, kafkaProducer);
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
		log.debug("PatientServiceImpl(patientRepository={},billingServiceGrpcClient={},kafkaProducer={}): this.patientRepository → {}", patientRepository, billingServiceGrpcClient, kafkaProducer, this.patientRepository);
        this.kafkaProducer = kafkaProducer;
		log.warn("<<< Exiting PatientServiceImpl(patientRepository={},billingServiceGrpcClient={},kafkaProducer={})", patientRepository, billingServiceGrpcClient, kafkaProducer);
    }
    @Override
    public List<PatientResponseDTO> getPatients() {
		log.error(">>> Entering getPatients()");
        long start = System.currentTimeMillis();
        List<Patient> patients = patientRepository.findAll();
		log.error("getPatients(): find query executed in {} ms", (System.currentTimeMillis() - start));
		log.error("getPatients(): patients → {}", patients);
        return patients.stream().map(PatientMapper::toPatientResponseDTO).toList();
    }

    @Override
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
		log.error(">>> Entering createPatient(patientRequestDTO={})", patientRequestDTO);
        long start = System.currentTimeMillis();
        if(patientRepository.existsByEmail(patientRequestDTO.email())) {
            throw new EmailAlreadyExistsException("A patient with this email "
             + "already exists" + patientRequestDTO.email());
        }
		log.error("createPatient(patientRequestDTO)={}: database query executed in {} ms", patientRequestDTO, (System.currentTimeMillis() - start));
        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));
		log.error("createPatient(patientRequestDTO={}): newPatient → {}", patientRequestDTO, newPatient);
		log.error("createPatient(patientRequestDTO)={}: save query executed in {} ms", patientRequestDTO, (System.currentTimeMillis() - start));

        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(),
                newPatient.getName(), newPatient.getEmail());

        kafkaProducer.sendEvent(newPatient);

        PatientResponseDTO result = PatientMapper.toPatientResponseDTO(newPatient);
		log.warn("<<< Exiting createPatient(patientRequestDTO={}) -> {}", patientRequestDTO, result);
        return result;
    }

    @Override
    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
		log.error(">>> Entering updatePatient(id={},patientRequestDTO={})", id, patientRequestDTO);
        long start = System.currentTimeMillis();
        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with ID: " + id));

		log.error("updatePatient(id,patientRequestDTO)={},{}: find query executed in {} ms", id, patientRequestDTO, (System.currentTimeMillis() - start));
		log.error("updatePatient(id={},patientRequestDTO={}): patient → {}", id, patientRequestDTO, patient);
        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.email(), id)) {
            throw new EmailAlreadyExistsException("A patient with this email "
                    + "already exists" + patientRequestDTO.email());
        }
		log.error("updatePatient(id,patientRequestDTO)={},{}: database query executed in {} ms", id, patientRequestDTO, (System.currentTimeMillis() - start));

        patient.setName(patientRequestDTO.name());
		log.debug("updatePatient(id={},patientRequestDTO={}): patient → {}", id, patientRequestDTO, patient);
        patient.setEmail(patientRequestDTO.email());
        patient.setAddress(patientRequestDTO.address());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.dateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);
		log.error("updatePatient(id={},patientRequestDTO={}): updatedPatient → {}", id, patientRequestDTO, updatedPatient);
        log.error("updatePatient(id,patientRequestDTO)={},{}: save query executed in {} ms", id, patientRequestDTO, (System.currentTimeMillis() - start));
        PatientResponseDTO result = PatientMapper.toPatientResponseDTO(updatedPatient);
		log.warn("<<< Exiting updatePatient(id={},patientRequestDTO={}) -> {}", id, patientRequestDTO, result);
        return result;
    }

    @Override
    public void deletePatient(UUID id) {
		log.error(">>> Entering deletePatient(id={})", id);
        patientRepository.deleteById(id);
		log.warn("<<< Exiting deletePatient(id={})", id);
    }
}
