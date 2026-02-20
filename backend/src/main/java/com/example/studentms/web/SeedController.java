package com.example.studentms.web;

import com.example.studentms.domain.FeatureClick;
import com.example.studentms.domain.Role;
import com.example.studentms.domain.User;
import com.example.studentms.repository.FeatureClickRepository;
import com.example.studentms.repository.RoleRepository;
import com.example.studentms.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class SeedController {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final FeatureClickRepository featureClickRepository;
  private final PasswordEncoder passwordEncoder;

  public SeedController(UserRepository userRepository,
                        RoleRepository roleRepository,
                        FeatureClickRepository featureClickRepository,
                        PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.featureClickRepository = featureClickRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/seed")
  public ResponseEntity<String> seed() {
    if (userRepository.findByUsername("user1").isPresent()) {
      return ResponseEntity.ok("Database already seeded. Use username: user1, password: password1");
    }

    if (roleRepository.findByName("ADMIN").isEmpty()) {
      roleRepository.save(Role.builder().name("ADMIN").build());
    }
    if (roleRepository.findByName("TEACHER").isEmpty()) {
      roleRepository.save(Role.builder().name("TEACHER").build());
    }
    if (roleRepository.findByName("STUDENT").isEmpty()) {
      roleRepository.save(Role.builder().name("STUDENT").build());
    }

    Role studentRole = roleRepository.findByName("STUDENT").orElseThrow();
    String[] genders = {"Male", "Female", "Other"};
    Random random = new Random();

    for (int i = 1; i <= 10; i++) {
      User user = User.builder()
          .username("user" + i)
          .password(passwordEncoder.encode("password" + i))
          .fullName("User " + i)
          .age(15 + random.nextInt(40))
          .gender(genders[random.nextInt(genders.length)])
          .enabled(true)
          .build();
      user.getRoles().add(studentRole);
      userRepository.save(user);
    }

    List<User> users = userRepository.findAll();
    String[] features = {"date_filter", "gender_filter", "age_filter", "bar_chart_click", "line_chart_zoom"};
    LocalDate today = LocalDate.now();
    LocalDate startDate = today.minusDays(30);

    for (User user : users) {
      for (int i = 0; i < 10; i++) {
        LocalDate day = startDate.plusDays(random.nextInt(31));
        Instant timestamp = day.atTime(random.nextInt(24), random.nextInt(60)).toInstant(ZoneOffset.UTC);
        FeatureClick click = FeatureClick.builder()
            .user(user)
            .featureName(features[random.nextInt(features.length)])
            .clickedAt(timestamp)
            .build();
        featureClickRepository.save(click);
      }
    }

    return ResponseEntity.ok("Seeded! Use username: user1, password: password1");
  }
}
