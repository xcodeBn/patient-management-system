package io.xcodebn.billingservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import io.xcodebn.billingservice.model.BillingAccount;
import io.xcodebn.billingservice.repository.BillingAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patients.events.PatientEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientEventConsumer {

    private final BillingAccountRepository billingAccountRepository;

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
        String patientId = patientEvent.getPatientId();

        // Check if billing account already exists
        if (billingAccountRepository.existsByPatientId(patientId)) {
            log.info("Billing account already exists for patient: {}", patientId);
            return;
        }

        // Create new billing account entity
        BillingAccount billingAccount = BillingAccount.builder()
                .patientId(patientId)
                .patientName(patientEvent.getName())
                .patientEmail(patientEvent.getEmail())
                .build();

        // Save to billing database
        billingAccountRepository.save(billingAccount);

        log.info("Billing account created successfully for patient: {} (ID: {})",
                patientEvent.getName(), patientId);
    }
}