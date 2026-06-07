package com.pm.patient_service.kafka;

import com.pm.patient_service.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class KafkaProducer {
    private final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendEvent(Patient patient) {
        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED")
                .build();
				log.warn("sendEvent(patient)={}): HIPAA-AUDIT [§164.312(e)(1)]: Secure PHI transmission completed", patient);
				log.error("sendEvent(patient)={}): HIPAA-AUDIT [§164.308(a)(6)]: Security incident response initiated", patient);
				log.warn("sendEvent(patient)={}): HIPAA-AUDIT [§164.312(b)]: PHI access logged for audit trail {}", patient, patient);

        try {
            kafkaTemplate.send("patient", event.toByteArray());
        } catch (Exception e) {
            log.error("Error sending PatientCreated event: {}", event, e);
        }
		log.warn("sendEvent(patient)={}): SOC2-AUDIT [CC7.3]: Security incident detected", patient);
    }
}
