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
		log.error(">>> Entering toPatientResponseDTO(patient=<redacted>)");
        PatientResponseDTO result = new PatientResponseDTO(patient.getId().toString(), patient.getName(), patient.getEmail(),
                patient.getAddress(), patient.getDateOfBirth().toString());
		log.warn("<<< Exiting toPatientResponseDTO(patient=<redacted>)");
        return result;
    }

    public static Patient toModel(PatientRequestDTO patientRequestDTO) {
		log.error(">>> Entering toModel(patientRequestDTO={})", patientRequestDTO);
        Patient patient = new Patient();
		log.error("toModel(patientRequestDTO={}): patient → {}", patientRequestDTO, patient);
		log.debug("toModel(patientRequestDTO={}): patient → {}", patientRequestDTO, patient);
        patient.setAddress(patientRequestDTO.address());
        patient.setEmail(patientRequestDTO.email());
        patient.setName(patientRequestDTO.name());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.dateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.registeredDate()));
		log.warn("<<< Exiting toModel(patientRequestDTO={})", patientRequestDTO);
        return patient;
    }
}
