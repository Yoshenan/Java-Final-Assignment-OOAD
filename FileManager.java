import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class FileManager {
    private static final String FILE_PATH = "parking.txt";
    public static ArrayList<Vehicle> vehicleList = new ArrayList<>();

    public static void saveDetails(String numPlate, double time, String date, String parkingType, String vehicleType, String level, String spotID) {

        String data = numPlate + "," + time + "," + date + "," + parkingType + "," + vehicleType + "," + level + "," + spotID;
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
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] p = line.split(",");
            if (p.length >= 7) { 
                if (p[0].equalsIgnoreCase(searchPlate.trim()) && !line.contains("PAID")) {
                    return line; 
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error loading: " + e.getMessage());
    }
    return null; 
}

    public static DefaultTableModel getUnifiedView() {
    String[] columns = {"Plate","Date", "Level", "Spot", "Type", "Status"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty() || line.toUpperCase().contains("PAID")) continue;

            String[] d = line.split(",");
            if (d.length >= 7) { 
                model.addRow(new Object[]{ d[0], d[2],d[5], d[6], d[4], "PARKED" });
            }
        }
    } catch (IOException e) { e.printStackTrace(); }
    return model;
}

    public static DefaultTableModel getFilteredParkDetails(String filterType) {
    String[] columns = {"Plate Number", "Entry Time", "Vehicle Type", "Spot ID", "Level", "Status"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    try (BufferedReader read = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;
        while ((line = read.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] d = line.split(",");
            if (d.length >= 7) { 
                if (d[3].equalsIgnoreCase(filterType.trim())) { 
                    String status = (d.length >= 9) ? d[8].trim() : "Parked";
                    model.addRow(new Object[]{ 
                        d[0],    
                        d[1],   
                        d[6],    
                        d[4],    
                        d[5],    
                        status   
                    });
                }
            }
        }
    } catch (Exception e) { 
        System.err.println("Filter Error: " + e.getMessage()); 
    }
    return model;
}

public static void updateFees(String plate, double amount, String status) {
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts[0].equalsIgnoreCase(plate.trim()) && !line.contains("PAID")) {
                String updatedLine = line + "," + String.format("%.2f", amount) + "," + status;
                lines.add(updatedLine);
                
            } else {
                lines.add(line);
            }
        }
    } catch (IOException e) { return; }

    try {
        java.nio.file.Files.write(java.nio.file.Paths.get(FILE_PATH), lines);
    } catch (IOException e) { e.printStackTrace(); }
}

    public static double[] getTotalRevenue() {
        double fineTotal = 0, standardTotal = 0;
        try (BufferedReader read = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = read.readLine()) != null) {
                String[] d = line.split(",");
                if (d.length >= 9) {
                    double fee = Double.parseDouble(d[7]);
                    if (d[8].contains("FINED")) fineTotal += fee;
                    else if (d[8].contains("PAID")) standardTotal += fee;
                }
            }
        } catch (Exception e) { }
        return new double[]{standardTotal, fineTotal};
    }

    
    public static Object[][] getReportData() {
        List<Object[]> reportList = new ArrayList<>();
        double demoCurrentTime = 24.0; 
        try (BufferedReader read = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = read.readLine()) != null) {
                if (line.trim().isEmpty() || line.contains("PAID")) continue;
                String[] d = line.split(",");
                if (d.length >= 7) {
                    try {
                        double timeIn = Double.parseDouble(d[1]);
                        double duration = demoCurrentTime - timeIn;
                        String fineStatus = (duration > 24) ? "OVERSTAY" : "NO FINES";
                        reportList.add(new Object[]{ d[0], d[1], d[3],d[5], d[4], d[6], String.format("%.1f hrs", duration), fineStatus });
                    } catch (NumberFormatException nfe) { continue; }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return reportList.toArray(new Object[0][0]);
    }

    public static Object[][] getHistoryData() {
        List<Object[]> historyList = new ArrayList<>();
        try (BufferedReader read = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = read.readLine()) != null) {
                String[] d = line.split(",");
                if (d.length >= 9 && d[8].toUpperCase().contains("PAID")) {
                    historyList.add(new Object[]{ d[0], d[2],d[3], d[5], d[4], d[6], "RM " + d[7], d[8] });
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return historyList.toArray(new Object[0][0]);
    }

  public static int countByType(String searchKey) {
    int count = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            
            String[] d = line.split(",");
            if (line.toUpperCase().contains("PAID")) continue;

            if (d.length >= 4 && d[3].equalsIgnoreCase(searchKey.trim())) {
                count++;
            }
        }
    } catch (IOException e) { return 0; }
    return count;
}

    public static boolean isSpotOccupied(String spotID, String level) {
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.toUpperCase().contains("PAID")) continue;

            String[] d = line.split(",");
            if (d.length >= 7) {
                if (d[6].equalsIgnoreCase(spotID.trim()) && d[5].equalsIgnoreCase(level.trim())) {
                    return true; 
                }
            }
        }
    } catch (IOException e) { return false; }
    return false;
}

  public static int getActiveCarsByType(String type) {
    int count = 0;
    try (BufferedReader read = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;
        while ((line = read.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String upperLine = line.toUpperCase();
            if (upperLine.contains(type.toUpperCase()) && !upperLine.contains("PAID")) {
                count++;
            }
        }
    } catch (Exception e) {
        return 0;
    }
    return count;
}
}