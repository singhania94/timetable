package com.fab.timetable.service;

import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fab.timetable.entity.Subject;
import com.fab.timetable.entity.Teacher;
import com.fab.timetable.entity.Teacher.Category;
import com.fab.timetable.helper.Constants;
import com.fab.timetable.helper.CustomException;
import com.fab.timetable.repository.SectionRepository;
import com.fab.timetable.repository.SubjectRepository;
import com.fab.timetable.repository.TeacherRepository;

@Service
public class TeacherService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeacherService.class);
			
	@Autowired TeacherRepository repo;
	@Autowired SectionRepository sectionRepo;
	@Autowired SubjectRepository subjectRepo;
	
	public Teacher insert(Teacher t) {
		LOGGER.info("object=teacher;operation=insert;teacherName={};teacherCategory={}", t.name, t.category.toString());
		if(repo.existsById(t.name))
			throw new CustomException("object=teacher;name=" + t.name + ";reason=alreadyexists;");
		if(!subjectRepo.existsById(t.subjectId)) {
			Subject s = new Subject();
			s.name = t.subjectId;
			subjectRepo.save(s);
		}
		t.sectionIds = new HashSet<>();
		return repo.insert(t);
	}
	
	public Teacher assignSubjectToTeacher(String name, String subject) {
		Teacher t = repo.findById(name)
				.orElseThrow(() -> new CustomException("object=teacher;name=" + name + ";reason=notexists;"));
		if(!subjectRepo.existsById(subject)) {
			Subject s = new Subject();
			s.name = subject;
			subjectRepo.save(s);
		}
		
		t.subjectId = subject;
		return repo.save(t);
	}
	
	public List<Teacher> findAll() {
		LOGGER.info("object=teacher;operation=findAll;");
		return repo.findAll();
	}
	
	public Teacher findById(String name) {
		LOGGER.info("object=teacher;operation=findById;name={}", name);
		return repo.findById(name)
				.orElseThrow(() -> new CustomException("object=teacher;name=" + name + ";reason=notexists;"));
	}
	
	public List<Teacher> findByCategory(Category c) {
		LOGGER.info("object=teacher;operation=findByCategory;category={}", c.toString());
		return repo.findByCategory(c);
	}
	
	public void deleteSubjectFromTeacher(String name, String subject) {
		Teacher t = repo.findById(name).orElse(new Teacher());
		t.subjectId = Constants.DEFAULT_SUBJECT;
		repo.save(t);
	}
	
	public void deleteById(String name) {
		LOGGER.info("object=teacher;operation=deleteByName;name={}", name);
		Teacher t = repo.findById(name).orElse(new Teacher());

		t.sectionIds.stream()
			.map(sid -> sectionRepo.findById(sid).get())
			.peek(s -> {
				for (int i = 0; i < Constants.NUM_PERIODS; i++) {
					if (s.teachers[i].name.equals(name))
						s.teachers[i] = null;
				}
			})
			.forEach(s -> sectionRepo.save(s));
		repo.deleteById(name);
	}
}
