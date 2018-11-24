package com.fab.timetable.service;

import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fab.timetable.entity.MyClass;
import com.fab.timetable.entity.Section;
import com.fab.timetable.entity.Teacher;
import com.fab.timetable.helper.Constants;
import com.fab.timetable.helper.CustomException;
import com.fab.timetable.repository.ClassRepository;
import com.fab.timetable.repository.SectionRepository;
import com.fab.timetable.repository.TeacherRepository;

@Service
public class SectionService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SectionService.class);
	
	@Autowired SectionRepository repo;
	@Autowired ClassRepository classRepo;
	@Autowired TeacherRepository teacherRepo;
	
	public Section insert(String classNumber, String name) {
		LOGGER.info("object=section;operation=insert;classNumber={};sectionName={};", classNumber, name);
		if(!classRepo.existsById(classNumber))
			throw new CustomException("object=class;number=" + classNumber + ";reason=notexists;");
		else if(repo.existsById(name))
			throw new CustomException("object=section;name=" + name + ";reason=alreadyexists;");
		
		MyClass c = classRepo.findById(classNumber).get();
		Section s = new Section();
		s.name = name;
		s.classId = classNumber;
		s.teachers = new Teacher[Constants.NUM_SUBJECTS];
		for(int i = 0; i < Constants.NUM_SUBJECTS; i++)
			s.teachers[i] = teacherRepo.findById(Constants.DUMMY_TEACHER_NAME).get();
		s = repo.insert(s);
		
		c.sections.add(s);
		classRepo.save(c);
		
		return s;
	}
	
	public Section addTeacherToSection(String name, String teacher, int index) {
		LOGGER.info("object=section;operation=addTeacherToSection;name={};teacher={}", name, teacher);
		Section s = repo.findById(name)
				.orElseThrow(() -> new CustomException("object=section;name=" + name + ";reason=notexists;"));
		Teacher t = teacherRepo.findById(teacher)
				.orElseThrow(() -> new CustomException("object=teacher;name=" + teacher + ";reason=notexists;"));
		
		if(!classRepo.findById(s.classId).get().isCategory().equals(t.category))
			throw new CustomException("object=teacher/section;name=" + name + ";teacher=" + teacher + ";reason=categoryMismatch;");
		
		t.sectionIds.add(name);
		teacherRepo.save(t);
		s.teachers[index] = t;
		return repo.save(s);
	}
	
	public List<Section> findAll() {
		LOGGER.info("object=section;operation=findAll;");
		return repo.findAll();
	}
	
	public Section findById(String name) {
		LOGGER.info("object=section;operation=findById;name={};", name);
		return repo.findById(name)
				.orElseThrow(() -> new CustomException("object=section;name=" + name + ";reason=notexists;"));
	}
	
	public void deleteTeacherFromSection(String name, String teacher) {
		LOGGER.info("object=section;operation=deleteTeacherFromSection;name={};teacher={}", name, teacher);
		Section s = repo.findById(name)
				.orElseThrow(() -> new CustomException("object=section;name=" + name + ";reason=notexists;"));
		
		Arrays.asList(s.teachers).stream()
			.map(o -> (Teacher) o)
			.filter(t -> t.name.equals(teacher))
			.peek(t -> t.sectionIds.removeIf(sid -> sid.equals(name)))
			.forEach(t -> teacherRepo.save(t));
		
		for(int i = 0; i < Constants.NUM_SUBJECTS; i++)
			if(s.teachers[i].name.equals(teacher))
				s.teachers[i] = null;
		repo.save(s);
	}
	
	public void deleteById(String name) {
		LOGGER.info("object=section;operation=delete;name={};", name);
		Optional<Section> s = repo.findById(name);
		
		if(s.isPresent()) {
			Arrays.asList(s.get().teachers).stream()
				.map(o -> (Teacher) o)
				.map(t -> teacherRepo.findById(t.name).get())
				.peek(t -> t.sectionIds.removeIf(ss -> ss.equals(name)))
				.forEach(t -> teacherRepo.save(t));
			
			MyClass c = classRepo.findById(s.get().classId).get();
			LOGGER.info("object=class;operation=removeSection;classNumber={};sectionName={}",c.number, s.get().name);
			c.sections.removeIf(ss -> ss.name.equals(s.get().name));
			
			classRepo.save(c);
			repo.deleteById(name);
		}
	}
	
	public int[][] getTimeTableForSection(String name) {
		LOGGER.info("object=section;operation=getTimeTable;name={};", name);
		Section s = repo.findById(name)
				.orElseThrow(() -> new CustomException("object=section;name=" + name + ";reason=notexists;"));
		MyClass c = classRepo.findById(s.classId).get();
		
		if(c.subjects.size() != Constants.NUM_SUBJECTS || s.teachers.length != Constants.NUM_SUBJECTS) {
			LOGGER.warn("object=section;operation=printTimeTable;name={};subjects={};teachers={};reason=invalidSize", 
								name, c.subjects.size(), s.teachers.length);
			return null;
		}
		
		for(int i = 0; i < Constants.NUM_WORK_DAYS; i++)
			for(int j = 0; j < Constants.NUM_PERIODS; j++)
				if(s.timeTable[i][j] == -1) {
					LOGGER.warn("object=section;operation=printTimeTable;name={};reason=timeTableNotGeneratedYet", name);
					return null;
				}
					
		for(int i = 0; i < Constants.NUM_WORK_DAYS; i++) {
			for(int j = 0; j < Constants.NUM_PERIODS; j++) {
				int index = s.timeTable[i][j];
				System.out.print(c.subjects.get(index) + "|" + s.teachers[index]);
			}System.out.println();
		} return s.timeTable;
	}
}
