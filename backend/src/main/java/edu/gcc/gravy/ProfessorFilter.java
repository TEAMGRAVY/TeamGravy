package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfessorFilter extends Filter {
    private String professor;

    public ProfessorFilter(String professor) {
        super(FilterType.PROFESSOR);
        this.professor = professor;
    }

    @Override
    public List<Section> apply(List<Section> sections) {
        List<Section> results = new ArrayList<>();
        for (Section section : sections){
            if (Objects.equals(section.getProfessor(), professor)){
                results.add(section);
            }
        }
        return results;
    }
}
