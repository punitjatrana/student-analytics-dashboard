package com.example.studentms.web;

import com.example.studentms.domain.Attendance;
import com.example.studentms.service.AttendanceService;
import com.example.studentms.web.dto.AttendanceMarkRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
  private final AttendanceService attendanceService;

  @PostMapping("/mark")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public Attendance mark(@RequestBody @Valid AttendanceMarkRequest req) {
    return attendanceService.markAttendance(req.getCourseId(), req.getStudentId(), req.getDate(), req.getPresent());
  }

  @GetMapping("/percentage")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
  public double percentage(@RequestParam Long courseId, @RequestParam Long studentId) {
    return attendanceService.getAttendancePercentage(courseId, studentId);
  }

  @GetMapping("/by-course/{courseId}")
  @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
  public List<Attendance> byCourse(@PathVariable Long courseId) {
    return attendanceService.getByCourse(courseId);
  }
}
