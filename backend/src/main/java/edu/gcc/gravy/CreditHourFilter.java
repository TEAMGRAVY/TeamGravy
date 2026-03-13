package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreditHourFilter extends Filter {
    private int creditHours;

    public CreditHourFilter(int creditHours) {
        super(FilterType.CREDITHOUR);
        this.creditHours = creditHours;
    }

    @Override
    public List<Section> apply(List<Section> sections) {
        List<Section> results = new ArrayList<>();
        for (Section section : sections){
            if (Objects.equals(section.getCourse().getCreditHours(), creditHours)){
                results.add(section);
            }
        }
        return results;
    }

}
