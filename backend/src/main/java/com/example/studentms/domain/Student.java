package com.example.studentms.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "students")
public class Student {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50, name = "roll_no")
  private String rollNo;

  @Column(nullable = false, length = 100, name = "first_name")
  private String firstName;

  @Column(nullable = false, length = 100, name = "last_name")
  private String lastName;

  @Column(unique = true, length = 200)
  private String email;
}
