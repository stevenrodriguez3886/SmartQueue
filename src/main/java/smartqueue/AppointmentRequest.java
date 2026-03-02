package smartqueue;

/**
 * @brief Data Transfer Object (DTO) for handling incoming API requests.
 * * Used to map incoming JSON payloads from the frontend into Java objects.
 */
public class AppointmentRequest {
    
    /** @brief The UUID of the appointment (used for cancellation requests). */
    private String id;
    
    /** @brief The customer's name (used for booking requests). */
    private String name;
    
    /** @brief The requested date (used for booking requests). */
    private String date;
    
    /** @brief The requested hour (used for booking requests). */
    private int hour;

    // --- Getters and Setters ---
    // These are absolutely required for Spring Boot (Jackson) to map the JSON data correctly.

    /** @return The appointment UUID. */
    public String getId() { return id; }
    
    /** @param id The appointment UUID to set. */
    public void setId(String id) { this.id = id; }

    /** @return The customer's name. */
    public String getName() { return name; }
    
    /** @param name The customer's name to set. */
    public void setName(String name) { this.name = name; }
    
    /** @return The appointment date string. */
    public String getDate() { return date; }
    
    /** @param date The appointment date string to set. */
    public void setDate(String date) { this.date = date; }
    
    /** @return The appointment hour. */
    public int getHour() { return hour; }
    
    /** @param hour The appointment hour to set. */
    public void setHour(int hour) { this.hour = hour; }
}