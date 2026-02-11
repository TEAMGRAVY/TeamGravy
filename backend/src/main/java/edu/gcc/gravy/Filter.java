package edu.gcc.gravy;

import java.util.List;

public abstract class Filter {
    private FilterType type;

    public Filter(FilterType type) {
        this.type = type;
    }

    public abstract List<Section> apply(List<Section> sections);

    public FilterType getType() {
        return type;
    }
}
