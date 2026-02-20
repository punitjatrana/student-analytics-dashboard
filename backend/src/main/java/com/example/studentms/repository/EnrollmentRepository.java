package com.example.studentms.repository;

import com.example.studentms.domain.Course;
import com.example.studentms.domain.Enrollment;
import com.example.studentms.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
  Optional<Enrollment> findByStudentAndCourse(Student student, Course course);

  List<Enrollment> findByCourse(Course course);

  List<Enrollment> findByStudent(Student student);
}
