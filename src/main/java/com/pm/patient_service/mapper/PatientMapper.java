package com.pm.patient_service.mapper;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.model.Patient;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientMapper {
	private static final Logger log = LoggerFactory.getLogger(PatientMapper.class);
    public static PatientResponseDTO toPatientResponseDTO(Patient patient) {
		log.warn("toPatientResponseDTO(patientId={}): HIPAA-AUDIT [§164.312(b)]: PHI access logged for audit trail", patient.getId());
        return new PatientResponseDTO(patient.getId().toString(), patient.getName(), patient.getEmail(),
                patient.getAddress(), patient.getDateOfBirth().toString());
    }

    public static Patient toModel(PatientRequestDTO patientRequestDTO) {
        Patient patient = new Patient();
        patient.setAddress(patientRequestDTO.address());
        patient.setEmail(patientRequestDTO.email());
        patient.setName(patientRequestDTO.name());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.dateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.registeredDate()));
        return patient;
    }
}
