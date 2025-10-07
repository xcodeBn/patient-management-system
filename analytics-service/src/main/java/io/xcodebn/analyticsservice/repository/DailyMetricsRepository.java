package io.xcodebn.analyticsservice.repository;

import io.xcodebn.analyticsservice.model.DailyMetricsDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyMetricsRepository extends MongoRepository<DailyMetricsDocument, String> {

    Optional<DailyMetricsDocument> findByDate(LocalDate date);

    List<DailyMetricsDocument> findByDateBetween(LocalDate start, LocalDate end);

    List<DailyMetricsDocument> findTop30ByOrderByDateDesc();
}
