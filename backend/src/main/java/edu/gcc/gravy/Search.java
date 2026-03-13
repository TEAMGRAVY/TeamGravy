package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;

public class Search {
    private ArrayList<Section> allSections;
    private ArrayList<Section> currentResults;
    private ArrayList<Filter> activeFilters;

    // both of these make up a Query.
    private final String codeQuery;
    private final String keywordQuery;

    public Search(String codeQuery, String keywordQuery) {

        this.codeQuery = codeQuery;
        this.keywordQuery = keywordQuery;
        this.activeFilters = new ArrayList<>();
        this.allSections = new JSONReader().readJSON();
        this.currentResults = runBaseSearch();

    }

    public ArrayList<Section> addFilter(Filter filter) {
        activeFilters.add(filter);
        currentResults = filter.apply(currentResults);
        return currentResults;
    }

    public ArrayList<Section> removeFilter(FilterType type) {
        activeFilters.removeIf(f -> f.getType() == type);

        ArrayList<Section> results = allSections;

        for (Filter f : activeFilters) {
            results = f.apply(results);
        }

        currentResults = results;
        return currentResults;
    }

    public List<Section> getResults() {
        return currentResults;
    }

    public void reset() {
        activeFilters = new ArrayList<>();
        currentResults = runBaseSearch();
    }

    public void setAllSections(ArrayList<Section> sections) {
        this.allSections = sections;
        this.currentResults = runBaseSearch();
    }

    // R1a + R1b base search
    private ArrayList<Section> runBaseSearch() {
        ArrayList<Section> results = new ArrayList<>();

        String codeQ = codeQuery == null ? "" : codeQuery.toLowerCase().trim();
        String keyQ = keywordQuery == null ? "" : keywordQuery.toLowerCase().trim();

        for (Section s : allSections) {
            String code = (s.getCourseCode() == null) ? "" :
                    s.getCourseCode().toLowerCase();

            String title = (s.getCourse() != null &&
                    s.getCourse().getTitle() != null)
                    ? s.getCourse().getTitle().toLowerCase()
                    : "";

            boolean codeMatch = codeQ.isEmpty() || code.contains(codeQ);
            boolean keywordMatch = keyQ.isEmpty() || title.contains(keyQ);

            if (codeMatch && keywordMatch) {
                results.add(s);
            }
        }

        return results;
    }
}