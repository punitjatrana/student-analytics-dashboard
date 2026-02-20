package com.example.studentms.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
  @NotBlank
  private String username;
  @NotBlank
  private String password;
  @NotBlank
  private String fullName;
  private Integer age;
  private String gender; // Male, Female, Other
}
