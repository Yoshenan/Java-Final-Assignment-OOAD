public class Fine {
    public Vehicle vehicle;
    public Parking parking;
    public boolean isParked;

    public Fine() {
        this.vehicle = new Vehicle();
        this.parking = new Parking();
        this.isParked = true;
    }

    public static class FineType {
        public double finePayType(double totalRate, Vehicle vehicle, boolean isParked) {
            if (vehicle.getDuration() > 24.0) {
                System.out.println("Fine Applied for Plate: " + vehicle.getPlateNumber());
            }
            return totalRate;
        }
    }

    public static class Fixed extends FineType {
        @Override
        public double finePayType(double totalRate, Vehicle vehicle, boolean isParked) {
            if (vehicle.getDuration() > 24.0) {
                return totalRate + 50.00;
            }
            return totalRate;
        }
    }

    public static class Progressive extends FineType {
        @Override
        public double finePayType(double totalRate, Vehicle vehicle, boolean isParked) {
            double duration = vehicle.getDuration();
            if (duration > 72.0) return totalRate + 250.00; 
            if (duration > 48.0) return totalRate + 200.00; 
            if (duration > 24.0) return totalRate + 150.00; 
            if (duration == 24.0) return totalRate + 50.00;
            return totalRate;
        }
    }

    public static class Hourly extends FineType {
        @Override
        public double finePayType(double totalRate, Vehicle vehicle, boolean isParked) {
            double duration = vehicle.getDuration();
            if (duration > 24.0) {
                return totalRate + (duration - 24) * 20.00;
            }
            return totalRate;
        }
    }
}