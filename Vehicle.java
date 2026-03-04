
public class Vehicle {
    
    String plateNumber;
    double entryTime;
    String entryDay;
    String exitDay;
    double exitTime;
    double duration;
    


    public Vehicle() {
    }

    public Vehicle(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public double getDuration() {
        return duration;
    }

    public String getPlateNumber() {
        return this.plateNumber;
    }

    public void calculateDuration(double entry, double exit) {
        this.entryTime = entry;
        this.exitTime = exit;
        if (exitTime < entryTime) {
            System.out.println("Invalid Time Range");
            this.duration = 0;
        } else {
            this.duration = Math.ceil(exitTime - entryTime);
        }
    }

    public void setPlateNumber(String plate) {
        if (plate.toUpperCase().matches("[A-Z]{3}\\s\\d{4}")) {
            this.plateNumber = plate.toUpperCase();
        } else {
            this.plateNumber = "INVALID";
        }
    }
}