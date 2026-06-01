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

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description="API for managing Patients")
public class PatientController {
	private static final Logger log = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;
    public PatientController(PatientService patientService) {
		log.debug(">>> Entering PatientController(patientService={})", patientService);
        this.patientService = patientService;
		log.debug("<<< Exiting PatientController(patientService={})", patientService);
		log.debug("PatientController(patientService={}): this.patientService → {}", patientService, this.patientService);
		log.debug("PatientController(patientService={}): patientService → {}", patientService, patientService);
    }

	log.debug("PatientController(patientService={}): this.patientService → {}", patientService, this.patientService);
	log.debug("PatientController(patientService={}): patientService → {}", patientService, patientService);
    @GetMapping
    @Operation(summary = "Get Patients")
    public ResponseEntity<List<PatientResponseDTO>> getPatients() {
		log.debug(">>> Entering getPatients()");
        List<PatientResponseDTO> patients = patientService.getPatients();
		log.debug("getPatients(): patients → {}", patients);
		log.debug("<<< Exiting getPatients()");
        return ResponseEntity.ok(patients);
    }

    @PostMapping
    @Operation(summary = "Create a new Patient")
    public ResponseEntity<PatientResponseDTO> createPatient(
            @Validated({Default.class, CreatePatientValidationGroup.class})
            @RequestBody PatientRequestDTO patientRequestDTO) {
        return ResponseEntity.ok(patientService.createPatient(patientRequestDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Patient")
    public ResponseEntity<PatientResponseDTO> updatePatient(
            @PathVariable UUID id,@Validated({Default.class})
            @RequestBody PatientRequestDTO patientRequestDTO) {
        return ResponseEntity.ok(patientService.updatePatient(id, patientRequestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Patient")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
		log.debug(">>> Entering deletePatient(id={})", id);
        patientService.deletePatient(id);
		log.debug("<<< Exiting deletePatient(id={})", id);
        return ResponseEntity.noContent().build();
    }
}
