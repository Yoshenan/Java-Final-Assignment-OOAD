

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


    public String paymentReceipt(){
        double rate = parking.getHourlyRate();
        double parkingFee = vehicle.duration * rate;
        double fine = parking.totalRate - parkingFee;

        return "<html><pre>" +
           "========== PAYMENT RECEIPT ==========\n" +
           "Vehicle Plate  : " + vehicle.plateNumber + "\n" +
           "Entry Time     : " + vehicle.entryTime + " hours\n" + 
           "Entry Date : " + vehicle.entryDay + "\n"+
           "Exit Time      : " + vehicle.exitTime + " hours\n" + 
           "Exit Date: " + vehicle.exitDay + "\n"+
           "Duration       : " + vehicle.duration + " hours\n" +
           "-------------------------------------\n" +
           String.format("Parking Fee    : %.2f hrs x RM%.2f = RM%.2f\n", vehicle.duration,rate,parkingFee) +
           String.format("Fine applied    :  RM%.2f\n", fine) +
           "-------------------------------------\n" +
           String.format("Total Paid     : RM%.2f\n",parking.totalRate) +
           "Payment Method : " + paymentMethod + "\n" +
           "=====================================" +
           "</pre></html>";
    }
}