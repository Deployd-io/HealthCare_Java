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
		log.error(">>> Entering handleValidationException(ex={})", ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField() , error.getDefaultMessage()));
				log.error("handleValidationException(ex={}): errors → {}", ex, errors);
        ResponseEntity<Map<String, String>> result = ResponseEntity.badRequest().body(errors);
		log.warn("<<< Exiting handleValidationException(ex={}) -> {}", ex, result);
        return result;
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex) {
			log.error(">>> Entering handleEmailAlreadyExistsException(ex={})", ex);

        log.warn("Email address already exists {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Email address already exists");
        ResponseEntity<Map<String, String>> result = ResponseEntity.badRequest().body(errors);
		log.warn("<<< Exiting handleEmailAlreadyExistsException(ex={}) -> {}", ex, result);
        return result;
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePatientNotFoundException(
            PatientNotFoundException ex) {
			log.error(">>> Entering handlePatientNotFoundException(ex={})", ex);
        log.warn("Patient not found {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Patient not found");
        ResponseEntity<Map<String, String>> result = ResponseEntity.badRequest().body(errors);
		log.warn("<<< Exiting handlePatientNotFoundException(ex={}) -> {}", ex, result);
        return result;
    }
}
