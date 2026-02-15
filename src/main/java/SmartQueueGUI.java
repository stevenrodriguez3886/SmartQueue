/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */



/**
 *
 * @author alejandrodominguez
 */
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class SmartQueueGUI extends JFrame {

    // --- Model ---
    static class Appointment {
        String name;
        String date; // YYYY-MM-DD (basic validation)
        int hour;    // 9..16

        Appointment(String name, String date, int hour) {
            this.name = name;
            this.date = date;
            this.hour = hour;
        }
    }

    private final ArrayList<Appointment> appointments = new ArrayList<>();

    // --- UI components ---
    private JTextField nameField;
    private JTextField dateField;
    private JComboBox<Integer> hourCombo;
    private JButton bookButton;
    private JButton removeButton;

    private JTable table;
    private DefaultTableModel tableModel;

    // Service hours (9AM - 5PM; last bookable start hour is 16)
    private static final int OPEN_HOUR = 9;
    private static final int CLOSE_HOUR = 17;

    public SmartQueueGUI() {
        super("SmartQueue - Appointment Booking");
        initUI();
    }

    private void initUI() {
        // Top panel (form)
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Customer Name:");
        JLabel dateLabel = new JLabel("Appointment Date (YYYY-MM-DD):");
        JLabel hourLabel = new JLabel("Start Hour:");

        nameField = new JTextField(20);
        dateField = new JTextField(12);

        hourCombo = new JComboBox<>();
        for (int h = OPEN_HOUR; h < CLOSE_HOUR; h++) { // 9..16
            hourCombo.addItem(h);
        }

        bookButton = new JButton("Book Appointment");
        removeButton = new JButton("Remove Selected");

        // Layout form fields
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(dateLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(hourLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(hourCombo, gbc);

        // Buttons row
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.add(bookButton);
        buttonPanel.add(removeButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"Name", "Date", "Time"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Add action listeners
        bookButton.addActionListener(e -> bookAppointment());
        removeButton.addActionListener(e -> removeSelected());

        // Frame layout
        setLayout(new BorderLayout(10, 10));
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Nice sizing defaults
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 420);
        setLocationRelativeTo(null);
    }

    private void bookAppointment() {
        String name = nameField.getText().trim();
        String date = dateField.getText().trim();
        Integer hour = (Integer) hourCombo.getSelectedItem();

        // Basic validations
        if (name.isEmpty()) {
            showError("Please enter a customer name.");
            return;
        }

        if (!isValidDateFormat(date)) {
            showError("Please enter a valid date in YYYY-MM-DD format.");
            return;
        }

        if (hour == null || hour < OPEN_HOUR || hour >= CLOSE_HOUR) {
            showError("Please choose a valid hour between " + OPEN_HOUR + ":00 and " + (CLOSE_HOUR - 1) + ":00.");
            return;
        }

        if (isSlotTaken(date, hour)) {
            showError("This time slot is already taken for " + date + " at " + formatHour(hour) + ".");
            return;
        }

        // Create + store
        Appointment appt = new Appointment(name, date, hour);
        appointments.add(appt);

        // Update table
        tableModel.addRow(new Object[]{name, date, formatHour(hour)});

        // Confirmation + clear fields
        JOptionPane.showMessageDialog(this,
                "Appointment booked successfully!\n\nName: " + name + "\nDate: " + date + "\nTime: " + formatHour(hour),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        nameField.setText("");
        // keep date/hour for faster repeated booking if desired
        nameField.requestFocusInWindow();
    }

    private void removeSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Please select an appointment in the table to remove.");
            return;
        }

        // Remove from model list
        // Since we add rows in same order as appointments list, indices match
        appointments.remove(row);

        // Remove from table
        tableModel.removeRow(row);

        JOptionPane.showMessageDialog(this,
                "Selected appointment removed.",
                "Removed",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isSlotTaken(String date, int hour) {
        for (Appointment a : appointments) {
            if (a.date.equals(date) && a.hour == hour) return true;
        }
        return false;
    }

    private boolean isValidDateFormat(String date) {
        // Minimal format check: YYYY-MM-DD (not a full calendar validation)
        // Good enough for a course MVP; can be improved later.
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private String formatHour(int hour) {
        return String.format("%02d:00", hour);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new SmartQueueGUI().setVisible(true));
    }
}

