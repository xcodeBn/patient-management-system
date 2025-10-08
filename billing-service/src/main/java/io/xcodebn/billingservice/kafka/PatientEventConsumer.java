package io.xcodebn.billingservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import io.xcodebn.billingservice.dto.CreateBillingAccountRequest;
import io.xcodebn.billingservice.exception.BillingAccountAlreadyExistsException;
import io.xcodebn.billingservice.service.BillingAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patients.events.PatientEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientEventConsumer {

    private final BillingAccountService billingAccountService;

    @KafkaListener(topics = "patient", groupId = "billing-service")
    public void consumePatientEvent(byte[] event) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            log.info("Received patient event - PatientId: {}, Name: {}, Email: {}",
                    patientEvent.getPatientId(), patientEvent.getName(), patientEvent.getEmail());

            // Create billing account for this patient
            createBillingAccount(patientEvent);

        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing patient event: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing patient event: {}", e.getMessage(), e);
        }
    }

    private void createBillingAccount(PatientEvent patientEvent) {
        try {
            CreateBillingAccountRequest request = CreateBillingAccountRequest.builder()
                    .patientId(patientEvent.getPatientId())
                    .patientName(patientEvent.getName())
                    .patientEmail(patientEvent.getEmail())
                    .build();

            billingAccountService.createBillingAccount(request);
            log.info("Billing account created successfully for patient: {}", patientEvent.getPatientId());

        } catch (BillingAccountAlreadyExistsException e) {
            log.info("Billing account already exists for patient: {}", patientEvent.getPatientId());

        } catch (Exception e) {
            log.error("Failed to create billing account for patient: {}", patientEvent.getPatientId(), e);
            // In production, you might want to send to DLQ or retry mechanism
        }
    }
}