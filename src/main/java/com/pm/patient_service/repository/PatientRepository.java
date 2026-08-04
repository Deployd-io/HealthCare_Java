package com.pm.patient_service.repository;

import com.pm.patient_service.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
	private static final Logger log = LoggerFactory.getLogger(PatientRepository.class);
    boolean existsByEmail(String email);
	log.debug(">>> Entering existsByEmail()");
    boolean existsByEmailAndIdNot(String email, UUID uuid);
	log.debug("<<< Exiting existsByEmail()");
}
