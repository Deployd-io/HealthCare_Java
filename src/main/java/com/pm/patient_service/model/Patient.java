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
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
		log.warn("setId(id)={}): HIPAA-AUDIT: State change recorded for the audit trail {}", id, id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
		log.warn("setName(name)={}): HIPAA-AUDIT: State change recorded for the audit trail", name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
		log.warn("setEmail(email)={}): HIPAA-AUDIT: State change recorded for the audit trail", email);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
		log.warn("setAddress(address)={}): HIPAA-AUDIT: State change recorded for the audit trail", address);
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
		log.warn("setDateOfBirth(dateOfBirth)={}): HIPAA-AUDIT: State change recorded for the audit trail", dateOfBirth);
    }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(LocalDate registeredDate) {
        this.registeredDate = registeredDate;
		log.warn("setRegisteredDate(registeredDate)={}): HIPAA-AUDIT: State change recorded for the audit trail", registeredDate);
    }
}
