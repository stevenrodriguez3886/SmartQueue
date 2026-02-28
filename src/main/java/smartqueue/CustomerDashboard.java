package smartqueue;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerDashboard {

    private final AppointmentService appointmentService;
    private static final int OPEN_HOUR = 9;
    private static final int CLOSE_HOUR = 17;
    private final SimpMessagingTemplate messagingTemplate;

    public CustomerDashboard(AppointmentService appointmentService, SimpMessagingTemplate messagingTemplate) {
        this.appointmentService = appointmentService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/book")
    public ResponseEntity<Object> book(@RequestBody AppointmentRequest request) {
        String name = request.getName();
        String date = request.getDate();
        int hour = request.getHour();

        // Validation 1: Name format (No numbers, not empty)
        if (name == null || name.isEmpty() || name.matches(".*\\d.*")) {
            return ResponseEntity.badRequest().body("Name must not be empty or contain numbers.");
        }

        LocalDate dateObj;
        try {
            dateObj = LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().body("Invalid date format.");
        }

        // Validation 2: No past dates
        LocalDate now = LocalDate.now();
        if (dateObj.isBefore(now)) {
            return ResponseEntity.badRequest().body("Past dates are not allowed.");
        }

        // Validation 3: Only up to 1 year in advance
        if (dateObj.isAfter(now.plusYears(1))) {
            return ResponseEntity.badRequest().body("Choose a date within the next year.");
        }

        // Validation 4: No weekends
        DayOfWeek dow = dateObj.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return ResponseEntity.badRequest().body("Appointments cannot be scheduled on weekends.");
        }

        // Validation 5: Within business hours
        if (hour < OPEN_HOUR || hour >= CLOSE_HOUR) {
            return ResponseEntity.badRequest().body("Choose a time between 09:00 and 16:00.");
        }

        // Validation 6: Check for duplicates or taken slots
        for (Appointment a : appointmentService.getAll()) {
            if (a.date.equals(date) && a.name.equalsIgnoreCase(name)) {
                return ResponseEntity.badRequest().body("You already have an appointment on this date.");
            }
            if (a.date.equals(date) && a.hour == hour) {
                return ResponseEntity.badRequest().body("This time slot is already taken.");
            }
        }

        Appointment newAppointment = new Appointment(name, date, hour);
        appointmentService.add(newAppointment);

        // Broadcast that the queue changed
        messagingTemplate.convertAndSend("/topic/queue-update", "Refresh");

        return ResponseEntity.ok(newAppointment);
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancel(@RequestBody AppointmentRequest request) {
        boolean removed = appointmentService.removeSpecific(request.getName(), request.getDate(), request.getHour());
        if (removed){
            // Broadcast that the queue changed
            messagingTemplate.convertAndSend("/topic/queue-update", "Refresh");
            return ResponseEntity.ok("Appointment cancelled successfully.");
        }
        return ResponseEntity.badRequest().body("No matching appointment found.");
    }

    @GetMapping("/wait-time")
    public ResponseEntity<String> getWaitTime(@RequestParam String date, @RequestParam int hour) {
        int waitCount = appointmentService.getWaitCount(date, hour);
        int totalWait = waitCount * appointmentService.getDuration();
        return ResponseEntity.ok("Estimated Wait: " + totalWait + " minutes. " + waitCount + " people ahead of you.");
    }

    @GetMapping("/position")
    public ResponseEntity<String> getPosition(@RequestParam String name, @RequestParam String date, @RequestParam int hour) {
        int pos = appointmentService.getPosition(name, date, hour);
        
        if (pos == -1) {
            return ResponseEntity.badRequest().body("Appointment not found. Please check your details.");
        }
        if (pos == 0) {
            return ResponseEntity.ok("You are next!");
        }
        
        return ResponseEntity.ok("There are " + pos + " people ahead of you.");
    }
}