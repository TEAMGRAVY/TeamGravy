package edu.gcc.gravy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfessorFilter extends Filter {
    private final String professor;

    public ProfessorFilter(String professor) {
        super(FilterType.PROFESSOR);
        this.professor = professor;
    }

    @Override
    public ArrayList<Section> apply(List<Section> sections) {
        ArrayList<Section> results = new ArrayList<>();
        for (Section section : sections){

            ArrayList<String> sectionProfs = section.getProfessors();

            for(String prof : sectionProfs){

                if (Objects.equals(prof, professor)){
                    results.add(section);
                    break;
                }
            }
        }

        return results;
    }
}
