package com.fab.timetable.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="subject")
public class Subject {

	@Id public String name;
	public Set<String> classIds;
	
	public Subject() {
		classIds = new HashSet<>();
	}
	
	@Override
    public boolean equals(Object o) { 
  
        if (o == this)
            return true; 
        if (!(o instanceof Subject)) 
            return false; 
          
        Subject s = (Subject) o; 
        return name.equals(s.name);
    } 
}
