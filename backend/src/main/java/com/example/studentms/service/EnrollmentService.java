package com.example.studentms.service;

import com.example.studentms.domain.Course;
import com.example.studentms.domain.Enrollment;
import com.example.studentms.domain.Student;
import com.example.studentms.repository.CourseRepository;
import com.example.studentms.repository.EnrollmentRepository;
import com.example.studentms.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
  private final EnrollmentRepository enrollmentRepository;
  private final CourseRepository courseRepository;
  private final StudentRepository studentRepository;

  public Enrollment enroll(Long courseId, Long studentId) {
    Course course = courseRepository.findById(courseId).orElseThrow();
    Student student = studentRepository.findById(studentId).orElseThrow();
    return enrollmentRepository.findByStudentAndCourse(student, course)
        .orElseGet(() -> enrollmentRepository.save(Enrollment.builder()
            .course(course).student(student).build()));
  }

  public void unenroll(Long courseId, Long studentId) {
    Course course = courseRepository.findById(courseId).orElseThrow();
    Student student = studentRepository.findById(studentId).orElseThrow();
    enrollmentRepository.findByStudentAndCourse(student, course)
        .ifPresent(enrollmentRepository::delete);
  }

  public List<Enrollment> byCourse(Long courseId) {
    Course course = courseRepository.findById(courseId).orElseThrow();
    return enrollmentRepository.findByCourse(course);
  }

  public List<Enrollment> byStudent(Long studentId) {
    Student student = studentRepository.findById(studentId).orElseThrow();
    return enrollmentRepository.findByStudent(student);
  }
}
