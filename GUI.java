import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GUI extends JFrame {
    private CardLayout cl = new CardLayout();
    private JPanel main = new JPanel(cl);
    final private JTabbedPane tabs = new JTabbedPane();
    private static Fine.FineType currentScheme = new Fine.Fixed();
    final private String loadedData = "";

    public GUI() {
        setTitle("Parking System");
        setSize(800, 500);
        setDefaultCloseOperation(3);
        
        JPanel login = new JPanel(new GridBagLayout());
        String[] roles = {"Admin", "Entry/Exit", "Report"};
        JComboBox<String> rb = new JComboBox<>(roles);
        JButton lb = new JButton("Login");

        lb.addActionListener(e -> {
            String role = (String) rb.getSelectedItem();
            switch (role) {
                case "Admin" -> {
                    main.add(createAdminTab(), "A");
                    cl.show(main, "A");
                }
                case "Report" -> {
                    main.add(createReportingTab(), "R"); // Add the new tab to CardLayout
                    cl.show(main, "R");
                }
                default -> cl.show(main, "H");
            }
        });

        login.add(rb); login.add(lb);
        main.add(login, "L");
        main.add(createHub(), "H");
        add(main);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createHub() {
        JPanel p = new JPanel(new BorderLayout());
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 0) tabs.setComponentAt(0, createSpaceTab());
        });
        tabs.addTab("Check", createSpaceTab());
        tabs.addTab("Entry", createForm("Save Details", 1));
        tabs.addTab("Exit", createForm("Load Vehicle", 2));
        p.add(tabs);
        JButton out = new JButton("Logout");
        out.addActionListener(e -> cl.show(main, "L"));
        p.add(out, "South");
        return p;
    }

    private JPanel createSpaceTab() {
        JPanel p = new JPanel(new GridLayout(2, 2));
        p.setBackground(new Color(30, 30, 30));
        String[] t = {"Regular", "Reserved", "Compact", "Handicapped"};
        for (String typeName : t) {
            JLabel l = new JLabel(typeName + ": " + Parking.getCount(typeName), 0);
            l.setForeground(Color.WHITE);
            l.setFont(new Font("Arial", 1, 22));
            l.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            p.add(l);
        }
        return p;
    }

    private JPanel createForm(String btnTxt, int mode) {
        int rows = (mode == 1) ? 7 : 4;
        JPanel p = new JPanel(new GridLayout(rows, 2, 5, 5));
        JTextField f1 = new JTextField(), f2 = new JTextField();
        JComboBox<String> lvlBox = new JComboBox<>(new String[]{"Level 1", "Level 2", "Level 3", "Level 4", "Level 5"});
        JComboBox<String> SpotID = new JComboBox<>(new String[]{"A01", "A02", "A03", "A04", "A05", "B01", "B02", "B03", "B04", "B05"});
        JComboBox<String> Vhcbox = new JComboBox<>(new String[]{"Car", "Motorcycle", "Suv/Truck", "Handicapped Vehicle"});
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Regular", "Reserved", "Compact", "Handicapped"});

        p.add(new JLabel("Plate Number:")); p.add(f1);
        p.add(new JLabel(mode == 1 ? "Entry Time:" : "Exit Time:")); p.add(f2);

        if (mode == 1) {
            p.add(new JLabel("Parking Category:")); p.add(typeBox);
            p.add(new JLabel("Vehicle Type:")); p.add(Vhcbox);
            p.add(new JLabel("Spot ID:")); p.add(SpotID);
            p.add(new JLabel("Level:")); p.add(lvlBox);
        }

        JButton btn = new JButton(btnTxt);
        JButton payBtn = new JButton("Pay & Receipt");
        payBtn.setEnabled(false);

        btn.addActionListener(e -> {
            if (mode == 1) {
                try {
                    double time = Double.parseDouble(f2.getText());
                    FileManager.saveDetails(f1.getText(), time, (String) Vhcbox.getSelectedItem(), (String) typeBox.getSelectedItem(), (String) lvlBox.getSelectedItem(), (String) SpotID.getSelectedItem());
                    JOptionPane.showMessageDialog(this, "Saved!");
                    f1.setText(""); f2.setText("");
                } catch (HeadlessException | NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Invalid Input!"); }
            } else {
                DefaultTableModel m = FileManager.getUnifiedView();
                boolean found = false;
                for (int i = 0; i < m.getRowCount(); i++) {
                    String plate = m.getValueAt(i, 0).toString();
                    String status = m.getValueAt(i, 5).toString();
                    if (plate.equalsIgnoreCase(f1.getText().trim()) && status.equals("PARKED")) {
                        payBtn.setEnabled(true);
                        found = true;
                        JOptionPane.showMessageDialog(this, "Vehicle Found!");
                        break;
                    }
                }
                if (!found) JOptionPane.showMessageDialog(this, "Plate Not Found or Already Paid!");
            }
        });

        payBtn.addActionListener(b -> {
            handlePay(f1.getText(), f2.getText());
            payBtn.setEnabled(false);
            f1.setText(""); f2.setText("");
        });

        p.add(btn);
        if (mode == 2) p.add(payBtn);
        return p;
    }

    private void handlePay(String plate, String time) {
    try {
        Payment p = new Payment();
        String data = FileManager.loadDetails(plate); 
        if (data == null) return;
        String[] d = data.split(","); 
        String category = d[3]; // d[3] is the Category (Regular, Compact, etc.)
        p.vehicle.plateNumber = plate;
        p.vehicle.calculateDuration(Double.parseDouble(d[1]), Double.parseDouble(time));
        double baseRate = p.vehicle.duration * Parking.getParkingRate(category);
        p.parking.totalRate = currentScheme.finePayType(baseRate, p.vehicle, false);
        String msg = "Plate: " + plate + "\nDuration: " + String.format("%.2f", p.vehicle.duration) + " hrs";
        if (p.vehicle.duration > 24) msg += "\n(OVERSTAY FINE APPLIED)";
        msg += "\n\nTotal to Pay: RM " + String.format("%.2f", p.parking.totalRate);
        int c = JOptionPane.showOptionDialog(this, msg, "Payment", 0, 3, null, new String[]{"Cash", "Card"}, "Cash");
        if (c >= 0) {
            p.paymentMethod = (c == 0) ? "Cash" : "Card";
            if (c == 0) {
                String v = JOptionPane.showInputDialog("Total: RM" + String.format("%.2f", p.parking.totalRate) + "\nEnter Cash:");
                if (v != null) {
                    double change = Double.parseDouble(v) - p.parking.totalRate;
                    if (change < 0) {
                        JOptionPane.showMessageDialog(this, "Insufficient cash!");
                        return;
                    }
                    JOptionPane.showMessageDialog(this, "Payment Success! Change: RM" + String.format("%.2f", change));
                } else return;
            }
            boolean isFined = p.vehicle.duration >24;
            String finalStatus = isFined  ? "PAID(FINED)" : "PAID";
            FileManager.updateFees(plate, p.parking.totalRate, finalStatus);
            tabs.setComponentAt(0, createSpaceTab());
            p.paymentReceipt();
        }
    } catch (HeadlessException | NumberFormatException e) { 
        JOptionPane.showMessageDialog(this, "Payment Error: " + e.getMessage()); 
    }
}

    private JPanel createAdminTab() {
        JPanel panel = new JPanel(new FlowLayout());
        JButton viewDetBtn = new JButton("View Master Records");
        JButton btnTotal = new JButton("Check Revenue");
        JButton  ParkDet = new JButton("Parked Vehicles");
        JLabel  label = new JLabel("Select Fine Scheme");
        String[] schemes = {"Fixed" , "Progressive " , "Hourly"};
        JComboBox  <String> schemeSelect = new JComboBox<>(schemes);

        schemeSelect.addActionListener(e -> {
    int choice = schemeSelect.getSelectedIndex();
    switch (choice) {
        case 0 -> {
            currentScheme = new Fine.Fixed();
            JOptionPane.showMessageDialog(this, "Fine System updated to Fixed");
                }
        case 1 -> {
            currentScheme = new Fine.Progressive();
            JOptionPane.showMessageDialog(this, "Fine System updated to Progressive");
                }
        case 2 -> {
            currentScheme = new Fine.Hourly();
            JOptionPane.showMessageDialog(this, "Fine System updated to Hourly");
                }
    }
});
        JButton back = new JButton("Logout");
        viewDetBtn.addActionListener(c -> {
            JOptionPane.showMessageDialog(null, new JScrollPane(new JTable(FileManager.getUnifiedView())), "Master Records", 1);
        });

        btnTotal.addActionListener(e -> {
    double[] rev = FileManager.getTotalRevenue(); 
    double grandTotal = rev[0] + rev[1];
            JOptionPane.showMessageDialog(this, "Total Revenue Collected: RM " + String.format("%.2f", grandTotal));
        });

         ParkDet.addActionListener(e -> {
            JFrame frame = new JFrame("Parking Space Monitoring");
    frame.setSize(400, 300);
    frame.add(createSpaceTab()); 
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
        });

        back.addActionListener(e -> cl.show(main, "L"));
        panel.add(label);
        panel.add(schemeSelect);
        panel.add(viewDetBtn);
        panel.add(ParkDet);
        panel.add(btnTotal);
        panel.add(back);
        return panel;
    }
   public JPanel createReportingTab() {
    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    // Changed GridLayout to 1 row, 3 columns to show the breakdown
    JPanel header = new JPanel(new GridLayout(1, 3, 10, 10));
    header.setBorder(BorderFactory.createTitledBorder("System Statistics"));

    // Fetch the array from your updated method
    double[] revData = FileManager.getTotalRevenue();
    double standard = revData[0];
    double fines = revData[1];
    double grandTotal = standard + fines;

    JLabel stdLabel = new JLabel("Standard: RM " + String.format("%.2f", standard));
    stdLabel.setForeground(new Color(0, 51, 153)); // Blue

    JLabel fineLabel = new JLabel("Fines: RM " + String.format("%.2f", fines));
    fineLabel.setForeground(Color.RED); // Red

    JLabel totalLabel = new JLabel("Total: RM " + String.format("%.2f", grandTotal));
    totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
    totalLabel.setForeground(new Color(0, 102, 0)); // Green

    header.add(stdLabel);
    header.add(fineLabel);
    header.add(totalLabel);

    // --- Rest of your table and button logic remains the same ---
    String[] columns = {"Plate", "Entry", "Type", "SpotID","Level", "Duration", "Fine Status"};
    Object[][] data = FileManager.getReportData();
    
    DefaultTableModel model = new DefaultTableModel(data, columns) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    JTable table = new JTable(model);
    table.getTableHeader().setReorderingAllowed(false); 
    table.setFillsViewportHeight(true);
    JScrollPane scrollPane = new JScrollPane(table);
    
    JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton refreshBtn = new JButton("Refresh Data");
    JButton parkDet = new JButton("Space Monitor"); 
    JButton logoutBtn = new JButton("Logout");
    JButton historyBtn = new JButton("View Payment History");

    refreshBtn.addActionListener(e -> {
        main.add(createReportingTab(), "R");
        cl.show(main, "R");
    });

    historyBtn.addActionListener(e -> {
        String[] histColumns = {"Plate", "level","SpotID","Type", "Vehicle", "Fee Paid", "Final Status"};
        Object[][] histData = FileManager.getHistoryData();
        table.setModel(new DefaultTableModel(histData, histColumns));
        header.setBorder(BorderFactory.createTitledBorder("Past Transactions (Paid)"));
    });

    // ... (rest of your parkDet and logout listeners)
    parkDet.addActionListener(e -> {
        JFrame frame = new JFrame("Live Parking Space Monitoring");
        frame.setSize(600, 400); 
        frame.add(createSpaceTab()); 
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    });
    logoutBtn.addActionListener(e -> cl.show(main, "L"));

    footer.add(historyBtn);
    footer.add(parkDet);
    footer.add(refreshBtn);
    footer.add(logoutBtn);

    mainPanel.add(header, BorderLayout.NORTH);
    mainPanel.add(scrollPane, BorderLayout.CENTER);
    mainPanel.add(footer, BorderLayout.SOUTH);

    return mainPanel;
}
    public static void main(String[] args) { new GUI(); }
}