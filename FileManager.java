
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class FileManager {
    private static final String FILE_PATH = "parking.txt";
    public static ArrayList<Vehicle> vehicleList = new ArrayList<>();

    public static void saveDetails(String numPlate, double time, String vehicleType , String ParkingType, String level, String SpotID) {
        String data = numPlate + "," + time + "," + ParkingType + "," + vehicleType + ","+level + "," + SpotID ;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving: " + e.getMessage());
        }
    }

    public static String loadDetails(String searchPlate) {
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] p = line.split(",");
            if (p[0].equalsIgnoreCase(searchPlate.trim())) {
                return line; 
            }
        }
    } catch (IOException e) {
        System.err.println("Error loading: " + e.getMessage());
    }
    return null; 
}
public static int countByType(String searchKey) {
    int count = 0;
    try {
        java.io.File f = new java.io.File(FILE_PATH);
        if(!f.exists()) return 0;
        
        try (java.util.Scanner s = new java.util.Scanner(f)) {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.toLowerCase().contains(searchKey.toLowerCase().trim())) {
                    count++;
                }
            }
        }
    } catch (FileNotFoundException e) {
        return 0;
    }
    return count;
}

public static DefaultTableModel getUnifiedView() {
    String[] columns = {"Plate", "Level", "Spot", "Type", "Fee (RM)", "Status"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
        java.util.Scanner sc = new java.util.Scanner(reader);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] d = line.split(",");

            
            if (d.length >= 8) { 
                String fee = d[6];
                String status = d[7];
                model.addRow(new Object[]{ d[0], d[4], d[5], d[2], fee, status });
            } else if (d.length >= 6) { 
                model.addRow(new Object[]{ d[0], d[4], d[5], d[2], "0.00", "PARKED" });
            }
        }
    } catch (java.io.IOException e) {
    }
    return model;
}

   public static void updateFees(String plate, double amount, String status) {
    java.util.List<String> lines = new java.util.ArrayList<>();
    java.io.File file = new java.io.File(FILE_PATH);
    if (!file.exists()) return;

    try (java.util.Scanner sc = new java.util.Scanner(file)) {
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(",");
            if (parts.length > 0 && parts[0].equalsIgnoreCase(plate.trim()) && !line.contains("PAID")) {
                lines.add(line + "," + amount + "," + status);
            } else {
                lines.add(line);
            }
        }
    } catch (java.io.FileNotFoundException e) {
        System.out.println("File not found: " + e.getMessage());
        return;
    }
    try {
        java.nio.file.Files.write(java.nio.file.Paths.get(FILE_PATH), lines);
    } catch (java.io.IOException e) {
        System.out.println("Error writing to file: " + e.getMessage());
    }
}
   public static Object[][] getReportData() {
    List<Object[]> reportList = new ArrayList<>();
    double currentTime = 20.0; 

    try (BufferedReader read = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;
        while ((line = read.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] d = line.split(",");
            if (d.length < 6) {
                continue; 
            }
            if (!line.contains("PAID")) {
                try {
                    String plate = d[0];
                    double timeIn = Double.parseDouble(d[1]);
                    String lvl = d[2];
                    String type = d[3];
                    String slot = d[4];
                    double duration = currentTime - timeIn;
                    String fineStatus = (duration > 24) ? "OVERSTAY" : "NO FINES";
                    reportList.add(new Object[]{
                        plate, timeIn,lvl, type, slot, 
                        String.format("%.1f hrs", duration), 
                        fineStatus
                    });
                } catch (NumberFormatException nfe) {}}}
    } catch (Exception e) {System.err.println("Error generating report: " + e.getMessage());
    }return reportList.toArray(Object[][]::new);}

public static double[] getTotalRevenue() {
    double FineTotal = 0;
    double StandardTotal = 0;
    try (BufferedReader read = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;
        while ((line = read.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] d = line.split(",");
            if (d.length >= 8 && d[7].contains("FINED")) {
                FineTotal += Double.parseDouble(d[6]);
            }
            else if (d.length >= 8 && d[7].contains("PAID")) {
                try {
                    StandardTotal += Double.parseDouble(d[6]);
                } catch (NumberFormatException e) {
                }}}
    } catch (Exception e) {}
return new double[]{StandardTotal, FineTotal};
}

public static Object[][] getHistoryData() {
    List<Object[]> historyList = new ArrayList<>();
    try (BufferedReader read = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;
        while ((line = read.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            
            String[] d = line.split(",");
            if (d.length >= 8 && d[7].toUpperCase().contains("PAID")) {
                historyList.add(new Object[]{
                    d[0], d[2],d[4], d[3], d[5],"RM " + d[6],d[7]});}}
    } catch (Exception e) {e.printStackTrace();}
    return historyList.toArray(new Object[0][]);}


public static DefaultTableModel getFilteredParkDetails(String filterType) {
    String[] columns = {"Plate Number", "Entry Time", "Vehicle Type", "Spot ID", "Level"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);

    try (BufferedReader read = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;
        while ((line = read.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] d = line.split(",");
            if (d.length >= 6) { 
                if (d[3].equalsIgnoreCase(filterType)) {
                    model.addRow(new Object[]{
                        d[0], // Plate
                        d[1], // Time
                        d[2], // Vehicle Type (Car/Motor)
                        d[4], // Spot ID
                        (d.length > 6) ? d[2] : "N/A" // Level (or use d[2] if that's where Level is)
                    });
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace(); 
    }
    return model;
}
}