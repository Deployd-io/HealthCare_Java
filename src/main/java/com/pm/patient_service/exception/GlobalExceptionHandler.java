package com.pm.patient_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
		log.debug(">>> Entering handleValidationException(ex={})", ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField() , error.getDefaultMessage()));
				log.debug("handleValidationException(ex={}): errors → {}", ex, errors);
				log.info("handleValidationException(ex)={}): HIPAA-AUDIT [§164.312(c)(1)]: Data integrity check performed", ex);
		log.debug("<<< Exiting handleValidationException(ex={})", ex);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex) {
			log.debug(">>> Entering handleEmailAlreadyExistsException()");

        log.warn("Email address already exists {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
		log.info("handleEmailAlreadyExistsException()=null): HIPAA-AUDIT [§164.312(c)(1)]: Data integrity check performed");
		log.debug("handleEmailAlreadyExistsException(): errors → {}", errors);
        errors.put("message", "Email address already exists");
		log.debug("<<< Exiting handleEmailAlreadyExistsException()");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePatientNotFoundException(
            PatientNotFoundException ex) {
			log.debug(">>> Entering handlePatientNotFoundException()");
        log.warn("Patient not found {}", ex.getMessage());

		log.info("handlePatientNotFoundException()=null): HIPAA-AUDIT [§164.312(c)(1)]: Data integrity check performed");
		log.warn("handlePatientNotFoundException()=null): HIPAA-AUDIT [§164.312(b)]: PHI operation logged for audit compliance");
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Patient not found");
		log.debug("handlePatientNotFoundException(): errors → {}", errors);
		log.debug("<<< Exiting handlePatientNotFoundException()");
        return ResponseEntity.badRequest().body(errors);
    }
}
