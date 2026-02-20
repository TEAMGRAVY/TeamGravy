package edu.gcc.gravy;

public class Activity {
    private String name;
    private TimeSlot time;

    public Activity(String name, TimeSlot time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public TimeSlot getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) { // Override for quicker removeActivity() in Schedule
        if (this == o) return true;
        if (!(o instanceof Activity)) return false;
        Activity a = (Activity) o;
        return Objects.equals(time, a.time)
                && Objects.equals(name, a.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, name);
    }
}
