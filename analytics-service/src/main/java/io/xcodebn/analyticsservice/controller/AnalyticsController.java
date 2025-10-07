package io.xcodebn.analyticsservice.controller;

import io.xcodebn.analyticsservice.model.DailyMetricsDocument;
import io.xcodebn.analyticsservice.model.PatientEventDocument;
import io.xcodebn.analyticsservice.service.AnalyticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * GET /analytics/patients/total
     * Get total patient events count
     */
    @GetMapping("/patients/total")
    public ResponseEntity<Map<String, Object>> getTotalPatientEvents() {
        long count = analyticsService.getTotalPatientEvents();
        return ResponseEntity.ok(Map.of(
                "totalEvents", count,
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * GET /analytics/patients/{patientId}/events
     * Get all events for a specific patient
     */
    @GetMapping("/patients/{patientId}/events")
    public ResponseEntity<List<PatientEventDocument>> getPatientEvents(
            @PathVariable String patientId
    ) {
        List<PatientEventDocument> events = analyticsService.getPatientEvents(patientId);
        return ResponseEntity.ok(events);
    }

    /**
     * GET /analytics/patients/count?startDate=2025-01-01&endDate=2025-01-31
     * Get patient event count for date range
     */
    @GetMapping("/patients/count")
    public ResponseEntity<Map<String, Object>> getPatientEventCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        long count = analyticsService.getPatientEventCount(startDate, endDate);
        return ResponseEntity.ok(Map.of(
                "startDate", startDate,
                "endDate", endDate,
                "eventCount", count
        ));
    }

    /**
     * GET /analytics/daily?date=2025-10-06
     * Get daily metrics for a specific date
     */
    @GetMapping("/daily")
    public ResponseEntity<DailyMetricsDocument> getDailyMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DailyMetricsDocument metrics = analyticsService.getDailyMetrics(date);
        return ResponseEntity.ok(metrics);
    }

    /**
     * GET /analytics/daily/range?startDate=2025-01-01&endDate=2025-01-31
     * Get daily metrics for a date range
     */
    @GetMapping("/daily/range")
    public ResponseEntity<List<DailyMetricsDocument>> getDailyMetricsRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<DailyMetricsDocument> metrics = analyticsService.getMetricsForDateRange(startDate, endDate);
        return ResponseEntity.ok(metrics);
    }

    /**
     * GET /analytics/daily/last30
     * Get last 30 days of metrics
     */
    @GetMapping("/daily/last30")
    public ResponseEntity<List<DailyMetricsDocument>> getLast30DaysMetrics() {
        List<DailyMetricsDocument> metrics = analyticsService.getLast30DaysMetrics();
        return ResponseEntity.ok(metrics);
    }

    /**
     * POST /analytics/daily/refresh
     * Refresh today's metrics
     */
    @PostMapping("/daily/refresh")
    public ResponseEntity<DailyMetricsDocument> refreshTodayMetrics() {
        DailyMetricsDocument metrics = analyticsService.refreshTodayMetrics();
        return ResponseEntity.ok(metrics);
    }
}
