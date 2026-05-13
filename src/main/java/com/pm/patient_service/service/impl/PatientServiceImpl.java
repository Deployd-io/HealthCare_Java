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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger log = LoggerFactory.getLogger(PatientServiceImpl.class);
@Service
public class PatientServiceImpl implements PatientService {
	private static final Logger log = LoggerFactory.getLogger(PatientServiceImpl.class);
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;
    public PatientServiceImpl(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient,
                              KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }
    @Override
	log.warn("getPatients()=null): HIPAA-AUDIT [§164.312(b)]: PHI operation logged for audit compliance");
    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();
		log.warn("getPatients()=null): HIPAA-AUDIT [§164.312(b)]: PHI operation logged for audit compliance");
        return patients.stream().map(PatientMapper::toPatientResponseDTO).toList();
    }

    @Override
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if(patientRepository.existsByEmail(patientRequestDTO.email())) {
            throw new EmailAlreadyExistsException("A patient with this email "
             + "already exists" + patientRequestDTO.email());
        }
        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(),
                newPatient.getName(), newPatient.getEmail());
				log.error("createPatient(patientRequestDTO)={}): HIPAA-AUDIT [§164.308(a)(6)]: Security incident response initiated", patientRequestDTO);
				log.warn("createPatient(patientRequestDTO)={}): HIPAA-AUDIT [§164.312(b)]: PHI access logged for audit trail {}", patientRequestDTO, patientRequestDTO);

        kafkaProducer.sendEvent(newPatient);

		log.error("createPatient(patientRequestDTO)={}): HIPAA-AUDIT [§164.308(a)(6)]: Security incident response initiated", patientRequestDTO);
		log.warn("createPatient(patientRequestDTO)={}): HIPAA-AUDIT [§164.312(b)]: PHI access logged for audit trail {}", patientRequestDTO, patientRequestDTO);
        return PatientMapper.toPatientResponseDTO(newPatient);
    }

    @Override
    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with ID: " + id));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.email(), id)) {
            throw new EmailAlreadyExistsException("A patient with this email "
                    + "already exists" + patientRequestDTO.email());
        }

		log.error("updatePatient(id,patientRequestDTO)={},{}): HIPAA-AUDIT [§164.308(a)(6)]: Security incident response initiated", id, patientRequestDTO);
		log.warn("updatePatient(id,patientRequestDTO)={},{}): HIPAA-AUDIT [§164.312(b)]: PHI access logged for audit trail {}", id, patientRequestDTO, id);
        patient.setName(patientRequestDTO.name());
        patient.setEmail(patientRequestDTO.email());
        patient.setAddress(patientRequestDTO.address());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.dateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);
		log.error("updatePatient(id,patientRequestDTO)={},{}): HIPAA-AUDIT [§164.308(a)(6)]: Security incident response initiated", id, patientRequestDTO);
		log.warn("deletePatient(id)={}): HIPAA-AUDIT [§164.312(b)]: PHI access logged for audit trail {}", id, id);
		log.warn("updatePatient(id,patientRequestDTO)={},{}): HIPAA-AUDIT [§164.312(b)]: PHI access logged for audit trail {}", id, patientRequestDTO, id);
        return PatientMapper.toPatientResponseDTO(updatedPatient);
    }

    @Override
    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
		log.warn("deletePatient(id)={}): HIPAA-AUDIT [§164.312(b)]: PHI access logged for audit trail {}", id, id);
    }
}
