
public class Parking {
    public String parkingType;
    private String id;
    public double totalRate; 


    public static double getParkingRate(String type) {
    if (type == null) return 0.0;

        return switch (type) {
            case "Compact" -> 2.50;
            case "Regular" -> 5.00;
            case "Handicapped" -> 2.00;
            case "Reserved" -> 10.00;
            default -> 0.00;
        };
}

    public Parking(String parkingType, String id) {
        this.parkingType = parkingType;
        this.id = id;
    }

    public Parking() {}


    public String getId() { return id; }

        public double getHourlyRate() {
        if (this.parkingType == null) return 0.0;

        if (parkingType.equalsIgnoreCase("Compact")) {
            return 2.50;
        } else if (parkingType.equalsIgnoreCase("Regular")) {
            return 5.00;
        } else if (parkingType.equalsIgnoreCase("Handicapped")) {
            return 2.00;
        } else if (parkingType.equalsIgnoreCase("Reserved")) {
            return 10.00;
        } else {
            return 0.00;
        }
    }

    public static int getCount(String type) {
        return FileManager.countByType(type);
    }
}