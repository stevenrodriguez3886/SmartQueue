package smartqueue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity // Tells Hibernate to create a database table for this class
public class Appointment {
    @Id
    public String id;
    public String name;
    
    // date and hour reserved SQL keywords, so we need to specify the column names explicitly.
    // easier to make this change here than to change the variable names everywhere else in the codebase.
    @Column(name = "appointment_date") 
    public String date;
    @Column(name = "appointment_hour")
    public int hour;

    // Default constructor is REQUIRED for JPA
    public Appointment() {}

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