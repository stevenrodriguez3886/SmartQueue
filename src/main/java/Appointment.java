public class Appointment {
    String name;
    String date;
    int hour;

    public Appointment(String name, String date, int hour) {
        this.name = name;
        this.date = date;
        this.hour = hour;
    }

    public String getFormattedTime() {
        return String.format("%02d:00", hour);
    }
}