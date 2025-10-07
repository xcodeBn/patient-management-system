package io.xcodebn.analyticsservice.kafka;


import com.google.protobuf.InvalidProtocolBufferException;
import io.xcodebn.analyticsservice.model.PatientEventDocument;
import io.xcodebn.analyticsservice.repository.PatientEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patients.events.PatientEvent;

import java.time.Instant;

@Slf4j
@Service
public class KafkaConsumer {

    private final PatientEventRepository patientEventRepository;

    public KafkaConsumer(PatientEventRepository patientEventRepository) {
        this.patientEventRepository = patientEventRepository;
    }

    @KafkaListener(topics = "patient", groupId = "analytics-service")
    public void consumeEvent(byte[] event) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            log.info("Received patient event {PatientId = {}, PatientName={}, PatientEmail = {}",
                    patientEvent.getPatientId(), patientEvent.getName(), patientEvent.getEmail());

            // Save to MongoDB
            PatientEventDocument document = PatientEventDocument.builder()
                    .eventType("PATIENT_EVENT")
                    .patientId(patientEvent.getPatientId())
                    .name(patientEvent.getName())
                    .email(patientEvent.getEmail())
                    .timestamp(Instant.now())
                    .source("patient-service")
                    .build();

            patientEventRepository.save(document);
            log.info("Successfully persisted patient event to MongoDB: {}", document.getId());

        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing Unable to parse event from bytes {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing event: {}", e.getMessage(), e);
        }
    }
}
