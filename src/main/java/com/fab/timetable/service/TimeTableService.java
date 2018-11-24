package com.fab.timetable.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fab.timetable.entity.MyClass;
import com.fab.timetable.entity.Section;
import com.fab.timetable.entity.Subject;
import com.fab.timetable.entity.SubjectStat;
import com.fab.timetable.entity.Teacher;
import com.fab.timetable.entity.Teacher.Category;
import com.fab.timetable.entity.TeacherStat;
import com.fab.timetable.helper.Constants;
import com.fab.timetable.helper.CustomException;
import com.fab.timetable.repository.SectionRepository;
import com.fab.timetable.repository.TeacherRepository;

@Service
public class TimeTableService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TimeTableService.class);

	@Autowired ClassService classService;
	@Autowired SectionService sectionService;
	@Autowired SubjectService subjectService;
	@Autowired TeacherService teacherService;
	
	@Autowired SectionRepository sectionRepo;
	@Autowired TeacherRepository teacherRepo;

	public void createTimeTable() {
		LOGGER.info("operation=createTimeTable;stat=start");
		isDataValid();

		List<MyClass> classes = classService.findAll();

		int[] shortfallPrimary = createTimeTableForCategory(classes.stream().filter(c -> c.isCategory().equals(Category.PRIMARY_TEACHER))
				.collect(Collectors.toSet()), teacherService.findByCategory(Category.PRIMARY_TEACHER));
		int[] shortfallGrad = createTimeTableForCategory(classes.stream()
				.filter(c -> c.isCategory().equals(Category.TRAINED_GRADUATE_TEACHER)).collect(Collectors.toSet()),
				teacherService.findByCategory(Category.TRAINED_GRADUATE_TEACHER));
		int[] shortfallPostGrad = createTimeTableForCategory(classes.stream().filter(c -> c.isCategory().equals(Category.POST_GRADUATE_TEACHER))
				.collect(Collectors.toSet()), teacherService.findByCategory(Category.POST_GRADUATE_TEACHER));
		
		calculateShortfall(shortfallPrimary, Category.PRIMARY_TEACHER);
		calculateShortfall(shortfallGrad, Category.TRAINED_GRADUATE_TEACHER);
		calculateShortfall(shortfallPostGrad, Category.POST_GRADUATE_TEACHER);
			
		LOGGER.info("operation=createTimeTable;stat=end");
	}

	public boolean isDataValid() {
		LOGGER.info("operation=validateDataForTimetable;");

		// subjects should be NUM_SUBJECTS in number for all classes.
		List<MyClass> classes = classService.findAll();
		List<MyClass> exClasses = classes.stream().filter(c -> c.subjects.size() != Constants.NUM_SUBJECTS)
				.collect(Collectors.toList());

		exClasses.forEach(c -> LOGGER.error(
				"object=class;operation=subjectCountValidation;" + "reason=invalidValue;countSubjects={}",
				c.subjects.size()));

		if (!exClasses.isEmpty())
			throw new CustomException(
					"object=timeTable;operation=validateDataForTimetable;reason=invalidSubjectCount;");

		LOGGER.info("operation=validateDataForTimetable;stat=subjectCountValid");

		// validate teacher count per category per subject.
		String exMsg = "";

		long req1 = validateTeacherCapacityForCategory(classes.stream()
				.filter(c -> c.isCategory().equals(Category.PRIMARY_TEACHER)).collect(Collectors.toSet()),
				teacherService.findByCategory(Category.PRIMARY_TEACHER));

		if (req1 != 0) {
			LOGGER.error("category=" + Category.PRIMARY_TEACHER + ";shortfall=" + req1 + ";");
			exMsg += "category=" + Category.PRIMARY_TEACHER + ";shortfall=" + req1 + ";";
		}

		long req2 = validateTeacherCapacityForCategory(classes.stream()
				.filter(c -> c.isCategory().equals(Category.TRAINED_GRADUATE_TEACHER)).collect(Collectors.toSet()),
				teacherService.findByCategory(Category.TRAINED_GRADUATE_TEACHER));

		if (req2 != 0) {
			LOGGER.error("category=" + Category.TRAINED_GRADUATE_TEACHER + ";shortfall=" + req2 + ";");
			exMsg += "category=" + Category.TRAINED_GRADUATE_TEACHER + ";shortfall=" + req2 + ";";
		}

		long req3 = validateTeacherCapacityForCategory(classes.stream()
				.filter(c -> c.isCategory().equals(Category.POST_GRADUATE_TEACHER)).collect(Collectors.toSet()),
				teacherService.findByCategory(Category.POST_GRADUATE_TEACHER));

		if (req3 != 0) {
			LOGGER.error("category=" + Category.POST_GRADUATE_TEACHER + ";shortfall=" + req3 + ";");
			exMsg += "category=" + Category.POST_GRADUATE_TEACHER + ";shortfall=" + req3 + ";";
		}

		if (!exMsg.isEmpty())
			throw new CustomException(exMsg);

		LOGGER.info("operation=validateDataForTimetable;stat=countTeacherForAllPoolValid");
		return true;
	}

	private int[] createTimeTableForCategory(Set<MyClass> classes, List<Teacher> teachers) {

		// Get a list of all subjects in the category
		List<Subject> subjects = classes.stream()
				.map(c -> c.subjects)
				.flatMap(sl -> sl.stream())
				.distinct()
				.collect(Collectors.toList());

		// Order and arrange subjects into subjectStat in the order of priority as defined in SubjetStat.
		Set<SubjectStat> subjectsStat = new TreeSet<>();
		for (Subject subject : subjects) {
			long teacherCount = teachers.stream()
					.filter(t -> t.subjectId.equals(subject.name))
					.distinct()
					.count();
			long sectionCount = classes.stream()
					.filter(c -> c.subjects.contains(subject))
					.map(c -> c.sections)
					.flatMap(s -> s.stream())
					.count();
			
			boolean added = subjectsStat.add(new SubjectStat(subject.name, teacherCount, sectionCount));
			System.out.println(subject.name + added);
		}
		
		// create special teachers stat set
		Set<TeacherStat> subjectSpecialTeachersStat = teachers.stream()
				.filter(t -> t.subjectId.equals(Constants.DEFAULT_SUBJECT))
				.map(t -> new TeacherStat(t.name))
				.collect(Collectors.toSet());

		/*
		 * Now comes the allocation. Talk about just Monday for now. In this part of the
		 * code, we allot slots to the subjects with highest priority first. The
		 * selected subject is then allocated to the section periods in best slot method
		 * in subjectStat to incorporate minimum overlap.
		 */
		int subjectIndexInClass, indexInTimeTable;
		int[] periodShortfall = new int[Constants.NUM_PERIODS];
		Set<MyClass> subjectClasses = new HashSet<>();
		Set<Section> subjectSections = new HashSet<>();
		Set<TeacherStat> subjectTeachersStat = new HashSet<>();
		
		// For subject (with highest priority first) in the list of all subjects for that category.
		for (SubjectStat subjectStat : subjectsStat) {
			
			Subject subject = subjectService.findById(subjectStat.name);
			subjectClasses = subject.classIds.stream()
					.map(cid -> classService.findById(cid))
					.distinct()
					.collect(Collectors.toSet());

			subjectTeachersStat = teachers.stream()
					.filter(t -> t.subjectId.equals(subject.name))
					.map(t -> new TeacherStat(t.name))
					.collect(Collectors.toSet());

			// For all the classes in the category that teach the subject
			for (MyClass subjectClass : subjectClasses) {
				
				subjectIndexInClass = subjectClass.subjects.indexOf(subject);
				subjectSections = subjectClass.sections;

				// For all the sections in the class
				for (Section subjectSection : subjectSections) {
					
					indexInTimeTable = subjectStat.findBestSlotForSubject(subjectSection.timeTable[0]);
					TeacherStat subjectTeacherStat = findBestTeacherForSection(subjectTeachersStat,
							subjectSpecialTeachersStat, indexInTimeTable);
					
					subjectStat.currentSlotAllotment[indexInTimeTable]++;
					for(int i = 0; i < Constants.NUM_WORK_DAYS; i++)	
						subjectSection.timeTable[i][i + indexInTimeTable] = subjectIndexInClass;
					subjectSection = sectionRepo.save(subjectSection);

					if (subjectTeacherStat != null) {
						subjectTeacherStat.isFree[indexInTimeTable] = false;
						subjectTeacherStat.currentCapacity++;

						subjectSection = sectionService.addTeacherToSection(subjectSection.name,
								subjectTeacherStat.name, subjectIndexInClass);
					} else
						periodShortfall[indexInTimeTable]++;
				}
			}
		} // allocate for other days also
		return periodShortfall;
	}

	private TeacherStat findBestTeacherForSection(Set<TeacherStat> subjectTeachersStat,
			Set<TeacherStat> subjectSpecialTeachersStat, int indexInTimeTable) {

		Optional<TeacherStat> teacherStat = subjectTeachersStat.stream()
				.filter(ts -> ts.currentCapacity < Constants.TEACHER_CAPACITY).filter(ts -> ts.isFree[indexInTimeTable])
				.sorted().findFirst();

		if (teacherStat.isPresent())
			return teacherStat.get();
		else {
			teacherStat = subjectSpecialTeachersStat.stream()
					.filter(ts -> ts.currentCapacity < Constants.TEACHER_CAPACITY)
					.filter(ts -> ts.isFree[indexInTimeTable]).sorted().findFirst();
			if (teacherStat.isPresent())
				return teacherStat.get();
		}
		return null;
	}

	private long validateTeacherCapacityForCategory(Set<MyClass> classes, List<Teacher> teachers) {
		long shortfall = 0;

		Set<Subject> subjects = classes.stream().map(c -> c.subjects).flatMap(sl -> sl.stream())
				.collect(Collectors.toSet());

		for (Subject subject : subjects) {
			long teacherCount = teachers.stream().filter(t -> t.subjectId.equals(subject.name)).distinct().count();
			long sectionCount = classes.stream().filter(c -> c.subjects.contains(subject)).map(c -> c.sections)
					.flatMap(s -> s.stream()).count();
			if (teacherCount * Constants.TEACHER_CAPACITY < sectionCount)
				shortfall += sectionCount - teacherCount * Constants.TEACHER_CAPACITY;
		}

		long superTeacherCount = teachers.stream().filter(t -> t.subjectId.equals(Constants.DEFAULT_SUBJECT)).distinct()
				.count();

		if (superTeacherCount * Constants.TEACHER_CAPACITY < shortfall) {
			long extraReq = shortfall / Constants.TEACHER_CAPACITY - superTeacherCount
					+ ((shortfall % Constants.TEACHER_CAPACITY == 0) ? 0 : 1);
			return extraReq;
		}
		return 0;
	}
	
	private void calculateShortfall(int[] shortfall, Category category) {
		int colMaxCount = 0, totalCount = 0;
		for (int i = 0; i < Constants.NUM_PERIODS; i++) {
			if (colMaxCount < shortfall[i])
				colMaxCount = shortfall[i];
			totalCount += shortfall[i];
		} 
		
		if (colMaxCount != 0) {
			if (colMaxCount > totalCount * Constants.TEACHER_CAPACITY) {
				LOGGER.warn(colMaxCount + " more teachers required in " + category);
			} else 
				LOGGER.warn((totalCount / Constants.TEACHER_CAPACITY + (totalCount % Constants.TEACHER_CAPACITY == 0 ? 0 : 1)) +
						" more teachers required in " + category);
		}
	}
}
