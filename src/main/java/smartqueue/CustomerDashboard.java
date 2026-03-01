package smartqueue;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * @brief REST Controller handling all customer-facing API endpoints.
 * * Manages booking, canceling, and checking wait times/positions.
 * Uses WebSockets to broadcast updates to connected clients.
 */
@RestController
@RequestMapping("/api/customer")
public class CustomerDashboard {

    private final AppointmentService appointmentService;
    private final SimpMessagingTemplate messagingTemplate;
    
    private static final int OPEN_HOUR = 9;  // 09:00 AM
    private static final int CLOSE_HOUR = 17; // 05:00 PM (17:00)

    /**
     * @brief Constructor for dependency injection.
     * @param appointmentService The business logic service.
     * @param messagingTemplate Template for sending WebSocket messages.
     */
    public CustomerDashboard(AppointmentService appointmentService, SimpMessagingTemplate messagingTemplate) {
        this.appointmentService = appointmentService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * @brief Handles booking a new appointment. Runs through multiple validation checks.
     * @param request The incoming JSON payload containing name, date, and hour.
     * @return ResponseEntity with the created Appointment or an error message.
     */
    @PostMapping("/book")
    public ResponseEntity<Object> book(@RequestBody AppointmentRequest request) {
        String name = request.getName();
        String date = request.getDate();
        int hour = request.getHour();

        // Validation 1: Name format (Cannot be null, empty, or contain numbers)
        if (name == null || name.isEmpty() || name.matches(".*\\d.*")) {
            return ResponseEntity.badRequest().body("Name must not be empty or contain numbers.");
        }

        LocalDate dateObj;
        try {
            // Attempt to parse the date string into a real Date object
            dateObj = LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().body("Invalid date format.");
        }

        // Validation 2: Prevent historical bookings
        LocalDate now = LocalDate.now();
        if (dateObj.isBefore(now)) {
            return ResponseEntity.badRequest().body("Past dates are not allowed.");
        }

        // Validation 3: Prevent bookings too far into the future (max 1 year)
        if (dateObj.isAfter(now.plusYears(1))) {
            return ResponseEntity.badRequest().body("Choose a date within the next year.");
        }

        // Validation 4: Ensure booking is on a weekday
        DayOfWeek dow = dateObj.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return ResponseEntity.badRequest().body("Appointments cannot be scheduled on weekends.");
        }

        // Validation 5: Ensure time falls within business hours
        if (hour < OPEN_HOUR || hour >= CLOSE_HOUR) {
            return ResponseEntity.badRequest().body("Choose a time between 09:00 and 16:00.");
        }

        // Validation 6: Check database to prevent double bookings
        for (Appointment a : appointmentService.getAll()) {
            if (a.date.equals(date) && a.name.equalsIgnoreCase(name)) {
                return ResponseEntity.badRequest().body("You already have an appointment on this date.");
            }
            if (a.date.equals(date) && a.hour == hour) {
                return ResponseEntity.badRequest().body("This time slot is already taken.");
            }
        }

        // If all validations pass, create and save the appointment
        Appointment newAppointment = new Appointment(name, date, hour);
        appointmentService.add(newAppointment);

        // Broadcast to all connected WebSockets that the queue has changed
        messagingTemplate.convertAndSend("/topic/queue-update", "Refresh");

        return ResponseEntity.ok(newAppointment);
    }

    /**
     * @brief Cancels an existing appointment using its UUID.
     * @param request JSON payload containing the appointment 'id'.
     * @return ResponseEntity with success or error message.
     */
    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancel(@RequestBody AppointmentRequest request) {
        boolean removed = appointmentService.removeSpecific(request.getId());
        if (removed) {
            // Notify clients of the change so UI updates in real-time
            messagingTemplate.convertAndSend("/topic/queue-update", "Refresh");
            return ResponseEntity.ok("Appointment successfully canceled.");
        }
        return ResponseEntity.badRequest().body("No matching appointment found.");
    }

    /**
     * @brief Calculates and returns the estimated wait time.
     * @param date The date of the appointment.
     * @param hour The hour of the appointment.
     * @return ResponseEntity containing a string with wait time and queue depth.
     */
    @GetMapping("/wait-time")
    public ResponseEntity<String> getWaitTime(@RequestParam String date, @RequestParam int hour) {
        int waitCount = appointmentService.getWaitCount(date, hour);
        int totalWait = waitCount * appointmentService.getDuration();
        return ResponseEntity.ok("Estimated Wait: " + totalWait + " minutes. " + waitCount + " people ahead of you.");
    }

    /**
     * @brief Checks the global queue position for a specific appointment.
     * @param id The UUID of the appointment.
     * @return ResponseEntity containing a status message regarding their turn.
     */
    @GetMapping("/position")
    public ResponseEntity<String> getPosition(@RequestParam String id) {
        int pos = appointmentService.getPosition(id);
        if (pos == -1) {
            return ResponseEntity.badRequest().body("Appointment not found. Did you cancel it?");
        }
        if (pos == 0) {
            return ResponseEntity.ok("You are next!");
        }
        return ResponseEntity.ok("There are " + pos + " people ahead of you.");
    }
}