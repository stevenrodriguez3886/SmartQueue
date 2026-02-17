import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

public class EmployeeDashboard extends JFrame {

    private ArrayList<Appointment> appointments;
    private DefaultTableModel tableModel;
    private JButton serveButton;
    private JSpinner durationSpinner;
    private int appointmentDurationMinutes = 15; // default

    public EmployeeDashboard(ArrayList<Appointment> list) {
        super("Staff Dashboard");
        this.appointments = list;
        initUI();
    }

    private void initUI() {
        tableModel = new DefaultTableModel(new Object[]{"Name", "Date", "Time"}, 0);
        JTable table = new JTable(tableModel);
        
        serveButton = new JButton("Serve Next Customer");
        serveButton.addActionListener(e -> serveNext());

        // Duration configuration panel
        JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        durationPanel.add(new JLabel("Estimated appointment duration (min):"));
        durationSpinner = new JSpinner(new SpinnerNumberModel(15, 5, 120, 5));
        durationSpinner.addChangeListener(e -> {
            appointmentDurationMinutes = (Integer) durationSpinner.getValue();
        });
        durationPanel.add(durationSpinner);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(durationPanel, BorderLayout.NORTH);
        add(serveButton, BorderLayout.SOUTH);

        setSize(500, 300);
        setLocation(550, 100); // Positioned to the right of customer view
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        for (Appointment a : appointments) {
            tableModel.addRow(new Object[]{a.name, a.date, a.getFormattedTime()});
        }
    }

    private void serveNext() {
        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Queue is empty.");
            return;
        }

        Appointment next = appointments.remove(0);
        refreshTable();
        JOptionPane.showMessageDialog(this, "Now Serving: " + next.name);
    }

    public int getAppointmentDurationMinutes() {
        return appointmentDurationMinutes;
    }
}