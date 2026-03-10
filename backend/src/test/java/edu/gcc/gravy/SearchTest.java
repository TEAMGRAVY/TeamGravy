package edu.gcc.gravy;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class SearchTest {

    // Helper to construct test sections
    private Section makeSection(int id, String title, char letter) {

        Course course = new Course(id, title, "COMP", 3, "Fall");

        Section section = new Section();
        section.setCourse(course);
        section.setSectionID(letter);

        return section;
    }

    @Test
    void addFilter() {

        List<Section> sections = new ArrayList<>();
        sections.add(makeSection(141,"Programming I",'A'));
        sections.add(makeSection(230,"Advanced Programming",'A'));

        Search search = new Search("COMP","");
        search.setAllSections(sections);

        // Filter keeps only course 230
        Filter filter = new Filter(FilterType.DEPARTMENT) {
            @Override
            public List<Section> apply(List<Section> input) {

                List<Section> result = new ArrayList<>();

                for (Section s : input) {
                    if (s.getCourse().getCourseID() == 230) {
                        result.add(s);
                    }
                }

                return result;
            }
        };

        List<Section> results = search.addFilter(filter);

        assertEquals(1, results.size());
        assertEquals(230, results.get(0).getCourse().getCourseID());
    }

    @Test
    void removeFilter() {

        List<Section> sections = new ArrayList<>();
        sections.add(makeSection(141,"Programming I",'A'));
        sections.add(makeSection(230,"Advanced Programming",'A'));

        Search search = new Search("COMP","");
        search.setAllSections(sections);

        Filter filter = new Filter(FilterType.DEPARTMENT) {
            @Override
            public List<Section> apply(List<Section> input) {

                List<Section> result = new ArrayList<>();

                for (Section s : input) {
                    if (s.getCourse().getCourseID() == 230) {
                        result.add(s);
                    }
                }

                return result;
            }
        };

        search.addFilter(filter);

        assertEquals(1, search.getResults().size());

        List<Section> results = search.removeFilter(FilterType.DEPARTMENT);

        assertEquals(2, results.size());
    }

    @Test
    void getResults() {

        List<Section> sections = new ArrayList<>();
        sections.add(makeSection(141,"Programming I",'A'));
        sections.add(makeSection(230,"Advanced Programming",'A'));

        Search search = new Search("COMP","");
        search.setAllSections(sections);

        List<Section> results = search.getResults();

        assertEquals(2, results.size());
    }

    @Test
    void reset() {

        List<Section> sections = new ArrayList<>();
        sections.add(makeSection(141,"Programming I",'A'));
        sections.add(makeSection(230,"Advanced Programming",'A'));

        Search search = new Search("COMP","");
        search.setAllSections(sections);

        Filter filter = new Filter(FilterType.DEPARTMENT) {
            @Override
            public List<Section> apply(List<Section> input) {

                List<Section> result = new ArrayList<>();
                result.add(input.get(0));
                return result;
            }
        };

        search.addFilter(filter);

        assertEquals(1, search.getResults().size());

        search.reset();

        // reset clears filters but keeps base search
        assertEquals(2, search.getResults().size());
    }

    @Test
    void setAllSections() {

        List<Section> firstList = new ArrayList<>();
        firstList.add(makeSection(141,"Programming I",'A'));

        Search search = new Search("COMP","");
        search.setAllSections(firstList);

        assertEquals(1, search.getResults().size());

        List<Section> newList = new ArrayList<>();
        newList.add(makeSection(230,"Advanced Programming",'A'));
        newList.add(makeSection(350,"Operating Systems",'A'));

        search.setAllSections(newList);

        assertEquals(2, search.getResults().size());
    }

    @Test
    void debugBaseSearch() {

        Course c = new Course(141,"Programming I","COMP",3,"Fall");

        Section s = new Section();
        s.setCourse(c);
        s.setSectionID('A');

        List<Section> list = new ArrayList<>();
        list.add(s);

        Search search = new Search("COMP","");
        search.setAllSections(list);

        System.out.println(search.getResults().size());

        assertEquals(1, search.getResults().size());
    }

}