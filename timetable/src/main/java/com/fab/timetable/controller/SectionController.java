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

import com.fab.timetable.entity.Section;
import com.fab.timetable.service.SectionService;

@RestController
@RequestMapping("/section")
public class SectionController {

	@Autowired SectionService service;
	
	@PostMapping("/insert/{number}/{name}")
	public Section insert(@PathVariable String number, @PathVariable String name) {
		return service.insert(number, name);
	}
	
	@PutMapping("/addTeacher/{name}/{teacher}/{index}")
	public Section addTeacherToSection(@PathVariable String name, @PathVariable String teacher, @PathVariable int index) {
		return service.addTeacherToSection(name, teacher, index);
	}
	
	@GetMapping("/findAll")
	public List<Section> findAll() {
		return service.findAll();
	}
	
	@GetMapping("/findById/{name}")
	public Section findById(@PathVariable String name) {
		return service.findById(name);
	}
	
	@GetMapping("/gettt/{name}")
	public int[][] getTimeTable(@PathVariable String name) {
		return service.getTimeTableForSection(name);
	}

	@PutMapping("/deleteTeacher/{name}/{teacher}")
	public void deleteTeacherFromSection(@PathVariable String name, @PathVariable String teacher) {
		service.deleteTeacherFromSection(name, teacher);
	}
	
	@DeleteMapping("/delete/{name}")
	public void deleteById(@PathVariable String name) {
		service.deleteById(name);
	}
}
