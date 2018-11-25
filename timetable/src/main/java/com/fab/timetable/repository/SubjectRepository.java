package com.fab.timetable.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fab.timetable.entity.Subject;

public interface SubjectRepository extends MongoRepository<Subject, String>{
}
