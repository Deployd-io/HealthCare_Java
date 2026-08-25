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
							logger.log_integrity("ENTERING: PatientServiceImpl() method");
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }
    @Override
    public List<PatientResponseDTO> getPatients() {
		logger.network_visibility("ENTERING: getPatients() method");
        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(PatientMapper::toPatientResponseDTO).toList();
    }

    @Override
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if(patientRepository.existsByEmail(patientRequestDTO.email())) {
			logger.log_integrity("ENTERING: createPatient() method");
            throw new EmailAlreadyExistsException("A patient with this email "
             + "already exists" + patientRequestDTO.email());
        }
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
				logger.log_integrity("ENTERING: updatePatient() method");

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.email(), id)) {
            throw new EmailAlreadyExistsException("A patient with this email "
                    + "already exists" + patientRequestDTO.email());
        }

        patient.setName(patientRequestDTO.name());
        patient.setEmail(patientRequestDTO.email());
        patient.setAddress(patientRequestDTO.address());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.dateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);
		log.info("updatePatient(id,patientRequestDTO)={},{}): SOC2-AUDIT [CC8.1]: Change management operation performed", id, patientRequestDTO);
		log.info("updatePatient(id,patientRequestDTO)={},{}): SOC2-AUDIT [CC6.6]: Database transaction completed", id, patientRequestDTO);
        return PatientMapper.toPatientResponseDTO(updatedPatient);
    }

    @Override
    public void deletePatient(UUID id) {
		logger.audit_coverage("ENTERING: deletePatient() method");
        patientRepository.deleteById(id);
    }
}
