import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class EmployeeDashboard extends JFrame {

    private ArrayList<Appointment> appointments;
    private DefaultTableModel tableModel;
    private JButton serveButton;

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

        add(new JScrollPane(table), BorderLayout.CENTER);
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
}