package smartqueue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;

/**
 * @brief Entity class representing a customer appointment.
 * * This class is mapped to a database table via JPA/Hibernate.
 * It stores customer identification, the requested date, and the hour slot.
 */
@Entity // Tells Hibernate to create a database table for this class
public class Appointment {

    /** * @brief Unique identifier for the appointment.
     * Stored as a UUID string to prevent ID guessing and collisions.
     */
    @Id
    public String id;

    /** @brief The name of the customer booking the appointment. */
    public String name;
    
    /** * @brief The scheduled date (YYYY-MM-DD).
     * Mapped to 'appointment_date' because 'date' is a reserved SQL keyword.
     * It is easier to make this change here than to rename variables everywhere else.
     */
    @Column(name = "appointment_date") 
    public String date;

    /** * @brief The scheduled hour (24-hour format).
     * Mapped to 'appointment_hour' because 'hour' is a reserved SQL keyword.
     */
    @Column(name = "appointment_hour")
    public int hour;

    /**
     * @brief Default constructor.
     * REQUIRED for JPA to instantiate the entity from the database.
     */
    public Appointment() {}

    /**
     * @brief Constructs a new Appointment with a unique UUID.
     * * @param name The customer's name.
     * @param date The date of the appointment (YYYY-MM-DD).
     * @param hour The hour of the appointment (24-hour format).
     */
    public Appointment(String name, String date, int hour) {
        this.name = name;
        this.date = date;
        this.hour = hour;
        this.id = UUID.randomUUID().toString(); // Generate unique ID on creation
    }

    // --- Getters required for the web browser / JSON serialization to see the data ---

    /** @return The customer's name. */
    public String getName() { return name; }
    
    /** @return The appointment date string. */
    public String getDate() { return date; }
    
    /** @return The appointment hour. */
    public int getHour() { return hour; }
    
    /** @return The unique UUID of the appointment. */
    public String getId() { return id; }
}