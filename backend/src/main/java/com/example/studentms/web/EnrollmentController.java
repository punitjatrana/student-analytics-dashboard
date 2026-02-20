package com.example.studentms.web;

import com.example.studentms.domain.Enrollment;
import com.example.studentms.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
  private final EnrollmentService enrollmentService;

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public ResponseEntity<Enrollment> enroll(@RequestParam Long courseId, @RequestParam Long studentId) {
    Enrollment e = enrollmentService.enroll(courseId, studentId);
    return ResponseEntity.created(URI.create("/api/enrollments/" + e.getId())).body(e);
  }

  @DeleteMapping
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public ResponseEntity<Void> unenroll(@RequestParam Long courseId, @RequestParam Long studentId) {
    enrollmentService.unenroll(courseId, studentId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/by-course/{courseId}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public List<Enrollment> byCourse(@PathVariable Long courseId) {
    return enrollmentService.byCourse(courseId);
  }

  @GetMapping("/by-student/{studentId}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public List<Enrollment> byStudent(@PathVariable Long studentId) {
    return enrollmentService.byStudent(studentId);
  }
}
