package edu.gcc.gravy;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    void createSchedule() {
        Student student = new Student(1, "CS", "Math", "Senior");

        assertEquals(0, student.getSchedules().size());

        student.createSchedule("Fall Plan", "Fall 2026");

        assertEquals(1, student.getSchedules().size());
        assertNotNull(student.getSchedules().get(0));
    }

    @Test
    void removeSchedule() {
        Student student = new Student(1, "CS", "Math", "Senior");

        student.createSchedule("Fall Plan", "Fall 2026");
        Schedule schedule = student.getSchedules().get(0);

        student.removeSchedule(schedule);

        assertEquals(0, student.getSchedules().size());
    }

    @Test
    void addCompletedCourses() {
        Student student = new Student(1, "CS", "Math", "Senior");

        assertEquals(0, student.getCompletedCourses().size());

        // Using null keeps this test decoupled from Course implementation
        student.addCompletedCourses(null);

        assertEquals(1, student.getCompletedCourses().size());
        assertNull(student.getCompletedCourses().get(0));
    }

    @Test
    void getSchedules() {
        Student student = new Student(1, "CS", "Math", "Senior");

        List<Schedule> schedules = student.getSchedules();

        assertNotNull(schedules);
        assertEquals(0, schedules.size());
    }

    @Test
    void getYear() {
        Student student = new Student(1, "CS", "Math", "Senior");

        assertEquals("Senior", student.getYear());
    }

    @Test
    void getMajor() {
        Student student = new Student(1, "CS", "Math", "Senior");

        assertEquals("CS", student.getMajor());
    }

    @Test
    void getMinor() {
        Student student = new Student(1, "CS", "Math", "Senior");

        assertEquals("Math", student.getMinor());
    }

    @Test
    void getCompletedCourses() {
        Student student = new Student(1, "CS", "Math", "Senior");

        List<Course> courses = student.getCompletedCourses();

        assertNotNull(courses);
        assertEquals(0, courses.size());
    }
}