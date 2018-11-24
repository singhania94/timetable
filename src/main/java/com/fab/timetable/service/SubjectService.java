package com.fab.timetable.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fab.timetable.entity.Subject;
import com.fab.timetable.helper.CustomException;
import com.fab.timetable.repository.ClassRepository;
import com.fab.timetable.repository.SubjectRepository;
import com.fab.timetable.repository.TeacherRepository;

@Service
public class SubjectService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubjectService.class);
	
	@Autowired SubjectRepository repo;
	@Autowired ClassRepository classRepo;
	@Autowired TeacherRepository teacherRepo;
	
	public Subject insert(String name) {
		LOGGER.info("object=subject;operation=insert;subject={};", name);
		if(repo.existsById(name))
			throw new CustomException("object=subject;name=" + name + ";reason=alreadyexists;");
		Subject s = new Subject();
		s.name = name;
		return repo.insert(s);
	}
	
	public List<Subject> findAll() {
		LOGGER.info("object=subject;operation=findAll;");
		return repo.findAll();
	}
	
	public Subject findById(String name) {
		LOGGER.info("object=teacher;operation=findById;name={}", name);
		return repo.findById(name)
				.orElseThrow(() -> new CustomException("object=subject;name=" + name + ";reason=notexists;"));
	}
	
	public void deleteById(String name) {
		LOGGER.info("object=subject;operation=deleteByName;name={}", name);
		Subject s = repo.findById(name)
						.orElse(new Subject());
		
		teacherRepo.findBySubjectId(name).stream()
			.peek(t -> t.subjectId = "")
			.forEach(t -> teacherRepo.save(t));
		
		s.classIds.stream()
			.map(cid -> classRepo.findById(cid).get())
			.peek(c -> c.subjects.removeIf(ss -> ss.name.equals(name)))
			.forEach(c -> classRepo.save(c));

		repo.deleteById(name);
	}
}
