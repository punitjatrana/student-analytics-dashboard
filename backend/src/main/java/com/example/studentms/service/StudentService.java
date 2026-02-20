package com.example.studentms.service;

import com.example.studentms.domain.Student;
import com.example.studentms.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {
  private final StudentRepository studentRepository;

  public List<Student> findAll() {
    return studentRepository.findAll();
  }

  public Student create(Student s) {
    return studentRepository.save(s);
  }

  public Student update(Long id, Student s) {
    Student existing = studentRepository.findById(id).orElseThrow();
    existing.setRollNo(s.getRollNo());
    existing.setFirstName(s.getFirstName());
    existing.setLastName(s.getLastName());
    existing.setEmail(s.getEmail());
    return studentRepository.save(existing);
  }

  public void delete(Long id) {
    studentRepository.deleteById(id);
  }
}
