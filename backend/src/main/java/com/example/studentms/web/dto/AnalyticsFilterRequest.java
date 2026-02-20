package com.example.studentms.web.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class AnalyticsFilterRequest {
  private Instant start;
  private Instant end;
  private String ageGroup; // "<18", "18-40", ">40"
  private String gender;   // "Male", "Female", "Other"
  private String featureName; // for line chart
}

