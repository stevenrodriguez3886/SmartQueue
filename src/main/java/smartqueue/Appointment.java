package smartqueue;

import java.util.UUID;

public class Appointment {
    String name;
    String date;
    int hour;
    public String id;

    public Appointment(String name, String date, int hour) {
        this.name = name;
        this.date = date;
        this.hour = hour;
        this.id = UUID.randomUUID().toString();
    }

    // Required for the web browser to see the data
    public String getName() { return name; }
    public String getDate() { return date; }
    public int getHour() { return hour; }
    public String getId() { return id; }
    
}