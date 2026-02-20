package com.example.studentms.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "feature_clicks")
public class FeatureClick {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "feature_name", nullable = false, length = 100)
  private String featureName;

  @Column(name = "clicked_at", nullable = false)
  private Instant clickedAt;
}

