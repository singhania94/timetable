package com.fab.timetable.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fab.timetable.entity.Subject;
import com.fab.timetable.service.SubjectService;

@RestController
@RequestMapping("/subject")
public class SubjectController {

	@Autowired SubjectService service;
	
	@PostMapping("/insert/{name}")
	public Subject insert(@PathVariable String name) {
		return service.insert(name);
	}
	
	@GetMapping("/findAll")
	public List<Subject> findAll() {
		return service.findAll();
	}
	
	@GetMapping("/findById/{name}")
	public Subject findById(@PathVariable String name) {
		return service.findById(name);
	}
	
	@DeleteMapping("/delete/{name}")
	public void deleteById(@PathVariable String name) {
		service.deleteById(name);
	}
}
