

public class Payment {
    Parking parking;
    Vehicle vehicle;
    public String paymentMethod = "N/A";

    public Payment() {
        this.parking = new Parking();
        this.vehicle = new Vehicle();
    }

    public String getPaymentMethod() { return paymentMethod; }

    public interface paymentType { double makePayment(double totalRate); }

    class Cash implements paymentType {
        private final double amount;
        public Cash(double amount) { this.amount = amount; }
        @Override
        public double makePayment(double totalRate) {
            if (amount >= totalRate) {
                paymentMethod = "Cash";
                return amount - totalRate;
            }
            return -1;
        }
    }

    class Card implements paymentType {
        @Override
        public double makePayment(double totalRate) {
            paymentMethod = "Card";
            return 0;
        }
    }


    public void paymentReceipt(){
        System.out.println("\n========== PAYMENT RECEIPT ==========");
        System.out.println("Vehicle Plate  : " + vehicle.plateNumber);
        System.out.println("Entry Time     : " + vehicle.entryTime + " hours");
        System.out.println("Exit Time      : " + vehicle.exitTime + " hours");
        System.out.println("Duration       : " + vehicle.duration + " hours");
        System.out.println("-------------------------------------");
        double hourlyRate = parking.getParkingRate(parking.parkingType);
        double parkingFee = vehicle.duration * hourlyRate;
        System.out.printf("Parking Fee    : %.2f hrs x RM%.2f = RM%.2f\n", vehicle.duration, hourlyRate, parkingFee);
        System.out.println("-------------------------------------");
        System.out.printf("Total Paid     : RM%.2f\n", parking.totalRate);
        System.out.println("Payment Method : " + paymentMethod);
        System.out.println("=====================================\n");
    }
}