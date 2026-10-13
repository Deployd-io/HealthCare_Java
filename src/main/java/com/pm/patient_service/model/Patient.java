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
		log.debug(">>> Entering getId()");
		log.debug("<<< Exiting getId()");
        return id;
    }

    public void setId(UUID id) {
		log.debug(">>> Entering setId(id={})", id);
        this.id = id;
		log.debug("<<< Exiting setId(id={})", id);
    }

    public String getName() {
		log.debug(">>> Entering getName()");
		log.debug("<<< Exiting getName()");
        return name;
    }

    public void setName(String name) {
		log.debug(">>> Entering setName(name={})", name);
        this.name = name;
		log.debug("<<< Exiting setName(name={})", name);
    }

    public String getEmail() {
		log.debug(">>> Entering getEmail()");
		log.debug("<<< Exiting getEmail()");
        return email;
    }

    public void setEmail(String email) {
		log.debug(">>> Entering setEmail(email={})", email);
        this.email = email;
		log.debug("<<< Exiting setEmail(email={})", email);
    }

    public String getAddress() {
		log.debug(">>> Entering getAddress()");
		log.debug("<<< Exiting getAddress()");
        return address;
    }

    public void setAddress(String address) {
		log.debug(">>> Entering setAddress(address={})", address);
        this.address = address;
		log.debug("<<< Exiting setAddress(address={})", address);
    }

    public LocalDate getDateOfBirth() {
		log.debug(">>> Entering getDateOfBirth()");
		log.debug("<<< Exiting getDateOfBirth()");
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
		log.debug(">>> Entering setDateOfBirth(dateOfBirth=<redacted>)");
        this.dateOfBirth = dateOfBirth;
		log.debug("<<< Exiting setDateOfBirth(dateOfBirth=<redacted>)");
    }

    public LocalDate getRegisteredDate() {
		log.debug(">>> Entering getRegisteredDate()");
		log.debug("<<< Exiting getRegisteredDate()");
        return registeredDate;
    }

    public void setRegisteredDate(LocalDate registeredDate) {
		log.debug(">>> Entering setRegisteredDate(registeredDate={})", registeredDate);
        this.registeredDate = registeredDate;
		log.debug("<<< Exiting setRegisteredDate(registeredDate={})", registeredDate);
    }
}
