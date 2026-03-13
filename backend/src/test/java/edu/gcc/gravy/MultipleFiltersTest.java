package edu.gcc.gravy;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultipleFiltersTest {

    // Helper method to construct sections for testing.
    private Section makeSection(int id, String title, String department, char letter, int creditHours, String professor1, String professor2) {

        Course course = new Course(id, title, department, creditHours, "Fall");

        Section section = new Section();
        section.setCourse(course);
        section.setSectionID(letter);
        ArrayList<String> profs = new ArrayList<>();
        profs.add(professor1);
        profs.add(professor2);
        section.setProfessors(profs);

        return section;
    }

    @Test
    void apply2Filters() {

        List<Section> sections = new ArrayList<>();

        // Create many sections with varying information to be filtered through
        sections.add(makeSection(141,"Programming I","COMP",'A',3,"Hutchins",null));
        sections.add(makeSection(210,"Data Structures","COMP",'B',3,"Hutchins",null));
        sections.add(makeSection(101,"Calculus I","MATH",'A',4,"Flanders","Smith"));
        sections.add(makeSection(101,"Calculus II","MATH",'B',4,"Smith",null));
        sections.add(makeSection(101,"Advanced Programming","MATH",'B',3,"Dickinson",null));
        sections.add(makeSection(101,"Physics I","PHYS",'A',4,"Wagner","Fugate"));

        // Create the filters to be applied
        CreditHourFilter creditHourFilter = new CreditHourFilter(4);
        DepartmentFilter departmentFilter = new DepartmentFilter("MATH");

        // Apply the filters
        List<Section> results = sections;
        results = creditHourFilter.apply(results);
        results = departmentFilter.apply(results);

        // Only 2 sections in this group should match both criteria
        assertEquals(2, results.size());

        // Ensure that all the sections that made it through filtering actually should be there
        for (Section s : results) {
            assertEquals(4, s.getCourse().getCreditHours());
            assertEquals("MATH",s.getCourse().getDepartment());
        }

    }

}
