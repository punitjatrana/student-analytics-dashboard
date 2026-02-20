package com.example.studentms.web;

import com.example.studentms.domain.Student;
import com.example.studentms.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {
  private final StudentService studentService;

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public List<Student> list() {
    return studentService.findAll();
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Student> create(@RequestBody @Valid Student s) {
    Student saved = studentService.create(s);
    return ResponseEntity.created(URI.create("/api/students/" + saved.getId())).body(saved);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public Student update(@PathVariable Long id, @RequestBody @Valid Student s) {
    return studentService.update(id, s);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    studentService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
