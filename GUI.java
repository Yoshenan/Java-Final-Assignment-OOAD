import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GUI extends JFrame {
    private CardLayout cl = new CardLayout();
    private JPanel main = new JPanel(cl);
    final private JTabbedPane tabs = new JTabbedPane();
    private static Fine.FineType currentScheme = new Fine.Fixed();
    final private String loadedData = "";
    final private JTextField userId = new JTextField(5);
    private static Vehicle tempvhc = new Vehicle();

   public GUI() {
    setTitle("Parking System");
    setSize(800, 500);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel login = new JPanel();
    login.setLayout(new BoxLayout(login, BoxLayout.Y_AXIS));
    login.setBorder(BorderFactory.createEmptyBorder(150, 300, 150, 300));
    JLabel userLabel = new JLabel("User ID:");
    userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    JTextField userId = new JTextField();
    userId.setMaximumSize(new Dimension(200, 30));
    userId.setAlignmentX(Component.CENTER_ALIGNMENT);
    JButton lb = new JButton("Login");
    lb.setMaximumSize(new Dimension(200, 35));
    lb.setAlignmentX(Component.CENTER_ALIGNMENT);
    lb.addActionListener(e -> {
        String inputId = userId.getText().trim();
        if (inputId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "User ID cannot be empty", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String UserId = inputId.toUpperCase().trim();
        if ("A01".equals(UserId)) { main.add(createAdminTab(), "A"); cl.show(main, "A"); }
        else if ("R01".equals(UserId)) { main.add(createReportingTab(), "R"); cl.show(main, "R"); }
        else if ("E01".equals(UserId)) {  cl.show(main, "H"); }
        else JOptionPane.showMessageDialog(this, "Invalid id!");
    });
    login.add(userLabel);
    login.add(userId);
    login.add(lb);
    main.add(login, "L");
    main.add(createHub(), "H");
    add(main);
    setLocationRelativeTo(null);
    setVisible(true);
}
    private JPanel createHub() {
        JPanel p = new JPanel(new BorderLayout());
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 0) tabs.setComponentAt(0, createSpaceTab());});
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
            int totalHistory = Parking.getCount(typeName);
            int max =(int) Parking.setTotalLots(typeName);
            int active = (int)FileManager.getActiveCarsByType(typeName);
            int available  = max -active;
            String statusText;
            if (active >= max) {
             String availColor = "#FF4444";
             statusText = "LOT FULL";
           } else {
            String availColour = "#52D017"; 
            statusText = available + " FREE";
}
            String availColor = (available <= 0) ? "#FF4444" : "#52D017";
             String combinedText = "<html><center>" +
            "<font size='5' color='white'><b>" + typeName + "</b> (Total: " + totalHistory + ")</font><br>" +
            "<hr color='#444444'>" +
            "<font color='gray'>Occupancy: " + active + " / " + max + "</font><br>" +
            "<font size='6' color='" + availColor + "'>" + available + " FREE</font>" +
            "</center></html>";
            JLabel l = new JLabel(combinedText, SwingConstants.CENTER);
            l.setOpaque(true);
            l.setBackground(new Color(45, 45, 45));
            l.setForeground(Color.WHITE);
            l.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            l.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e ){
                    DefaultTableModel model = FileManager.getFilteredParkDetails(typeName);
                    JTable table = new JTable(model);
                    JScrollPane scroll = new JScrollPane(table);
                    JOptionPane.showMessageDialog(null, scroll, typeName + " Details", JOptionPane.PLAIN_MESSAGE);
                }
            });
            p.add(l);}return p;
    }

    private JPanel createForm(String btnTxt, int mode) {
        int rows = (mode == 1) ? 8 : 4;
        JPanel p = new JPanel(new GridLayout(rows, 2, 5, 5));
        JTextField f1 = new JTextField(), f2 = new JTextField(), f3 = new JTextField();
        JComboBox<String> lvlBox = new JComboBox<>(new String[]{"Level 1", "Level 2", "Level 3", "Level 4", "Level 5"});
        JComboBox<String> SpotID = new JComboBox<>(new String[]{"A01", "A02", "A03", "A04", "A05", "B01", "B02", "B03", "B04", "B05","C01","C02","C03","C04","C05"});
        JComboBox<String> Vhcbox = new JComboBox<>(new String[]{"Car", "Motorcycle", "Suv/Truck", "Handicapped Vehicle"});
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Regular", "Reserved", "Compact", "Handicapped"});
        p.add(new JLabel("Plate Number:")); p.add(f1);
        p.add(new JLabel("Date (DD/MM):")); p.add(f3);
        p.add(new JLabel(mode == 1 ? "Entry Time(24H):" : "Exit Time(24H):")); p.add(f2);
        if (mode == 1) {
            p.add(new JLabel("Parking Category:")); p.add(typeBox);
            p.add(new JLabel("Vehicle Type:")); p.add(Vhcbox);
            p.add(new JLabel("Spot ID:")); p.add(SpotID);
            p.add(new JLabel("Level:")); p.add(lvlBox);
        }

        JButton btn = new JButton(btnTxt);//save
        JButton payBtn = new JButton("Pay & Receipt");
        payBtn.setEnabled(false);
        btn.addActionListener(e -> {
            if (mode == 1) {
                try {
                    if (f1.getText().isEmpty() || f2.getText().isEmpty() || f3.getText().isEmpty()){
                        JOptionPane.showMessageDialog(this,"Please Enter Details");
                        return;
                    }
                    if(!f3.getText().matches("\\d{2}/\\d{2}")){
                        JOptionPane.showMessageDialog(this, "Invalid Date Format! Use DD/MM (e.g. 26/02)", "Date Error", JOptionPane.WARNING_MESSAGE);
                    return;
                    }
                    double time = Double.parseDouble(f2.getText());
                    if(time<0 || time > 23.59){
                      JOptionPane.showMessageDialog(this, "Invalid Date Format! Use DD/MM (e.g. 26/02)", "Date Error", JOptionPane.WARNING_MESSAGE);
                    return;
                    }
                    
                    String spotId = SpotID.getSelectedItem().toString();
                    String level = lvlBox.getSelectedItem().toString();
                    if (FileManager.isSpotOccupied(spotId, level)) {
                    JOptionPane.showMessageDialog(this,"Spot " + spotId + " (Level " + level + ") is already occupied!", "Spot Required", JOptionPane.WARNING_MESSAGE);
                    return;
                    }
                    
                    String date = f3.getText();
                    tempvhc.setPlateNumber(f1.getText());
                    if (tempvhc.getPlateNumber().equals("INVALID")) { JOptionPane.showMessageDialog(this, "Format Error: Use 'ABC 1234'"); return; }
                    FileManager.saveDetails(tempvhc.getPlateNumber(), time, date, (String) typeBox.getSelectedItem(), (String) Vhcbox.getSelectedItem(), (String) lvlBox.getSelectedItem(), (String) SpotID.getSelectedItem());
                    JOptionPane.showMessageDialog(this, "Saved!");
                    f1.setText(""); f2.setText(""); f3.setText("");
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
            handlePay(f1.getText(), f2.getText(), f3.getText());
            payBtn.setEnabled(false);
            f1.setText(""); f2.setText("");f3.setText("");
        });

        p.add(btn);
        if (mode == 2) p.add(payBtn);
        return p;
    }

    private void handlePay(String plate, String time, String date) {
    try {
        if (plate.trim().isEmpty() || time.trim().isEmpty() || date.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Missing data! ", "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
        Payment p = new Payment();
String data = FileManager.loadDetails(plate); 
if (data == null) return;

String[] d = data.split(","); 
String entryDateStr = d[2].trim(); 
double entryTimeValue = Double.parseDouble(d[1].trim()); 
double exitTimeValue = Double.parseDouble(time);
p.vehicle.plateNumber = plate;
p.vehicle.entryTime = entryTimeValue; 
p.vehicle.exitTime = exitTimeValue;
p.vehicle.entryDay = entryDateStr;  
p.vehicle.exitDay = date.trim();
double duration;
if (date.trim().equals(entryDateStr)) {
    duration = exitTimeValue - entryTimeValue;
} else {
    int entryDay = Integer.parseInt(entryDateStr.split("/")[0]);
    int entryMonth = Integer.parseInt(entryDateStr.split("/")[1]);
    int exitDay = Integer.parseInt(date.trim().split("/")[0]);
    int exitMonth = Integer.parseInt(date.trim().split("/")[1]);
    int entryTotalDays = (entryMonth * 30) + entryDay;
    int exitTotalDays = (exitMonth * 30) + exitDay;
    int dayDiff = exitTotalDays - entryTotalDays;
    duration = (exitTimeValue + (dayDiff * 24)) - entryTimeValue;
}

p.vehicle.duration = duration;
p.parking.parkingType = d[3];
        double baseRate = p.vehicle.duration * Parking.getParkingRate(p.parking.parkingType);
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
            boolean isFined = p.vehicle.duration > 24;
            String finalStatus = isFined ? "PAID(FINED)" : "PAID";
            FileManager.updateFees(plate, p.parking.totalRate, finalStatus);
            tabs.setComponentAt(0, createSpaceTab());
            JOptionPane.showMessageDialog(this, p.paymentReceipt(), "Receipt", JOptionPane.PLAIN_MESSAGE);
        }
    } catch (HeadlessException | NumberFormatException e) { 
        JOptionPane.showMessageDialog(this, "Payment Error: " + e.getMessage()); 
    }
}
    private JPanel createAdminTab() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    JPanel header = new JPanel();
    JLabel label = new JLabel("Fine Scheme:");
    label.setFont(new Font("Arial", Font.BOLD, 14));
    String[] schemes = {"Fixed", "Progressive", "Hourly"};
    JComboBox<String> schemeSelect = new JComboBox<>(schemes);
    if (currentScheme instanceof Fine.Progressive) schemeSelect.setSelectedIndex(1);
    else if (currentScheme instanceof Fine.Hourly) schemeSelect.setSelectedIndex(2);
    else if (currentScheme instanceof Fine.Fixed) schemeSelect.setSelectedIndex(0);
    schemeSelect.addActionListener(e -> {
        int choice = schemeSelect.getSelectedIndex();
        switch (choice) {
            case 0 -> { currentScheme = new Fine.Fixed(); JOptionPane.showMessageDialog(this, "Fine System updated to Fixed"); }
            case 1 -> { currentScheme = new Fine.Progressive(); JOptionPane.showMessageDialog(this, "Fine System updated to Progressive"); }
            case 2 -> { currentScheme = new Fine.Hourly(); JOptionPane.showMessageDialog(this, "Fine System updated to Hourly"); }
        }
    });
    header.add(label);
    header.add(schemeSelect);
    JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
    JButton viewDetBtn = new JButton("View Master Records");
    JButton btnTotal = new JButton("Check Revenue");
    JButton ParkDet = new JButton("Parked Vehicles");
    JButton back = new JButton("Logout");
    viewDetBtn.addActionListener(c -> JOptionPane.showMessageDialog(null, 
        new JScrollPane(new JTable(FileManager.getUnifiedView())), "Master Records", 1));
    
    btnTotal.addActionListener(e -> {
        double[] rev = FileManager.getTotalRevenue();
        JOptionPane.showMessageDialog(this, "Total Revenue: RM " + String.format("%.2f", rev[0] + rev[1]));
    });
    
    ParkDet.addActionListener(e -> {
        JFrame frame = new JFrame("Parking Space Monitoring");
        frame.setSize(400, 300);
        frame.add(createSpaceTab());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    });
    back.addActionListener(e -> cl.show(main, "L"));
    buttonPanel.add(viewDetBtn);
    buttonPanel.add(btnTotal);
    buttonPanel.add(ParkDet);
    buttonPanel.add(back);
    panel.add(header, BorderLayout.NORTH);
    panel.add(buttonPanel, BorderLayout.CENTER);
    return panel;
}
   public JPanel createReportingTab() {
    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JPanel header = new JPanel(new GridLayout(1, 3, 10, 10));
    header.setBorder(BorderFactory.createTitledBorder("System Statistics"));
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

    String[] columns = {"Plate", "Entry", "Parking Type","Level", "Vehicle Type","Spot Id", "Duration", "Fine Status"};
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
        String[] histColumns = {"Plate", "Date","Parking Type","Level","Vehicle Type", "Spot ID ", "Fee Paid", "Final Status"};
        Object[][] histData = FileManager.getHistoryData();
        table.setModel(new DefaultTableModel(histData, histColumns));
        header.setBorder(BorderFactory.createTitledBorder("Past Transactions (Paid)"));
    });
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