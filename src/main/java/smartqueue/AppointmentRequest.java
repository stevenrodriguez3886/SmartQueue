package smartqueue;

public class AppointmentRequest {
    private String id;
    private String name;
    private String date;
    private int hour;

    // Getters and Setters are required for Spring Boot to map the JSON data
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }
}