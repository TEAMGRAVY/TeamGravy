package edu.gcc.gravy;

import java.io.*;

public class ScheduleFileManager {

    private ScheduleFileManager manager = null;

    private ScheduleFileManager(){}

    public ScheduleFileManager getInstance(){
        if (manager == null){
            manager = new ScheduleFileManager();
        }
        return manager;
    }

    public void SaveSchedule(String fileName, Schedule object){
        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

            out.writeObject(this);
            //System.out.println("Schedule object serialized successfully and saved to " + fileName);

        } catch (IOException i) {
            i.printStackTrace();
        }
    }



    public static Schedule LoadSchedule(String fileName){
        try (FileInputStream fileIn = new FileInputStream(fileName);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {

            // Read the object and cast it to the original class type
            Schedule readSchedule = (Schedule) in.readObject();

            return readSchedule;
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Schedule class not found");
            c.printStackTrace();
            return null;
        }
    }

}
