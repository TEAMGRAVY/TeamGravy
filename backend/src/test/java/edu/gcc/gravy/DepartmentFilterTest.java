package edu.gcc.gravy;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentFilterTest {

    // Helper method to construct sections for testing
    private Section makeSection(int id, String title, String department, char letter) {

        Course course = new Course(id, title, department, 3, "Fall");

        Section section = new Section();
        section.setCourse(course);
        section.setSectionID(letter);

        return section;
    }

    @Test
    void getType() {

        DepartmentFilter filter = new DepartmentFilter("COMP");

        // The filter type should always be DEPARTMENT
        assertEquals(FilterType.DEPARTMENT, filter.getType());
    }

    @Test
    void apply() {

        List<Section> sections = new ArrayList<>();

        sections.add(makeSection(141,"Programming I","COMP",'A'));
        sections.add(makeSection(210,"Data Structures","COMP",'B'));
        sections.add(makeSection(101,"Calculus I","MATH",'A'));

        DepartmentFilter filter = new DepartmentFilter("COMP");

        List<Section> results = filter.apply(sections);

        // Only the COMP sections should remain after filtering
        assertEquals(2, results.size());

        for (Section s : results) {
            assertEquals("COMP", s.getCourse().getDepartment());
        }
    }
}