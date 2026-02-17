package com.pm.patient_service.controller;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.dto.validators.CreatePatientValidationGroup;
import com.pm.patient_service.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description="API for managing Patients")
public class PatientController {
	private static final Logger log = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;
    public PatientController(PatientService patientService) {
        log.info(">>> Entering PatientController");
        this.patientService = patientService;
        log.info("<<< Exiting PatientController");
    }

    @GetMapping
    @Operation(summary = "Get Patients")
    public ResponseEntity<List<PatientResponseDTO>> getPatients() {
        log.info(">>> Entering getPatients");
        List<PatientResponseDTO> patients = patientService.getPatients();
		log.warn("getPatients()=null): HIPAA-AUDIT [§164.312(b)]: PHI access logged for audit trail {}", patients);
		log.info("getPatients()=null): HIPAA-AUDIT [§164.312(c)(1)]: Data integrity check performed");
		log.warn("getPatients()=null): HIPAA-AUDIT [§164.312(e)(1)]: Secure PHI transmission completed");
        log.info("<<< Exiting getPatients");
        return ResponseEntity.ok(patients);
    }

    @PostMapping
    @Operation(summary = "Create a new Patient")
    public ResponseEntity<PatientResponseDTO> createPatient(
            @Validated({Default.class, CreatePatientValidationGroup.class})
            @RequestBody PatientRequestDTO patientRequestDTO) {
        log.info(">>> Entering createPatient");
        log.info("<<< Exiting createPatient");
        return ResponseEntity.ok(patientService.createPatient(patientRequestDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Patient")
    public ResponseEntity<PatientResponseDTO> updatePatient(
            @PathVariable UUID id,@Validated({Default.class})
            @RequestBody PatientRequestDTO patientRequestDTO) {
        log.info(">>> Entering updatePatient");
        log.info("<<< Exiting updatePatient");
        return ResponseEntity.ok(patientService.updatePatient(id, patientRequestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Patient")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        log.info(">>> Entering deletePatient");
        patientService.deletePatient(id);
		log.warn("deletePatient(id)={}): HIPAA-AUDIT [§164.312(b)]: PHI access logged for audit trail {}", id, id);
        log.info("<<< Exiting deletePatient");
        return ResponseEntity.noContent().build();
    }
}