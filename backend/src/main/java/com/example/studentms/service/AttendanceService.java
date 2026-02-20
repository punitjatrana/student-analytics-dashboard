package com.example.studentms.service;

import com.example.studentms.domain.*;
import com.example.studentms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
  private final AttendanceRepository attendanceRepository;
  private final CourseRepository courseRepository;
  private final StudentRepository studentRepository;

  public Attendance markAttendance(Long courseId, Long studentId, LocalDate date, boolean present) {
    Course course = courseRepository.findById(courseId).orElseThrow();
    Student student = studentRepository.findById(studentId).orElseThrow();
    Attendance attendance = attendanceRepository
        .findByCourseAndStudentAndSessionDate(course, student, date)
        .orElse(Attendance.builder()
            .course(course).student(student).sessionDate(date)
            .build());
    attendance.setPresent(present);
    return attendanceRepository.save(attendance);
  }

  public double getAttendancePercentage(Long courseId, Long studentId) {
    Course course = courseRepository.findById(courseId).orElseThrow();
    Student student = studentRepository.findById(studentId).orElseThrow();
    long total = attendanceRepository.countSessions(course, student);
    if (total == 0)
      return 0.0;
    long present = attendanceRepository.countPresentSessions(course, student);
    return (present * 100.0) / total;
  }

  public List<Attendance> getByCourse(Long courseId) {
    Course course = courseRepository.findById(courseId).orElseThrow();
    return attendanceRepository.findByCourse(course);
  }
}
