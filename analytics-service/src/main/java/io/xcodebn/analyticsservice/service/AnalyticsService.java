package io.xcodebn.analyticsservice.service;

import io.xcodebn.analyticsservice.model.DailyMetricsDocument;
import io.xcodebn.analyticsservice.model.PatientEventDocument;
import io.xcodebn.analyticsservice.repository.DailyMetricsRepository;
import io.xcodebn.analyticsservice.repository.PatientEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
public class AnalyticsService {

    private final PatientEventRepository patientEventRepository;
    private final DailyMetricsRepository dailyMetricsRepository;

    public AnalyticsService(
            PatientEventRepository patientEventRepository,
            DailyMetricsRepository dailyMetricsRepository
    ) {
        this.patientEventRepository = patientEventRepository;
        this.dailyMetricsRepository = dailyMetricsRepository;
    }

    /**
     * Get total patient event count
     */
    public long getTotalPatientEvents() {
        return patientEventRepository.count();
    }

    /**
     * Get events for a specific patient
     */
    public List<PatientEventDocument> getPatientEvents(String patientId) {
        return patientEventRepository.findByPatientId(patientId);
    }

    /**
     * Get patient event count for a specific date range
     */
    public long getPatientEventCount(LocalDate startDate, LocalDate endDate) {
        Instant start = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        return patientEventRepository.findByTimestampBetween(start, end).size();
    }

    /**
     * Get daily metrics for a specific date
     */
    public DailyMetricsDocument getDailyMetrics(LocalDate date) {
        return dailyMetricsRepository.findByDate(date)
                .orElseGet(() -> calculateDailyMetrics(date));
    }

    /**
     * Get metrics for a date range
     */
    public List<DailyMetricsDocument> getMetricsForDateRange(LocalDate startDate, LocalDate endDate) {
        return dailyMetricsRepository.findByDateBetween(startDate, endDate);
    }

    /**
     * Get last 30 days of metrics
     */
    public List<DailyMetricsDocument> getLast30DaysMetrics() {
        return dailyMetricsRepository.findTop30ByOrderByDateDesc();
    }

    /**
     * Calculate and cache daily metrics for a specific date
     */
    private DailyMetricsDocument calculateDailyMetrics(LocalDate date) {
        log.info("Calculating daily metrics for date: {}", date);

        Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        Long newPatients = patientEventRepository.countByEventTypeAndTimestampBetween(
                "PATIENT_EVENT", start, end
        );

        DailyMetricsDocument metrics = DailyMetricsDocument.builder()
                .date(date)
                .totalPatients(patientEventRepository.count())
                .newPatients(newPatients)
                .updatedPatients(0L)  // Can be enhanced later
                .totalLogins(0L)      // Will populate when we add auth events
                .failedLogins(0L)
                .uniqueUsers(0L)
                .lastUpdated(System.currentTimeMillis())
                .build();

        // Save to cache for future queries
        return dailyMetricsRepository.save(metrics);
    }

    /**
     * Refresh metrics for today
     */
    public DailyMetricsDocument refreshTodayMetrics() {
        LocalDate today = LocalDate.now();
        dailyMetricsRepository.findByDate(today).ifPresent(metrics ->
                dailyMetricsRepository.delete(metrics)
        );
        return calculateDailyMetrics(today);
    }
}
