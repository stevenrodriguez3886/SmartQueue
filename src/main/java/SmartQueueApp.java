import java.util.ArrayList;

import javax.swing.SwingUtilities;

public class SmartQueueApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Create the shared data list
            ArrayList<Appointment> sharedList = new ArrayList<>();

            // 2. Create the Employee view first
            EmployeeDashboard staff = new EmployeeDashboard(sharedList, "Staff Dashboard");
            
            // 3. Create Customer view, passing the list and the staff view (for updates)
            CustomerDashboard customer = new CustomerDashboard(sharedList, staff);

            // 4. Show both windows
            staff.setVisible(true);
            customer.setVisible(true);
        });
    }
}