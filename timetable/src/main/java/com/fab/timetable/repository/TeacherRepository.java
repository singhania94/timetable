package com.fab.timetable.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fab.timetable.entity.Teacher;
import com.fab.timetable.entity.Teacher.Category;

public interface TeacherRepository extends MongoRepository<Teacher, String>{
	
	public List<Teacher> findByCategory(Category c);
	public List<Teacher> findBySubjectId(String id);
}
