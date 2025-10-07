package io.xcodebn.analyticsservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "daily_metrics")
public class DailyMetricsDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private LocalDate date;

    // Patient metrics
    private Long totalPatients;
    private Long newPatients;
    private Long updatedPatients;

    // Login metrics (will add when we implement auth events)
    private Long totalLogins;
    private Long failedLogins;
    private Long uniqueUsers;

    // Additional metrics can be stored as flexible map
    private Map<String, Object> additionalMetrics;

    private Long lastUpdated;  // Timestamp
}
