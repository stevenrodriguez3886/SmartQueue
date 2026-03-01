package smartqueue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentDatabase extends JpaRepository<Appointment, String> {
    // Spring generates the SQL for these automatically
    List<Appointment> findAllByOrderByDateAscHourAsc();
}