package com.fab.timetable.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fab.timetable.entity.Section;

public interface SectionRepository extends MongoRepository<Section, String>{
}
