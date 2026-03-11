package edu.gcc.gravy;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfessorFilterTest {

    // Helper methods to construct sections for testing.
    private Section makeSection(int id, String title, String department, char letter, String professor) {

        Course course = new Course(id, title, department, 3, "Fall");

        Section section = new Section();
        section.setCourse(course);
        section.setSectionID(letter);
        ArrayList<String> profs = new ArrayList<>();
        profs.add(professor);
        section.setProfessors(profs);

        return section;
    }

    private Section makeSection(int id, String title, String department, char letter, String professor1, String professor2) {

        Course course = new Course(id, title, department, 3, "Fall");

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
    void getType() {


        String profToSearchFor = "Hutchins";
        ProfessorFilter filter = new ProfessorFilter(profToSearchFor);

        // The filter type should always be PROFESSOR
        assertEquals(FilterType.PROFESSOR, filter.getType());
    }

    @Test
    void apply() {

        List<Section> sections = new ArrayList<>();

        sections.add(makeSection(141,"Programming I","COMP",'A',"Hutchins"));
        sections.add(makeSection(210,"Data Structures","COMP",'B',"Hutchins"));
        sections.add(makeSection(101,"Calculus I","MATH",'A',"McIntyre"));


        String profToSearchFor = "Hutchins";
        ProfessorFilter filter = new ProfessorFilter(profToSearchFor);

        List<Section> results = filter.apply(sections);

        // Only the COMP sections should remain after filtering
        assertEquals(2, results.size());

        for (Section s : results) {
            ArrayList<String> sectionProfs = s.getProfessors();
            for (String prof : sectionProfs){
                assertEquals("Hutchins", prof);
            }
        }
    }

    @Test
    void applyWithEmptyList() {
        String profToSearchFor = "Hutchins";
        ProfessorFilter filter = new ProfessorFilter(profToSearchFor);

        List<Section> sections = new ArrayList<>();

        List<Section> results = filter.apply(sections);

        assertTrue(results.isEmpty());
    }

    @Test
    void applyWithNoMatches() {

        List<Section> sections = new ArrayList<>();

        sections.add(makeSection(141,"Programming I","COMP",'A',"Hutchins","Johnson"));
        sections.add(makeSection(210,"Data Structures","COMP",'B',"Hutchins"));
        sections.add(makeSection(101,"Calculus I","MATH",'A',"McIntyre","Smith"));


        String profToSearchFor = "Dickinson";
        ProfessorFilter filter = new ProfessorFilter(profToSearchFor);

        List<Section> results = filter.apply(sections);

        assertTrue(results.isEmpty());
    }


}