package com.fab.timetable.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fab.timetable.helper.Constants;

@Document(collection="section")
public class Section {
	
	@Id public String name;
	public String classId;
	@DBRef public Teacher[] teachers;
	public int[][] timeTable;
	
	public Section() {
		teachers = new Teacher[Constants.NUM_SUBJECTS];
		timeTable = new int[Constants.NUM_WORK_DAYS][Constants.NUM_PERIODS];
		for(int i = 0; i < Constants.NUM_WORK_DAYS; i++) {
			for(int j = 0; j < Constants.NUM_PERIODS; j++)
				timeTable[i][j] = -1;
		}
	}
	
	@Override
    public boolean equals(Object o) { 
  
        if (o == this)
            return true; 
        if (!(o instanceof Section)) 
            return false; 
          
        Section s = (Section) o; 
        return name.equals(s.name);
    } 
}
