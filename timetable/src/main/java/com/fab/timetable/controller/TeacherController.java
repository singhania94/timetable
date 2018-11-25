package com.fab.timetable.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fab.timetable.entity.Teacher;
import com.fab.timetable.entity.Teacher.Category;
import com.fab.timetable.service.TeacherService;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

	@Autowired TeacherService service;
	
	@PostMapping("/insert")
	public Teacher insert(@RequestBody Teacher t) {
		return service.insert(t);
	}
	
	@PutMapping("/assignSubject/{name}/{subject}")
	public Teacher assignSubjectToTeacher(@PathVariable String name, @PathVariable String subject) {
		return service.assignSubjectToTeacher(name, subject);
	}
	
	@GetMapping("/findAll")
	public List<Teacher> findAll() {
		return service.findAll();
	}
	
	@GetMapping("/findByCategory/{category}")
	public List<Teacher> findByCategory(@PathVariable Category category) {
		return service.findByCategory(category);
	}
	
	@GetMapping("/findById/{name}")
	public Teacher findById(@PathVariable String name) {
		return service.findById(name);
	}
	
	@PutMapping("/deleteSubject/{name}/{subject}")
	public void deleteSubjectFromTeacher(@PathVariable String name, @PathVariable String subject) {
		service.deleteSubjectFromTeacher(name, subject);
	}
	
	@DeleteMapping("/delete/{name}")
	public void deleteById(@PathVariable String name) {
		service.deleteById(name);
	}
}
