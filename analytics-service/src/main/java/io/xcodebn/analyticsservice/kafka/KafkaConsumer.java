package io.xcodebn.analyticsservice.kafka;


import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patients.events.PatientEvent;

@Slf4j
@Service
public class KafkaConsumer {

    @KafkaListener(topics = "patient",groupId = "analytics-service")
    public void consumeEvent(byte[] event)  {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            // todo add analytics business logic
            log.info("Successfully received and parsed event {}", patientEvent);
            log.info("Received patient event {PatientId = {}, PatientName={}, PatientEmail = {}", patientEvent.getPatientId(), patientEvent.getName(), patientEvent.getEmail());
        }
        catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing Unable to parse event from bytes {}", e.getMessage());
        }
        catch (Exception e){
            log.error("Error deserializing event from bytes {}", e.getMessage());
        }
    }
}
