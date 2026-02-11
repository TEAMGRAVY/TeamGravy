package edu.gcc.gravy;

import java.util.List;

public class DepartmentFilter extends Filter {
    private String department;

    public DepartmentFilter(String department) {
        super(FilterType.DEPARTMENT);
        this.department = department;
    }

    @Override
    public List<Section> apply(List<Section> sections) {
        return List.of();
    }
}
