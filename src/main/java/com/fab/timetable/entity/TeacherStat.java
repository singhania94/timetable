package com.fab.timetable.entity;

import com.fab.timetable.helper.Constants;

public class TeacherStat implements Comparable<TeacherStat> {
	public String name;
	public int currentCapacity = 0;
	public boolean[] isFree;
	
	public TeacherStat(String name) {
		this.name = name;
		isFree = new boolean[Constants.NUM_PERIODS];
		
		for (int i = 0; i < Constants.NUM_PERIODS; i++) {
			isFree[i] = true;
		}
	}
	
	@Override
    public boolean equals(Object o) { 
  
        if (o == this)
            return true; 
        if (!(o instanceof TeacherStat)) 
            return false; 
          
        TeacherStat ss = (TeacherStat) o; 
        return name.equals(ss.name);
    }

	@Override
	public int compareTo(TeacherStat ts) {
		if(currentCapacity != ts.currentCapacity)
			return currentCapacity - ts.currentCapacity;
		return 1;
	}
}
