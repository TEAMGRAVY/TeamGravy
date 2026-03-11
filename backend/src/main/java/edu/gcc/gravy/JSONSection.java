package edu.gcc.gravy;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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


    public class time {
        public String day;
        public String end_time;
        public String start_time;

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

    }

    public Section toSection(){
        TimeSlot[] timeSlots = new TimeSlot[times.size()];
        for (int index = 0; index < times.size(); index++){
            timeSlots[index] = times.get(index).toTimeSlot();
        }

        Course course = new Course(number, name, subject, credits, semester);

        return new Section(course, section.charAt(0), faculty[0], total_seats, total_seats-open_seats,
                timeSlots
        );
    }

}
