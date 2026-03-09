package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;

public class Search {
    private List<Section> allSections;
    private List<Section> currentResults;
    private List<Filter> activeFilters;
//jjjj
    // both of these make up a Query
    private final String codeQuery;
    private final String keywordQuery;

    public Search(String codeQuery, String keywordQuery) {
        this.codeQuery = codeQuery;
        this.keywordQuery = keywordQuery;

        this.activeFilters = new ArrayList<>();

        // potentially implement going to library and finding "allSections" here

        this.allSections = new ArrayList<>();
        this.currentResults = runBaseSearch();

    }

    public List<Section> addFilter(Filter filter) {
        activeFilters.add(filter);
        currentResults = filter.apply(currentResults);
        return currentResults;
    }

    public List<Section> removeFilter(FilterType type) {
        activeFilters.removeIf(f -> f.getType() == type);

        List<Section> results = runBaseSearch();

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

    public void setAllSections(List<Section> sections) {
        this.allSections = sections;
        this.currentResults = runBaseSearch();
    }

    // R1a + R1b base search
    private List<Section> runBaseSearch() {
        List<Section> results = new ArrayList<>();

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