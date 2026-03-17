package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TermFilter extends Filter {
    private final String term;

    public TermFilter(String term) {
        super(FilterType.TERM);
        this.term = term;
    }

    @Override
    public List<Section> apply(List<Section> sections) {
        List<Section> results = new ArrayList<>();
        for (Section section : sections) {
            if (Objects.equals(section.getCourse().getTerm(), term)) {
                results.add(section);
            }
        }
        return results;
    }
}
