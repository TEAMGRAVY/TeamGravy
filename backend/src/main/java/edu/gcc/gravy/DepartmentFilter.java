package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DepartmentFilter extends Filter {
    private String department;

    public DepartmentFilter(String department) {
        super(FilterType.DEPARTMENT);
        this.department = department;
    }

    @Override
    public List<Section> apply(List<Section> sections) {
        List<Section> results = new ArrayList<>();
        for (Section section : sections){
            if (Objects.equals(section.getCourse().getDepartment(), department)){
                results.add(section);
            }
        }
        return results;
    }
}
