package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;

public class Search {
    private List<Section> allSections;
    private List<Section> currentResults;
    private List<Filter> activeFilters;
    private String searchQuery;

    public Search(String searchQuery) { // Use getCourseCode() from Section to see course code format (These should match based on the database)
        this.searchQuery = searchQuery;
        allSections = new ArrayList<>();
        activeFilters = new ArrayList<>();
        currentResults = allSections;
    }

    public List<Section> addFilter(Filter fitler) {
        activeFilters.add(fitler);
        // Apply new filter
        return currentResults;
    }

    public List<Section> removeFilter(FilterType type) {
        // Iterate through activeFilters and remove ones that match type
        // Apply updated filters
        return currentResults;
    }

    public List<Section> getResults() {
        return currentResults;
    }

    public void reset() { // When the user provides a new searchQuery
        allSections = new ArrayList<>();
        activeFilters = new ArrayList<>();
        currentResults = allSections;
    }
}
