package com.fab.timetable.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fab.timetable.entity.MyClass;
import com.fab.timetable.entity.Subject;
import com.fab.timetable.helper.CustomException;
import com.fab.timetable.repository.ClassRepository;
import com.fab.timetable.repository.SectionRepository;
import com.fab.timetable.repository.SubjectRepository;

@Service
public class ClassService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClassService.class);
	
	@Autowired ClassRepository repo;
	@Autowired SectionRepository sectionRepo;
	@Autowired SectionService sectionService;
	@Autowired SubjectRepository subjectRepo;
	
	public MyClass insert(String number) {
		LOGGER.info("object=class;operation=insert;number={};", number);
		if(repo.existsById(number))
			throw new CustomException("object=class;number=" + number + ";reason=alreadyexists;");
		
		try {
			int n = Integer.parseInt(number);
			if(n < 1 || n > 12)
				throw new CustomException("object=class;number=" + number + ";reason=invalidValueForNumber;");
		} catch (NumberFormatException ex) {
			throw new CustomException("object=class;number=" + number + ";reason=invalidValue;");
		}
		
		MyClass c = new MyClass();
		c.number = number;
		return repo.insert(c); 
	}
	
	public MyClass addSubjectToClass(String number, String subject) {
		LOGGER.info("object=class;operation=addSubjectToClass;subject={};", subject);
		MyClass c = repo.findById(number)
				.orElseThrow(() -> new CustomException("object=class;number=" + number + ";reason=notexists;"));
		
		Subject s;
		if(!subjectRepo.existsById(subject)) {
			s = new Subject();
			s.name = subject;
			s = subjectRepo.insert(s);
		} else {
			s = subjectRepo.findById(subject).get();
			if(c.subjects.contains(s))
				throw new CustomException("object=subject/class;number=" + number + ";subject=" 
												+ subject + "reason=subjectAlreadyExistsInClass;");
		}
		
		s.classIds.add(number);
		subjectRepo.save(s);
		c.subjects.add(s);
		return repo.save(c);
	}
	
	public List<MyClass> findAll() {
		LOGGER.info("object=class;operation=findAll;");
		return repo.findAll();
	}
	
	public MyClass findById(String number) {
		LOGGER.info("object=class;operation=findById;number={};", number);
		return repo.findById(number)
				.orElseThrow(() -> new CustomException("object=class;number=" + number + ";reason=notexists;"));	
	}
	
	public void deleteSubjectFromClass(String number, String subject) {
		LOGGER.info("object=class;operation=deleteSubjectFromClass;number={};", number);
		
		MyClass c = repo.findById(number)
				.orElse(new MyClass());
		
		c.subjects.stream()
			.filter(s -> s.name.equals(subject))
			.peek(s -> s.classIds.removeIf(cid -> cid.equals(number)))
			.forEach(s -> subjectRepo.save(s));
		
		c.subjects.removeIf(ss -> ss.name.equals(subject));
		repo.save(c);
	}	
	
	public void deleteById(String number) {
		LOGGER.info("object=class;operation=delete;number={};", number);
		MyClass c = repo.findById(number).orElse(new MyClass());
		
		c.sections.stream()
			.peek(s -> LOGGER.info("object=section;operation=delete;name={};", s.name))
			.forEach(s -> sectionService.deleteById(s.name));
		
		c.subjects.stream()
			.peek(s -> LOGGER.info("object=subject;operation=edit;name={};", s.name))
			.peek(s -> s.classIds.removeIf(cid -> cid.equals(number)))
			.forEach(s -> subjectRepo.save(s));
		
		repo.deleteById(number);
	}
}
