package com.example.studentms.web;

import com.example.studentms.domain.FeatureClick;
import com.example.studentms.domain.User;
import com.example.studentms.repository.FeatureClickRepository;
import com.example.studentms.repository.UserRepository;
import com.example.studentms.web.dto.AnalyticsResponse;
import com.example.studentms.web.dto.TrackEventRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalyticsController {

  private final FeatureClickRepository featureClickRepository;
  private final UserRepository userRepository;

  @PostMapping("/track")
  public ResponseEntity<Void> track(
      @AuthenticationPrincipal UserDetails principal,
      @RequestBody @Valid TrackEventRequest req
  ) {
    Optional<User> userOpt = userRepository.findByUsername(principal.getUsername());
    if (userOpt.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    User user = userOpt.get();
    FeatureClick click = FeatureClick.builder()
        .user(user)
        .featureName(req.getFeatureName())
        .clickedAt(Instant.now())
        .build();
    featureClickRepository.save(click);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/analytics")
  public ResponseEntity<AnalyticsResponse> getAnalytics(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(required = false) String ageGroup,
      @RequestParam(required = false) String gender,
      @RequestParam(required = false) String featureName
  ) {
    Instant start = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
    Instant end = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

    Integer minAge = null;
    Integer maxAge = null;
    if (ageGroup != null) {
      switch (ageGroup) {
        case "<18" -> maxAge = 17;
        case "18-40" -> {
          minAge = 18;
          maxAge = 40;
        }
        case ">40" -> minAge = 41;
      }
    }

    String normalizedGender = gender;
    if (normalizedGender != null && normalizedGender.isBlank()) {
      normalizedGender = null;
    }

    var barAgg = featureClickRepository.aggregateByFeature(start, end, normalizedGender, minAge, maxAge);

    String featureForLine = featureName != null && !featureName.isBlank()
        ? featureName
        : (barAgg.isEmpty() ? null : barAgg.get(0).getFeatureName());

    List<AnalyticsResponse.TimeSeriesPoint> linePoints = List.of();
    if (featureForLine != null) {
      var tsAgg = featureClickRepository.aggregateTimeSeries(
          featureForLine, start, end, normalizedGender, minAge, maxAge
      );
      linePoints = tsAgg.stream()
          .map(t -> new AnalyticsResponse.TimeSeriesPoint(t.getBucketStart(), t.getTotalClicks()))
          .collect(Collectors.toList());
    }

    AnalyticsResponse resp = new AnalyticsResponse();
    resp.setBarChart(barAgg.stream()
        .map(b -> new AnalyticsResponse.FeatureUsagePoint(b.getFeatureName(), b.getTotalClicks()))
        .collect(Collectors.toList()));
    resp.setLineChart(linePoints);

    return ResponseEntity.ok(resp);
  }
}