package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;

/**
 * Search is the central engine for finding course sections.
 *
 * BIG PICTURE:
 * A Search object is built with an optional course code query and/or keyword query.
 * These two strings form the "base query" — a broad, text-based sweep across all
 * available sections loaded by JSONReader into Main.allSections. From that base
 * result, the caller (CourseController) can layer on any number of Filter objects
 * (DepartmentFilter, ProfessorFilter, CreditHourFilter, TimeRangeFilter, etc.) to
 * progressively narrow the results. Filters can also be removed one at a time, at
 * which point the entire filter chain replays from scratch to stay consistent.
 *
 * STATE MODEL:
 *   allSections     — the full master list of every Section from JSONReader. Never modified.
 *   currentResults  — the live filtered view the UI/controller reads. Updated after every change.
 *   activeFilters   — the ordered list of Filters currently applied on top of the base search.
 */

public class Search {
    private List<Section> allSections;
    private List<Section> currentResults;
    private List<Filter> activeFilters;

    // Both of these make up a Query.
    // codeQuery    — matched against getCourseCode() e.g. "CS 214 A"
    // keywordQuery — matched against the course title  e.g. "Data Structures"
    // Either can be null/empty, which acts as a wildcard and matches every section.
    private final String codeQuery;
    private final String keywordQuery;

    /**
     * Constructs a new Search with the given base query strings.
     *
     * allSections starts empty here — the caller must immediately follow up with
     * setAllSections() to inject Main.allSections, which triggers runBaseSearch()
     * again with real data. The TODO below notes that fetching allSections internally
     * is a possible future improvement.
     *
     * @param codeQuery    partial/full course code to match (case-insensitive), or null
     * @param keywordQuery partial/full course title keyword to match (case-insensitive), or null
     */
    public Search(String codeQuery, String keywordQuery) {
        this.codeQuery = codeQuery;
        this.keywordQuery = keywordQuery;

        this.activeFilters = new ArrayList<>();

        // potentially implement going to library and finding "allSections" here

        // allSections is empty until setAllSections() is called, so currentResults
        // will also be empty — it gets rebuilt once real data is injected below.
        this.allSections = new ArrayList<>();
        this.currentResults = runBaseSearch();

    }

    /**
     * Adds a Filter and immediately narrows currentResults with it.
     *
     * Takes a shortcut: rather than replaying all filters from allSections,
     * it applies the new filter only to currentResults (which already has all
     * previous filters baked in). This is efficient but means removal must
     * replay from scratch — see removeFilter().
     *
     * @param filter any Filter subclass (DepartmentFilter, ProfessorFilter, etc.)
     * @return updated currentResults after the new filter is applied
     */

    public List<Section> addFilter(Filter filter) {
        activeFilters.add(filter);                     // Remember it so we can replay or remove it later
        currentResults = filter.apply(currentResults); // Narrow the already-filtered result set
        return currentResults;
    }

    /**
     * Removes all filters of the given FilterType and recomputes results from scratch.
     *
     * Because addFilter() works incrementally, sections excluded by a filter are gone
     * from currentResults — you can't simply undo them. So removal must restart from
     * allSections and replay every remaining filter in order to get a correct result.
     *
     * Note: removeIf clears ALL filters of the given type at once, so if the same
     * filter type was added more than once, all instances are dropped together.
     *
     * @param type the FilterType enum value identifying which filter(s) to remove
     * @return updated currentResults after the removed filter is no longer applied
     */

    public List<Section> removeFilter(FilterType type) {
        activeFilters.removeIf(f -> f.getType() == type); // Drop every filter matching this type

        // Must replay from the full master list — currentResults may be missing sections
        // that the removed filter had excluded.
        List<Section> results = allSections;

        // Chain the remaining filters sequentially, same order they were originally added
        for (Filter f : activeFilters) {
            results = f.apply(results);
        }

        currentResults = results;
        return currentResults;
    }

