package com.fab.timetable.entity;

import com.fab.timetable.helper.Constants;

public class SubjectStat implements Comparable<SubjectStat> {
	
	public String name;
	public long teacherCount;
	public long sectionCount;
	public double priority;
	public int[] currentSlotAllotment;
	
	public SubjectStat(String name, long teacherCount, long sectionCount) {
		this.name = name;
		this.teacherCount = teacherCount;
		this.sectionCount = sectionCount;
		this.priority = ((double) sectionCount) /  teacherCount;
		currentSlotAllotment = new int[Constants.NUM_PERIODS];
	}
	
	@Override
    public boolean equals(Object o) { 
  
        if (o == this)
            return true; 
        if (!(o instanceof SubjectStat)) 
            return false; 
          
        SubjectStat ss = (SubjectStat) o; 
        return name.equals(ss.name);
    }

	@Override
	public int compareTo(SubjectStat ss) {
		if(priority != ss.priority)
			return (int) (ss.priority - priority);
		if(ss.sectionCount != sectionCount)
			return (int) (ss.sectionCount - sectionCount);
		return name.compareTo(ss.name);
	}

	public int findBestSlotForSubject(int[] tt) {
		int indexMin = -1, min = Integer.MAX_VALUE;
		for (int i = 0; i < Constants.NUM_PERIODS; i++) {
			if(tt[i] == -1)
				if(min > currentSlotAllotment[i]) {
					min = currentSlotAllotment[i];
					indexMin = i;
				}
		} return indexMin;
	}
}
