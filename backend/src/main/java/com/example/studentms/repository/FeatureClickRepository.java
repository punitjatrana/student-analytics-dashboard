package com.example.studentms.repository;

import com.example.studentms.domain.FeatureClick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface FeatureClickRepository extends JpaRepository<FeatureClick, Long> {

  @Query("""
      select fc.featureName as featureName, count(fc.id) as totalClicks
      from FeatureClick fc
      where fc.clickedAt between :start and :end
        and (:gender is null or fc.user.gender = :gender)
        and (:minAge is null or fc.user.age >= :minAge)
        and (:maxAge is null or fc.user.age <= :maxAge)
      group by fc.featureName
      """)
  List<FeatureUsageAggregation> aggregateByFeature(
      @Param("start") Instant start,
      @Param("end") Instant end,
      @Param("gender") String gender,
      @Param("minAge") Integer minAge,
      @Param("maxAge") Integer maxAge
  );

  @Query(value = """
      SELECT date_trunc('day', fc.clicked_at) AS bucket_start,
             COUNT(fc.id) AS total_clicks
      FROM feature_clicks fc
      JOIN users u ON u.id = fc.user_id
      WHERE fc.clicked_at BETWEEN :start AND :end
        AND fc.feature_name = :featureName
        AND (:gender IS NULL OR u.gender = :gender)
        AND (:minAge IS NULL OR u.age >= :minAge)
        AND (:maxAge IS NULL OR u.age <= :maxAge)
      GROUP BY 1
      ORDER BY 1
      """, nativeQuery = true)
  List<TimeSeriesAggregation> aggregateTimeSeries(
      @Param("featureName") String featureName,
      @Param("start") Instant start,
      @Param("end") Instant end,
      @Param("gender") String gender,
      @Param("minAge") Integer minAge,
      @Param("maxAge") Integer maxAge
  );

  interface FeatureUsageAggregation {
    String getFeatureName();
    long getTotalClicks();
  }

  interface TimeSeriesAggregation {
    Instant getBucketStart();
    long getTotalClicks();
  }
}