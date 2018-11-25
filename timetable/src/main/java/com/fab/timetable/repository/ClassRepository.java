package com.fab.timetable.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fab.timetable.entity.MyClass;

public interface ClassRepository extends MongoRepository<MyClass, String> {
}
