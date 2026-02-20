package com.example.studentms.repository;

import com.example.studentms.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
  Optional<Student> findByRollNo(String rollNo);
}
