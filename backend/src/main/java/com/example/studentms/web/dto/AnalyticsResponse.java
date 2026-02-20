package com.example.studentms.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class AnalyticsResponse {

  private List<FeatureUsagePoint> barChart;
  private List<TimeSeriesPoint> lineChart;

  @Data
  @AllArgsConstructor
  public static class FeatureUsagePoint {
    private String featureName;
    private long totalClicks;
  }

  @Data
  @AllArgsConstructor
  public static class TimeSeriesPoint {
    private Instant timestamp;
    private long totalClicks;
  }
}

