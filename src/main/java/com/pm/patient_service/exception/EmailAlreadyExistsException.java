package com.pm.patient_service.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailAlreadyExistsException extends RuntimeException{
	private static final Logger log = LoggerFactory.getLogger(EmailAlreadyExistsException.class);
    public EmailAlreadyExistsException(String message) {
        super(message);
		log.debug(">>> Entering EmailAlreadyExistsException(message={})", message);
		log.debug("<<< Exiting EmailAlreadyExistsException(message={})", message);
    }
}
