package com.estore.api.estoreapi.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.estore.api.estoreapi.models.Course;

public interface CourseRepository extends MongoRepository<Course, String>{
  List<Course> findByTitleContaining(String title);
}
