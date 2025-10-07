package io.xcodebn.analyticsservice.repository;

import io.xcodebn.analyticsservice.model.PatientEventDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PatientEventRepository extends MongoRepository<PatientEventDocument, String> {

    List<PatientEventDocument> findByPatientId(String patientId);

    List<PatientEventDocument> findByEventType(String eventType);

    List<PatientEventDocument> findByTimestampBetween(Instant start, Instant end);

    Long countByEventTypeAndTimestampBetween(String eventType, Instant start, Instant end);
}
