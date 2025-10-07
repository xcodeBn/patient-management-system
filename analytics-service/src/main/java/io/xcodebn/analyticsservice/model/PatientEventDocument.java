package io.xcodebn.analyticsservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "patient_events")
public class PatientEventDocument {

    @Id
    private String id;

    private String eventType;  // PATIENT_CREATED, PATIENT_UPDATED, etc.
    private String patientId;
    private String name;
    private String email;
    private Instant timestamp;

    // Additional metadata
    private String source;  // Which service generated this event
}
