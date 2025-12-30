

public class payment {
    Parking parking;
    Vehicle vehicle;
    private String paymentMethod;
    //private final String id;

    public payment(){
        this.parking = new Parking();
        this.vehicle = new Vehicle();
        vehicle.inputPlateNumber();
        vehicle.calculateDuration();
        double rate = parking.parkingFare[0];
        vehicle.vehicleType();
        
    }

    public interface paymentType{
        double makePayment(double totalRate);
        
        
    }

    class Cash implements paymentType{
        private double amount;
        private double  balance;
       public Cash(double amount){this.amount = amount;}
       
       @Override
        public double  makePayment(double totalRate){
              System.out.println("Payment of amount : " + amount);
              if(amount > totalRate){
                 balance = amount - totalRate;
                 paymentMethod = "cash";
              }else {
                System.out.println("Payment of amount : " + amount);
              }
              return  balance;
        }
        
    }

    class Card implements paymentType{
        String cardNumber ,cardName;
        boolean cardExpiry = false;
        public Card( String cardNumber , boolean  cardExpiry , String cardName){
            this.cardNumber = cardNumber;
            this.cardExpiry = cardExpiry;
            this.cardName= cardName;

        }
        @Override
         public double  makePayment(double totalRate){
            if(!cardExpiry && cardNumber.length() != 16 || cardName.isEmpty()){
               System.err.println("Invalid , try again in cash");
               Cash cash = new Cash(totalRate);
               cash.makePayment(totalRate);
               paymentMethod = "Cash"; //switch to cash
            }else{
              paymentMethod = "card";
              System.out.println("Payment succesfull with: " + cardName);
            }
              return 0;
        }
       
        
        
    }

    public void  paymentReceipt(){
          System.out.println("-----Your receipt-----");
          System.out.println("Vehicle Plate : " + vehicle.plateNumber);
          System.out.println("Entry time : " + vehicle.entryTime);
          System.out.println("Exit time : " + vehicle.exitTime);
          System.out.println("Exit time : " + vehicle.exitTime);
          System.out.println("Duration : " + vehicle.duration + "hours");
          System.out.println("----------------------------------");
          double hourlyRate = parking.parkingFare[0];
          System.out.printf("Fee Breakdown: %.2f hrs x RM%.2f\n", vehicle.duration, hourlyRate);
          System.out.println("----------------------------------");
          System.out.println("Total Due:     RM" + parking.totalRate);
          System.out.println("Payment Method : " + paymentMethod);
          
          
    }



}
