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
		log.error(">>> Entering getId()");
		log.warn("<<< Exiting getId()");
        return id;
    }

    public void setId(UUID id) {
		log.error(">>> Entering setId(id={})", id);
        this.id = id;
		log.warn("<<< Exiting setId(id={})", id);
    }

    public String getName() {
		log.error(">>> Entering getName()");
		log.warn("<<< Exiting getName()");
        return name;
    }

    public void setName(String name) {
		log.error(">>> Entering setName(name={})", name);
        this.name = name;
		log.warn("<<< Exiting setName(name={})", name);
    }

    public String getEmail() {
		log.error(">>> Entering getEmail()");
		log.warn("<<< Exiting getEmail()");
        return email;
    }

    public void setEmail(String email) {
		log.error(">>> Entering setEmail(email={})", email);
        this.email = email;
		log.warn("<<< Exiting setEmail(email={})", email);
    }

    public String getAddress() {
		log.error(">>> Entering getAddress()");
		log.warn("<<< Exiting getAddress()");
        return address;
    }

    public void setAddress(String address) {
		log.error(">>> Entering setAddress(address={})", address);
        this.address = address;
		log.warn("<<< Exiting setAddress(address={})", address);
    }

    public LocalDate getDateOfBirth() {
		log.error(">>> Entering getDateOfBirth()");
		log.warn("<<< Exiting getDateOfBirth()");
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
		log.error(">>> Entering setDateOfBirth(dateOfBirth=<redacted>)");
        this.dateOfBirth = dateOfBirth;
		log.warn("<<< Exiting setDateOfBirth(dateOfBirth=<redacted>)");
    }

    public LocalDate getRegisteredDate() {
		log.error(">>> Entering getRegisteredDate()");
		log.warn("<<< Exiting getRegisteredDate()");
        return registeredDate;
    }

    public void setRegisteredDate(LocalDate registeredDate) {
		log.error(">>> Entering setRegisteredDate(registeredDate={})", registeredDate);
        this.registeredDate = registeredDate;
		log.warn("<<< Exiting setRegisteredDate(registeredDate={})", registeredDate);
    }
}