    /**
     * Returns the current filtered result set without modifying anything.
     * This is the primary read method used by CourseController to send results to the UI.
     */

    public List<Section> getResults() {
        return currentResults;
    }

    /**
     * Clears all active filters and re-runs only the base text search.
     * currentResults after this call reflects just codeQuery + keywordQuery with no filters.
     */

    public void reset() {
        activeFilters = new ArrayList<>();
        currentResults = runBaseSearch();
    }

    /**
     * Injects the master section list and immediately re-runs the base search.
     *
     * Called by CourseController after JSONReader loads Main.allSections. Until this
     * is called, allSections is empty and every search returns nothing. Active filters
     * are NOT re-applied here — this is intended to be called during initial setup
     * before any filters are added.
     *
     * @param sections the full list of all Sections parsed by JSONReader
     */

    public void setAllSections(List<Section> sections) {
        this.allSections = sections;
        this.currentResults = runBaseSearch(); // Rebuild now that real data is present
    }


    /**
     * Implements R1a and R1b
     * Scans allSections with the codeQuery and keywordQuery and returns every section that passes.
     * This is the unseen foundation that every filter chain is built on top of.
     *
     * Matching rules:
     *   - Both queries are lowercased and trimmed so matching is case-insensitive and
     *     whitespace-tolerant before comparison.
     *   - A null or blank query is a wildcard — it matches every section automatically.
     *   - codeQuery uses a substring check on getCourseCode()           e.g. "cs 2" matches "cs 214 a"
     *   - keywordQuery uses a substring check on getCourse().getTitle() e.g. "data" matches "Data Structures"
     *   - A section must satisfy BOTH checks to be included (AND logic).
     *   - If a section has a null course code or title, it is treated as "" and only
     *     matches if the corresponding query is also blank (wildcard).
     */

    private List<Section> runBaseSearch() {
        // Create an empty list that will be filled with every section that passes both checks
        List<Section> results = new ArrayList<>();

        // If codeQuery is null, treat it as "" so we can safely call .isEmpty() on it later.
        // If it is not null, lowercase it and strip leading/trailing whitespace so that
        // "CS 214" and "cs 214 " both behave identically when compared.
        String codeQ = codeQuery == null ? "" : codeQuery.toLowerCase().trim();

        // Same treatment for keywordQuery — null becomes "", otherwise lowercase and trim.
        String keyQ = keywordQuery == null ? "" : keywordQuery.toLowerCase().trim();

        // Walk every section in the master list one at a time
        for (Section s : allSections) {

            // Get this section's course code (e.g. "COMP 220 A") and lowercase it for comparison.
            // If getCourseCode() returns null, use "" instead so the contains() check below
            // does not crash.
            String code = (s.getCourseCode() == null) ? "" :
                    s.getCourseCode().toLowerCase();

            // Get this section's course title (e.g. "Data Structures") and lowercase it.
            // Must check that getCourse() is not null before calling getTitle() on it,
            // and then check that getTitle() itself is not null before lowercasing it.
            // If either is null, fall back to "" for the same reason as above.
            String title = (s.getCourse() != null &&
                    s.getCourse().getTitle() != null)
                    ? s.getCourse().getTitle().toLowerCase()
                    : "";

            // codeMatch is true if the user typed no code query at all (blank = match everything),
            // OR if this section's course code contains whatever the user did type.
            boolean codeMatch = codeQ.isEmpty() || code.contains(codeQ);

            // keywordMatch is true if the user typed no keyword at all (blank = match everything),
            // OR if this section's course title contains whatever the user did type.
            boolean keywordMatch = keyQ.isEmpty() || title.contains(keyQ);

            // Only add this section to results if it passed BOTH the code check and the keyword check.
            // Failing either one means the section is not a match and is skipped.
            if (codeMatch && keywordMatch) {
                results.add(s);
            }
        }

        // Return the finished list of every section that matched both queries
        return results;
    }
}