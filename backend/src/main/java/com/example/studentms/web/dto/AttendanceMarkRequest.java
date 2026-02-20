package com.example.studentms.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AttendanceMarkRequest {
  @NotNull
  private Long courseId;
  @NotNull
  private Long studentId;
  @NotNull
  private LocalDate date;
  @NotNull
  private Boolean present;
}
