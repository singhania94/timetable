package com.fab.timetable.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fab.timetable.entity.Teacher.Category;

@Document(collection="class")
public class MyClass {

	@Id public String number;
	@DBRef public Set<Section> sections;
	@DBRef public List<Subject> subjects;
	
	public MyClass() {
		sections = new HashSet<>();
		subjects = new ArrayList<>();
	}
	
	public Category isCategory() {
		int n = Integer.parseInt(number);
		if(n <= 5)
			return Category.PRIMARY_TEACHER;
		else if(n <= 8) 
			return Category.TRAINED_GRADUATE_TEACHER;
		return Category.POST_GRADUATE_TEACHER;
	}
	
	@Override
    public boolean equals(Object o) { 
  
        if (o == this)
            return true; 
        if (!(o instanceof MyClass)) 
            return false; 
          
        MyClass c = (MyClass) o; 
        return number.equals(c.number);
    } 
}
