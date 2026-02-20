package com.example.studentms.repository;

import com.example.studentms.domain.Attendance;
import com.example.studentms.domain.Course;
import com.example.studentms.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
  Optional<Attendance> findByCourseAndStudentAndSessionDate(Course course, Student student, LocalDate sessionDate);

  List<Attendance> findByCourseAndStudent(Course course, Student student);

  List<Attendance> findByCourse(Course course);

  @Query("select count(a) from Attendance a where a.course = :course and a.student = :student")
  long countSessions(@Param("course") Course course, @Param("student") Student student);

  @Query("select count(a) from Attendance a where a.course = :course and a.student = :student and a.present = true")
  long countPresentSessions(@Param("course") Course course, @Param("student") Student student);
}
