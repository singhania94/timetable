package com.fab.timetable.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fab.timetable.service.TimeTableService;

@RestController
@RequestMapping("/tt")
public class TimeTableController {
	
	@Autowired TimeTableService service;
	
	@GetMapping("/isValid")
	public boolean isDataValid() {
		return service.isDataValid();
	}
	
	@PutMapping("/createAll")
	public void createTimeTable() {
		service.createTimeTable();
	}
}
