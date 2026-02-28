package smartqueue;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeDashboard {

    private final AppointmentService appointmentService;

    public EmployeeDashboard(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
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
        // Returns the name of the person being served
        return ResponseEntity.ok("Now Serving: " + next.name);
    }

    @PostMapping("/duration")
    public void setDuration(@RequestParam int minutes) {
        // Replaces the JSpinner logic from the original GUI
        appointmentService.setDuration(minutes);
    }
}