package smartqueue;

import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @brief Service class containing the core business logic for the SmartQueue application.
 * * Acts as an intermediary between the REST controllers and the database layer.
 */
@Service
public class AppointmentService {
    
    /** @brief Dependency-injected database repository. */
    private final AppointmentDatabase database;
    
    /** @brief Expected duration of an appointment in minutes (kept in memory for the active session). */
    private int appointmentDurationMinutes = 15; 

    /**
     * @brief Constructor for dependency injection.
     * @param database The repository to interact with the database.
     */
    public AppointmentService(AppointmentDatabase database) {
        this.database = database;
    }

    /**
     * @brief Adds a new appointment to the database.
     * @param a The Appointment object to save.
     */
    public void add(Appointment a) {
        // Saves to the underlying database (e.g., H2 file/memory database)
        database.save(a); 
    }

    /**
     * @brief Retrieves all scheduled appointments in chronological order.
     * @return A sorted List of Appointments.
     */
    public List<Appointment> getAll() {
        // Retrieves the sorted list directly using our custom JPA repository method
        return database.findAllByOrderByDateAscHourAsc();
    }

    /**
     * @brief Removes a specific appointment by its UUID.
     * @param id The UUID of the appointment to cancel.
     * @return True if the appointment was found and removed, false otherwise.
     */
    public boolean removeSpecific(String id) {
        // Built-in JPA method to check if the ID exists to prevent errors
        if (database.existsById(id)) {
            database.deleteById(id); // Built-in JPA deletion
            return true;
        }
        return false;
    }

    /**
     * @brief Removes the next appointment in the queue and returns it (used by employees).
     * @return The next Appointment to be served, or null if the queue is empty.
     */
    public Appointment removeNext() {
        List<Appointment> all = getAll();
        if (all.isEmpty()) return null;
        
        // Grab the first person, delete them from the database, and return their data
        Appointment next = all.get(0);
        database.delete(next);
        return next;
    }

    /**
     * @brief Calculates how many people are ahead of a specific time slot on a given date.
     * @param date The date to check.
     * @param hour The hour to check.
     * @return The number of appointments scheduled before the given hour on the given date.
     */
    public int getWaitCount(String date, int hour) {
        int waitCount = 0;
        List<Appointment> all = getAll();
        
        // Iterate through all appointments and count those on the same day but earlier in the day
        for (Appointment a : all) {
            if (a.date.equals(date) && a.hour < hour) {
                waitCount++;
            }
        }
        return waitCount;
    }

    /**
     * @brief Finds the current position of a specific appointment in the global queue.
     * @param id The UUID of the appointment.
     * @return The 0-based index position in the queue, or -1 if not found.
     */
    public int getPosition(String id) {
        List<Appointment> all = getAll();
        for (int i = 0; i < all.size(); i++) {
            Appointment a = all.get(i);
            // Look for the exact UUID match instead of checking name, date, and hour
            // This is safer and guarantees we find the exact right person.
            if (a.id.equals(id)) {
                return i;
            }
        }
        return -1; // Appointment was likely canceled or already served
    }

    /**
     * @brief Gets the currently configured appointment duration.
     * @return Duration in minutes.
     */
    public int getDuration() {
        return appointmentDurationMinutes;
    }

    /**
     * @brief Sets the expected appointment duration.
     * @param duration The new duration in minutes.
     */
    public void setDuration(int duration) {
        this.appointmentDurationMinutes = duration;
    }
}