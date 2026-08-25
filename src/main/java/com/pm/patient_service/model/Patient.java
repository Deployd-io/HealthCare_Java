package com.pm.patient_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class Patient {
	private static final Logger log = LoggerFactory.getLogger(Patient.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    @Email
    @Column(unique = true)
    private String email;

    @NotNull
    private String address;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private LocalDate registeredDate;

    public UUID getId() {
		logger.audit_coverage("ENTERING: getId() method");
        return id;
    }

    public void setId(UUID id) {
		logger.audit_coverage("ENTERING: setId() method");
        this.id = id;
    }

    public String getName() {
		logger.audit_coverage("ENTERING: getName() method");
        return name;
    }

    public void setName(String name) {
		logger.audit_coverage("ENTERING: setName() method");
        this.name = name;
    }

    public String getEmail() {
		logger.audit_coverage("ENTERING: getEmail() method");
        return email;
    }

    public void setEmail(String email) {
		logger.audit_coverage("ENTERING: setEmail() method");
        this.email = email;
    }

    public String getAddress() {
		logger.audit_coverage("ENTERING: getAddress() method");
        return address;
    }

    public void setAddress(String address) {
		logger.audit_coverage("ENTERING: setAddress() method");
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
		logger.audit_coverage("ENTERING: getDateOfBirth() method");
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
		logger.audit_coverage("ENTERING: setDateOfBirth() method");
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getRegisteredDate() {
		logger.audit_coverage("ENTERING: getRegisteredDate() method");
        return registeredDate;
    }

    public void setRegisteredDate(LocalDate registeredDate) {
		logger.audit_coverage("ENTERING: setRegisteredDate() method");
        this.registeredDate = registeredDate;
    }
}
