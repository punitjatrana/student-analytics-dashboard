package com.example.studentms.web;

import com.example.studentms.domain.Course;
import com.example.studentms.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
  private final CourseService courseService;

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public List<Course> list() {
    return courseService.findAll();
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public ResponseEntity<Course> create(@RequestBody @Valid Course c) {
    Course saved = courseService.create(c);
    return ResponseEntity.created(URI.create("/api/courses/" + saved.getId())).body(saved);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public Course update(@PathVariable Long id, @RequestBody @Valid Course c) {
    return courseService.update(id, c);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    courseService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
