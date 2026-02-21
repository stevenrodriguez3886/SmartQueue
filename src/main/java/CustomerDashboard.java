import java.awt.GridLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class CustomerDashboard extends JFrame {

    private static final int OPEN_HOUR = 9;
    private static final int CLOSE_HOUR = 17;

    private ArrayList<Appointment> appointments;
    private EmployeeDashboard staffView;

    private JTextField nameField;
    private JTextField dateField;
    private JComboBox<Integer> hourCombo;
    private JButton bookButton;
    private JButton waitTimeButton;
    private JButton viewPositionInQueueButton;
    private JButton cancelButton;
    private EmployeeDashboard custWaitListView; // To hold the reference to the restricted view

    public CustomerDashboard(ArrayList<Appointment> list, EmployeeDashboard staff) {
        super("Customer Booking Station");
        this.appointments = list;
        this.staffView = staff;
        this.custWaitListView = new restrictedView(appointments); // Initialize the restricted view with the shared list
        initUI(list);
    }

    private void initUI(ArrayList<Appointment> list) {
        setLayout(new GridLayout(6, 2, 10, 10));

        nameField = new JTextField();
        dateField = new JTextField(LocalDate.now().toString());
        hourCombo = new JComboBox<>();
        for (int i = 9; i <= 16; i++) hourCombo.addItem(i);

        bookButton = new JButton("Book Appointment");
        bookButton.addActionListener(e -> book());

        waitTimeButton = new JButton("View Estimated Wait Time");
        waitTimeButton.addActionListener(e -> showWaitTime());

        viewPositionInQueueButton = new JButton("View Position in Queue");
        viewPositionInQueueButton.addActionListener(e -> showPositionInQueue());

        cancelButton = new JButton("Cancel Appointment");
        cancelButton.addActionListener(e -> cancelAppointment());

        add(new JLabel("Name:")); add(nameField);
        add(new JLabel("Appointment Date (YYYY-MM-DD):")); add(dateField);
        add(new JLabel("Start Hour:")); add(hourCombo);
        add(waitTimeButton); add(bookButton); 
        add(cancelButton); add(viewPositionInQueueButton); 

        setSize(400, 230); // Slightly increased height for the new row
        setLocation(100, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void book() {
        String name = nameField.getText();
        String date = dateField.getText();
        int hour = (int) hourCombo.getSelectedItem();

        // Basic validations
        if (name.isEmpty()) {
            showError("Please enter a customer name.");
            return;
        }

        if (!isValidDateFormat(date)) {
            showError("Please enter a valid date in YYYY-MM-DD format.");
            return;
        }

        LocalDate now = LocalDate.now();
        LocalDate dateObj;
        try {
            dateObj = LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            showError("Please enter a valid date in YYYY-MM-DD format.");
            return;
        }

        if (dateObj.isBefore(now)) {
            showError("Please enter a future date. Past dates are not allowed.");
            return;
        }

        // New validations
        // 1) Name must not contain digits
        if (name.matches(".*\\d.*")) {
            showError("Name must not contain numbers.");
            return;
        }

        // 2) Date must be within next 1 year
        if (dateObj.isAfter(now.plusYears(1))) {
            showError("Please choose a date within the next year.");
            return;
        }

        // 3) Disallow weekends
        DayOfWeek dow = dateObj.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            showError("Appointments cannot be scheduled on weekends.");
            return;
        }

        // 4) Prevent duplicate customer name on the same date
        for (Appointment a : appointments) {
            if (a.date.equals(date) && a.name.equalsIgnoreCase(name)) {
                showError("An appointment for this customer already exists on " + date + ".");
                return;
            }
        }

        if (hour < OPEN_HOUR || hour >= CLOSE_HOUR) {
            showError("Please choose a valid hour between " + OPEN_HOUR + ":00 and " + (CLOSE_HOUR - 1) + ":00.");
            return;
        }

        if (isSlotTaken(date, hour)) {
            showError("This time slot is already taken for " + date + " at " + formatHour(hour) + ".");
            return;
        }

        appointments.add(new Appointment(name, date, hour));
        
        // Sort the list chronologically
        appointments.sort(Comparator.comparing((Appointment a) -> a.date)
                .thenComparingInt(a -> a.hour));

        // Notify the staff view to update its table
        staffView.refreshTable();
        custWaitListView.refreshTable();
        
        JOptionPane.showMessageDialog(this, "Appointment Booked!");
        nameField.setText("");
    }

    private void cancelAppointment() {
        String name = nameField.getText();
        String date = dateField.getText();
        int hour = (int) hourCombo.getSelectedItem();

        // Basic validation
        if (name.isEmpty()) {
            showError("Please enter the name on the appointment to cancel.");
            return;
        }

        // Attempt to remove the appointment
        boolean removed = appointments.removeIf(a -> 
            a.name.equalsIgnoreCase(name) && 
            a.date.equals(date) && 
            a.hour == hour
        );

        // Notify the staff view and customer view to update their tables if an appointment was removed
        if (removed) {
            staffView.refreshTable();
            custWaitListView.refreshTable();
            JOptionPane.showMessageDialog(this, "Appointment successfully canceled.");
            nameField.setText("");
        } else {
            showError("No matching appointment found to cancel.");
        }
    }

    private void showWaitTime() {
        String date = dateField.getText();
        int hour = (int) hourCombo.getSelectedItem();

        LocalDate now = LocalDate.now();
        LocalDate dateObj;
        try {
            dateObj = LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            showError("Please enter a valid date in YYYY-MM-DD format.");
            return;
        }

        int waitCount = 0;
        for (Appointment a : appointments) {
            if (a.date.equals(date) && a.hour < hour) {
                waitCount++;
            }
        }

        // Dynamically get appointment duration from staff view
        int durationMinutes = staffView.getAppointmentDurationMinutes();
        int estimatedWaitMinutes = waitCount * durationMinutes;
        JOptionPane.showMessageDialog(this, "Estimated Wait Time: " + estimatedWaitMinutes + " minutes. There are: " + waitCount + " users ahead of you for " + date + " at " + formatHour(hour) + ".");
    }

    private void showPositionInQueue(){
        if (!custWaitListView.isVisible()) {
            // Create new instance of the EmployeeDashboard with restricted view and functionality
            custWaitListView.setVisible(true);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private boolean isValidDateFormat(String date) {
        if (date == null || date.isEmpty()) {
            return false;
        }
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private boolean isSlotTaken(String date, int hour) {
        for (Appointment a : appointments) {
            if (a.date.equals(date) && a.hour == hour) {
                return true;
            }
        }
        return false;
    }

    private String formatHour(int hour) {
        return String.format("%02d:00", hour);
    }
}