package smartqueue;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * @brief Repository interface for Appointment persistence.
 * * Extends JpaRepository to provide standard CRUD operations.
 * Spring Data JPA automatically generates the implementation at runtime.
 */
public interface AppointmentDatabase extends JpaRepository<Appointment, String> {
    
    /**
     * @brief Retrieves all appointments, sorted chronologically.
     * * Spring generates the SQL for this automatically based on the method name format:
     * "findAll" + "By" + "OrderBy[Property]Asc" + "[Property]Asc".
     * * @return A list of all Appointments ordered first by date, then by hour.
     */
    List<Appointment> findAllByOrderByDateAscHourAsc();
}