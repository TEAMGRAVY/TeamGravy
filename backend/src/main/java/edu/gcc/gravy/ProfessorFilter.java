package edu.gcc.gravy;

import java.util.List;

public class ProfessorFilter extends Filter {
    private String professor;

    public ProfessorFilter(String professor) {
        super(FilterType.PROFESSOR);
        this.professor = professor;
    }

    @Override
    public List<Section> apply(List<Section> sections) {
        return List.of();
    }
}
