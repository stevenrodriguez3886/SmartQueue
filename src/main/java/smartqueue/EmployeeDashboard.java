package smartqueue;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeDashboard {

    private final AppointmentService appointmentService;
    private final SimpMessagingTemplate messagingTemplate;

    public EmployeeDashboard(AppointmentService appointmentService, SimpMessagingTemplate messagingTemplate) {
        this.appointmentService = appointmentService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/full-queue")
    public List<Appointment> getFullQueue() {
        // Staff see the full list with names
        return appointmentService.getAll();
    }

    @DeleteMapping("/serve")
    public ResponseEntity<String> serveNext() {
        Appointment next = appointmentService.removeNext();
        if (next == null) return ResponseEntity.badRequest().body("Queue is empty.");
        
        // Notify ONLY the client listening for this specific ID
        messagingTemplate.convertAndSend("/topic/notify/" + next.id, 
        "It is your turn, " + next.name + "!");

        // Broadcast that the queue changed to update the dashboard
        messagingTemplate.convertAndSend("/topic/queue-update", "Refresh");

        // Returns the name of the person being served to the staff dashboard
        return ResponseEntity.ok("Now Serving: " + next.name);
    }

    @PostMapping("/duration")
    public void setDuration(@RequestParam int minutes) {
        // Replaces the JSpinner logic from the original GUI
        appointmentService.setDuration(minutes);
    }
}