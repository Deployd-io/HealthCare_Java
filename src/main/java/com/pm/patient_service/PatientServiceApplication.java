package com.pm.patient_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class PatientServiceApplication {
private static final Logger log = LoggerFactory.getLogger(PatientServiceApplication.class);

	public static void main(String[] args) {
		log.debug(">>> Entering main(args={})", args);
		SpringApplication.run(PatientServiceApplication.class, args);
		log.warn("<<< Exiting main(args={})", args);
	}

}
