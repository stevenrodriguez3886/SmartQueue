package smartqueue;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AppointmentService {
    // This replaces the shared ArrayList from the original main method
    private final List<Appointment> appointments = new ArrayList<>();
    private int appointmentDurationMinutes = 15; // Default

    public void add(Appointment a) {
        appointments.add(a);
        // Core Logic: Keep the list sorted chronologically
        appointments.sort(Comparator.comparing((Appointment app) -> app.date)
                .thenComparingInt(app -> app.hour));
    }

    public List<Appointment> getAll() {
        return appointments;
    }

    public boolean removeSpecific(String name, String date, int hour) {
        // Core Logic: Used for the "Cancel" feature
        return appointments.removeIf(a -> 
            a.name.equalsIgnoreCase(name) && 
            a.date.equals(date) && 
            a.hour == hour
        );
    }

    public Appointment removeNext() {
        if (appointments.isEmpty()) return null;
        // Core Logic: "Serve" the first person in the sorted queue
        return appointments.remove(0);
    }

    public int getWaitCount(String date, int hour) {
        int waitCount = 0;
        for (Appointment a : appointments) {
            // Core Logic: Calculate how many people are ahead on the same day
            if (a.date.equals(date) && a.hour < hour) {
                waitCount++;
            }
        }
        return waitCount;
    }

    public int getDuration() {
        return appointmentDurationMinutes;
    }

    public void setDuration(int duration) {
        this.appointmentDurationMinutes = duration;
    }
}