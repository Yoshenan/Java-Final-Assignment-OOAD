import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;




public class GUI extends JFrame {
    public Parking parking;
    public Payment payment;
    public Fine fine;
    public Vehicle vehicle;
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    public GUI() {
        setTitle("Parking Management System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel.add(createLoginPage(), "LOGIN");
        mainPanel.add(createAdminDashboard(), "ADMIN");
        mainPanel.add(createEntryExitDashboard(), "ENTRY/EXIT");
        mainPanel.add(createReportDashboard(), "REPORT"); 

        add(mainPanel);
        setVisible(true);
    }

    public void showPage(String pageName) {
        cardLayout.show(mainPanel, pageName);
    }

    private JPanel createLoginPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        String[] roles = {"ADMIN", "Entry/Exit", "REPORT"};
        JComboBox<String> roleSelector = new JComboBox<>(roles);
        JButton loginBtn = new JButton("Login");

        panel.add(new JLabel("Role: "));
        panel.add(roleSelector);
        panel.add(loginBtn);

        loginBtn.addActionListener(e -> {
            String selected = (String) roleSelector.getSelectedItem();
            showPage(selected.toUpperCase());
        });
        return panel;
    }

    
    private JPanel createAdminDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Upload Materials", new JLabel(parking.getId()) );
        
        JButton back = new JButton("Logout");
        back.addActionListener(e -> showPage("LOGIN"));
        
        panel.add(tabs, BorderLayout.CENTER);
        panel.add(back, BorderLayout.SOUTH);
        return panel;
    }

    
    private JPanel createEntryExitDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createEvaluationPanel(), BorderLayout.CENTER);
        
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> showPage("LOGIN"));
        panel.add(logout, BorderLayout.SOUTH);
        return panel;
    }

      
    private JPanel createReportDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createEvaluationPanel(), BorderLayout.CENTER);
        
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> showPage("LOGIN"));
        panel.add(logout, BorderLayout.SOUTH);
        return panel;
    }

    

    private JPanel createEvaluationPanel() {
        
        JButton loadBtn = new JButton("load");
        loadBtn.addActionListener(e ->{
        try (BufferedReader read = new BufferedReader(new FileReader("seminars.txt"))) {
        String line;
        
        while ((line = read.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length >= 1) {
                String title = parts[0];
                System.out.println("Loaded Seminar: " + title);
            }
        }
        System.out.println("All data loaded successfully!");
        
    } catch (IOException ex) {
        System.err.println("Error reading the file: " + ex.getMessage());
    }
});

 
        JPanel p = new JPanel(new GridLayout(6, 2, 10, 10));
        String[] clarityOpts = {"clear identifications of gap", "Problem is clear but weak justification", "broad problem statement", "Poor"};
        JComboBox<String> clarityBox = new JComboBox<>(clarityOpts);
        String[] methOpts = {"unbiased  ", "minimal bias , sample is large enough", "noticable bias , sample is smaller", "biased"};
        JComboBox<String> methBox = new JComboBox<>(methOpts);
        String[] resultsOpts = {"Professional Visuals & Excellent Interpretation","Accurate but some Visual flaw & Good Interpretation","Unprofessional Visuals & Needs to polish the interpretation"};
        JComboBox<String> resultsBox = new JComboBox<>(resultsOpts);
        String[] PresentationOpts= {"Confident & professional", "Clear & clean ", "Not Clear Enough & crowded " ,"unclear & too crowded " };
        JComboBox<String> PresentationBox = new JComboBox<>(PresentationOpts);

        JButton calcBtn = new JButton("Calculate & Show Mark");
    
        p.add(new JLabel("Problem Clarity:")); p.add(clarityBox);
        p.add(new JLabel("Methodology:")); p.add(methBox);
        p.add(new JLabel("Presentation:")); p.add(PresentationBox);
        p.add(new JLabel("Results:")); p.add(resultsBox);
        p.add(new JLabel("")); p.add(calcBtn);
        p.add(loadBtn);

        calcBtn.addActionListener(e -> {
            if (parking == null) {
                JOptionPane.showMessageDialog(this, "No student data found! Register first.");
                return;
            }
        });
        return p;
    }

    public static void main(String[] args) {
        new GUI();
    }
}



