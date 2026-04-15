package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OpenFilter extends Filter {
    private final boolean isOpen;

    public OpenFilter(boolean isOpen) {
        super(FilterType.OPEN);
        this.isOpen = isOpen;
    }

    @Override
    public List<Section> apply(List<Section> sections) {
        List<Section> results = new ArrayList<>();
        for (Section section : sections) {
            if (Objects.equals(section.isOpen(), isOpen)){
                results.add(section);
            }
        }
        return results;
    }

}
