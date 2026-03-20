package edu.gcc.gravy;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Used to operate as an intermediary class for json conversion.
 */
public class JSONSection {
    public int credits;
    public String[] faculty;
    public boolean is_lab;
    public boolean is_open;
    public String location;
    public String name;
    public int number;
    public int open_seats;
    public String section;
    public String semester;
    public String subject;
    public List<time> times;
    public int total_seats;

    /**
     * Object to store timeslots from json
     */
    public class time {
        public String day;
        public String end_time;
        public String start_time;

        // Make the JSON times into TimeSlots.
        public TimeSlot toTimeSlot(){
            return new TimeSlot(LocalTime.parse(start_time), LocalTime.parse(end_time),
                    switch (day) {
                        case ("M"):
                            yield Set.of(Day.MONDAY);
                        case ("T"):
                            yield Set.of(Day.TUESDAY);
                        case ("W"):
                            yield Set.of(Day.WEDNESDAY);
                        case ("R"):
                            yield Set.of(Day.THURSDAY);
                        case ("F"):
                            yield Set.of(Day.FRIDAY);
                        default:
                            yield Set.of();
                    });
        }

        // make the time object from a TimeSlot
        public time(TimeSlot t){
            this.end_time = t.getEndTime().toString();
            this.start_time = t.getStartTime().toString();
            Day d = t.getDays().stream().iterator().next();
            this.day = switch (d) {
                case MONDAY:
                    yield "M";
                case TUESDAY:
                    yield "T";
                case WEDNESDAY:
                    yield "W";
                case THURSDAY:
                    yield "R";
                case FRIDAY:
                    yield "F";
            };
        }

    }


    /**
     * Converts the JSONSection object to a Section
     * @param allCourses - An arraylist that has all
     * @return a section
     */
    public Section toSection(ArrayList<Course> allCourses){
        ArrayList<TimeSlot> timeSlots = new ArrayList<>();
        for (int index = 0; index < times.size(); index++){
            timeSlots.add(times.get(index).toTimeSlot());
        }

        Course course = new Course(number, name, subject, credits, semester);
        boolean courseExists = false;
        // If course is equivalent to a pre-existing course, use the pre-existing course
        for (Course current : allCourses){
            if (course.getTitle().equals(current.getTitle())){
                if (course.getCourseID() == current.getCourseID()
                    && course.getDepartment().equals(current.getDepartment())
                    && course.getTerm().equals(current.getTerm())
                    && course.getCreditHours() == current.getCreditHours()
                ){
                    course = current;
                    courseExists = true;
                }
            }
        }
        if (!courseExists){
            allCourses.add(course);
        }
        Section newSect = new Section(course, section.charAt(0), new ArrayList<>(List.of(faculty)), total_seats, total_seats-open_seats,
                timeSlots, is_open, location);

        // Additional feature
        // Add the section to the courses list of sections
        // course.getSections().add(newSect);
        // No idea why, but this line kills the program.

        return newSect;
    }

    // Reverse process to make a JSONSection from a section
    public JSONSection(Section section1) {
        Course course = section1.getCourse();
        this.credits = course.getCreditHours();
        this.faculty = section1.getProfessors().toArray(new String[0]);
        this.is_lab = false; // Placeholder
        this.is_open = section1.isOpen();
        this.location = section1.getLocation();
        this.name = course.getTitle();
        this.number = course.getCourseID();
        this.open_seats = section1.getCapacity()-section1.getEnrolled();
        this.section = String.valueOf(section1.getSectionID());
        this.semester = course.getTerm();
        this.subject = course.getDepartment();
        this.times = new ArrayList<>();
        for (TimeSlot slot : section1.getTime()) {
            this.times.add(new time(slot));
        }
        this.total_seats = section1.getCapacity();
    }


}
