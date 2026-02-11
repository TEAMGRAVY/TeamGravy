package edu.gcc.gravy;

import java.util.List;

public class CreditHourFilter extends Filter {
    private int creditHours;

    public CreditHourFilter(int creditHours) {
        super(FilterType.CREDITHOUR);
        this.creditHours = creditHours;
    }

    @Override
    public List<Section> apply(List<Section> sections) {
        return List.of();
    }

}
