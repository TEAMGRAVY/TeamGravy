package edu.gcc.gravy;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfessorFilterTest {

    // Helper method to construct sections for testing.
    private Section makeSection(int id, String title, String department, char letter) {

        Course course = new Course(id, title, department, 3, "Fall");

        Section section = new Section();
        section.setCourse(course);
        section.setSectionID(letter);

        return section;
    }

    @Test
    void getType() {

        ProfessorFilter filter = new ProfessorFilter("COMP");

        // The filter type should always be PROFESSOR
        assertEquals(FilterType.PROFESSOR, filter.getType());
    }

}