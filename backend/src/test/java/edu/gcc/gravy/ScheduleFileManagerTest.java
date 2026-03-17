package edu.gcc.gravy;

import com.google.gson.annotations.JsonAdapter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleFileManagerTest {

    @Test
    void saveAndLoadSchedule() {
        JSONReader reader = new JSONReader();

        ScheduleFileManager manager = ScheduleFileManager.getInstance();

        Student example = new Student(123456, "Electrical Engineering", "", "2026");

        ArrayList<Section> sections = reader.readJSON();
        Schedule sample = new Schedule(example, "Spring Sample Schedule", "2023_FALL");

        sample.addSection(sections.get(2));
        sample.addSection(sections.get(10));
        sample.addSection(sections.get(27));

        manager.SaveSchedule(sample.getScheduleName() + sample.getScheduleTerm(), sample);

        Schedule loadedSchedule = manager.LoadSchedule(sample.getScheduleName() + sample.getScheduleTerm());

        List<Section> loadedSection = loadedSchedule.getScheduleSections();
        assertTrue(loadedSection.get(1).equals(sections.get(2)) &&
        loadedSection.get(2).equals(sections.get(10)) &&
        loadedSection.get(3).equals(sections.get(27)));
    }

}