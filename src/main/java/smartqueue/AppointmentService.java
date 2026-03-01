package smartqueue;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AppointmentService {
    
    private final AppointmentDatabase database;
    private int appointmentDurationMinutes = 15; // Kept in memory for the active session

    public AppointmentService(AppointmentDatabase database) {
        this.database = database;
    }

    public void add(Appointment a) {
        // Saves to the H2 database file
        database.save(a); 
    }

    public List<Appointment> getAll() {
        // Retrieves the sorted list directly from the database
        return database.findAllByOrderByDateAscHourAsc();
    }

    public boolean removeSpecific(String id) {
        // Built-in JPA method to check if the ID exists
        if (database.existsById(id)) {
            database.deleteById(id); // Built-in JPA deletion
            return true;
        }
        return false;
    }

    public Appointment removeNext() {
        List<Appointment> all = getAll();
        if (all.isEmpty()) return null;
        
        // Grab the first person, delete them from the database, and return their data
        Appointment next = all.get(0);
        database.delete(next);
        return next;
    }

    public int getWaitCount(String date, int hour) {
        int waitCount = 0;
        List<Appointment> all = getAll();
        for (Appointment a : all) {
            if (a.date.equals(date) && a.hour < hour) {
                waitCount++;
            }
        }
        return waitCount;
    }

    public int getPosition(String id) {
        List<Appointment> all = getAll();
        for (int i = 0; i < all.size(); i++) {
            Appointment a = all.get(i);
            // Look for the exact UUID match instead of checking name, date, and hour
            if (a.id.equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public int getDuration() {
        return appointmentDurationMinutes;
    }

    public void setDuration(int duration) {
        this.appointmentDurationMinutes = duration;
    }
}