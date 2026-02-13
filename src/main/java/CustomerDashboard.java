import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class CustomerDashboard extends JFrame {

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
        add(new JLabel("Date (YYYY-MM-DD):")); add(dateField);
        add(new JLabel("Hour:")); add(hourCombo);
        add(new JLabel("")); add(bookButton); // Empty label for spacing

        setSize(400, 200);
        setLocation(100, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void book() {
        String name = nameField.getText();
        String date = dateField.getText();
        int hour = (int) hourCombo.getSelectedItem();

        if (name.isEmpty()) return;

        appointments.add(new Appointment(name, date, hour));
        
        // Sort the list chronologically
        appointments.sort(Comparator.comparing((Appointment a) -> a.date)
                .thenComparingInt(a -> a.hour));

        // Notify the staff view to update its table
        staffView.refreshTable();
        
        JOptionPane.showMessageDialog(this, "Appointment Booked!");
        nameField.setText("");
    }
}