package com.example.studentms.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TrackEventRequest {

  @NotBlank
  private String featureName;
}

