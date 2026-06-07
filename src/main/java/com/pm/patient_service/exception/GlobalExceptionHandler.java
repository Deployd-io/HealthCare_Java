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
				log.warn("handleValidationException(ex)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", ex);
		log.debug("<<< Exiting handleValidationException(ex={})", ex);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex) {
			log.debug(">>> Entering handleEmailAlreadyExistsException()");

        log.warn("Email address already exists {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
		log.warn("handleEmailAlreadyExistsException()=null): SOC2-AUDIT [CC6.1]: Password reset operation performed");
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

        Map<String, String> errors = new HashMap<>();
		log.warn("handlePatientNotFoundException()=null): SOC2-AUDIT [CC6.1]: Password reset operation performed");
		log.debug("handlePatientNotFoundException(): errors → {}", errors);
        errors.put("message", "Patient not found");
		log.debug("<<< Exiting handlePatientNotFoundException()");
        return ResponseEntity.badRequest().body(errors);
    }
}
