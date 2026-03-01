package smartqueue;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @brief REST Controller handling employee-facing API endpoints.
 * * Provides features for staff to view the queue, serve customers, and adjust settings.
 * All routes here are protected by Spring Security.
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeDashboard {

    private final AppointmentService appointmentService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * @brief Constructor for dependency injection.
     */
    public EmployeeDashboard(AppointmentService appointmentService, SimpMessagingTemplate messagingTemplate) {
        this.appointmentService = appointmentService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * @brief Retrieves the entire scheduled queue.
     * @return A list of all appointments, visible to staff.
     */
    @GetMapping("/full-queue")
    public List<Appointment> getFullQueue() {
        // Staff see the full list with names, unlike customers
        return appointmentService.getAll();
    }

    /**
     * @brief "Serves" the next person in line by removing them from the queue and notifying them.
     * @return ResponseEntity with the name of the person being served.
     */
    @DeleteMapping("/serve")
    public ResponseEntity<String> serveNext() {
        Appointment next = appointmentService.removeNext();
        if (next == null) return ResponseEntity.badRequest().body("Queue is empty.");
        
        // Notify ONLY the specific client listening to their unique UUID topic
        messagingTemplate.convertAndSend("/topic/notify/" + next.id, 
        "It is your turn, " + next.name + "!");

        // Broadcast to everyone else that the queue changed to update dashboards
        messagingTemplate.convertAndSend("/topic/queue-update", "Refresh");

        // Returns the name of the person being served back to the staff UI
        return ResponseEntity.ok("Now Serving: " + next.name);
    }

    /**
     * @brief Updates the global estimated duration per appointment.
     * @param minutes The new expected duration in minutes.
     */
    @PostMapping("/duration")
    public void setDuration(@RequestParam int minutes) {
        // Replaces the JSpinner logic from the original Java Swing GUI
        appointmentService.setDuration(minutes);
    }
}