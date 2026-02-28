package smartqueue;
public class Appointment {
    String name;
    String date;
    int hour;

    public Appointment(String name, String date, int hour) {
        this.name = name;
        this.date = date;
        this.hour = hour;
    }

    // Required for the web browser to see the data
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getFormattedTime() {
        return String.format("%02d:00", hour);
    }
}