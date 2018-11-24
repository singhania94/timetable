package com.fab.timetable.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fab.timetable.entity.MyClass;
import com.fab.timetable.service.ClassService;

@RestController
@RequestMapping("/class")
public class ClassController {
		
	@Autowired ClassService service;
	
	@PostMapping("/insert/{number}")
	public MyClass insert(@PathVariable String number) {
		return service.insert(number);
	}
	
	@PutMapping("/addSubject/{number}/{subject}")
	public MyClass addSubjectToClass(@PathVariable String number, @PathVariable String subject) {
		return service.addSubjectToClass(number, subject);
	}
	
	@GetMapping("/findAll")
	public List<MyClass> findAll() {
		return service.findAll();
	}
	
	@GetMapping("/findById/{number}")
	public MyClass findById(@PathVariable String number) {
		return service.findById(number);
	}

	@PutMapping("/deleteSubject/{number}/{subject}")
	public void deleteSubjectFromClass(@PathVariable String number, @PathVariable String subject) {
		service.deleteSubjectFromClass(number, subject);
	}
	
	@DeleteMapping("/delete/{number}")
	public void deleteById(@PathVariable String number) {
		service.deleteById(number);
	}
}
