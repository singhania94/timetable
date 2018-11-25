package com.fab.timetable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fab.timetable.entity.Teacher;
import com.fab.timetable.entity.Teacher.Category;
import com.fab.timetable.helper.Constants;
import com.fab.timetable.repository.TeacherRepository;

@SpringBootApplication
public class TimetableApplication implements CommandLineRunner {

	@Autowired
	TeacherRepository teacherRepo;

	public static void main(String[] args) {
		SpringApplication.run(TimetableApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (!teacherRepo.existsById(Constants.DUMMY_TEACHER_NAME)) {
			Teacher t = new Teacher();
			t.name = Constants.DUMMY_TEACHER_NAME;
			t.category = Category.NONE;
			t.subjectId = Category.NONE.toString();
			t.sectionIds = null;
			teacherRepo.insert(t);
		}
	}
}
