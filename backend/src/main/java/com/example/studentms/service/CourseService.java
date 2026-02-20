package com.example.studentms.service;

import com.example.studentms.domain.Course;
import com.example.studentms.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
  private final CourseRepository courseRepository;

  public List<Course> findAll() {
    return courseRepository.findAll();
  }

  public Course create(Course c) {
    return courseRepository.save(c);
  }

  public Course update(Long id, Course c) {
    Course existing = courseRepository.findById(id).orElseThrow();
    existing.setCode(c.getCode());
    existing.setTitle(c.getTitle());
    existing.setTeacher(c.getTeacher());
    return courseRepository.save(existing);
  }

  public void delete(Long id) {
    courseRepository.deleteById(id);
  }
}
