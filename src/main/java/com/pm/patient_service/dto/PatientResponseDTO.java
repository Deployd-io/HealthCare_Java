package com.pm.patient_service.dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record PatientResponseDTO(String id, String name, String email, String address, String dateOfBirth) {
private static final Logger log = LoggerFactory.getLogger(PatientResponseDTO.class);
}
