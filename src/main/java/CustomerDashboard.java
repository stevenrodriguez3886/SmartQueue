import java.awt.GridLayout;
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

    public CustomerDashboard(ArrayList<Appointment> list, EmployeeDashboard staff) {
        super("Customer Booking Station");
        this.appointments = list;
        this.staffView = staff;
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(4, 2, 10, 10));

        nameField = new JTextField();
        dateField = new JTextField("2025-01-01");
        hourCombo = new JComboBox<>();
        for (int i = 9; i <= 16; i++) hourCombo.addItem(i);

        bookButton = new JButton("Book Appointment");
        bookButton.addActionListener(e -> book());

        add(new JLabel("Name:")); add(nameField);
        add(new JLabel("Appointment Date (YYYY-MM-DD):")); add(dateField);
        add(new JLabel("Start Hour:")); add(hourCombo);
        add(new JLabel("")); add(bookButton); // Empty label for spacing

        setSize(400, 200);
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

        if (date.compareTo(java.time.LocalDate.now().toString()) < 0) {
            showError("Please enter a future date. Past dates are not allowed.");
            return;
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
        
        JOptionPane.showMessageDialog(this, "Appointment Booked!");
        nameField.setText("");
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