package com.fab.timetable.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fab.timetable.helper.Constants;

@Document(collection="teacher")
public class Teacher {
	
	public enum Category {
		PRIMARY_TEACHER,
		TRAINED_GRADUATE_TEACHER,
		POST_GRADUATE_TEACHER,
		NONE
	}
	
	@Id public String name;
	@Indexed public Category category;
	public Set<String> sectionIds;
	public String subjectId;
	
	public Teacher() {
		sectionIds = new HashSet<>();
		subjectId = Constants.DEFAULT_SUBJECT;
	}
	
	@Override
    public boolean equals(Object o) { 
  
        if (o == this)
            return true; 
        if (!(o instanceof Teacher)) 
            return false; 
          
        Teacher c = (Teacher) o; 
        return name.equals(c.name);
    } 
}
