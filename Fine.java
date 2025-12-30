
public class Fine {
 public  Vehicle vehicle;
 public  Parking parking;
 public boolean isParked;
  

    public Fine(){
     this.vehicle = new Vehicle();
     this.parking = new Parking();
     vehicle.inputPlateNumber();
     vehicle.calculateDuration();


    }

    public class fineType{
        double  finePayType(double totalRate, double duration){
         if(duration > 24.00 || isParked == false){
           System.out.println("Your no plate has fine : " + vehicle.plateNumber);
        }
        return totalRate;
    }
   
    class fixed extends fineType{//fixed fine type
        @Override
        double finePayType(double totalRate , double duration){
           return  totalRate + 50.00;
        }
    }

    class progessive extends fineType{//progessive fine type
        @Override
        double finePayType(double totalRate , double duration){
            
            if(duration== 24.00){
               return totalRate + 50;
            }else if(duration> 24 && duration<48){
               return totalRate + 50 + 100;
            }else if (duration > 48 && duration <72){
               return totalRate +50 + 150;
            } else {
                return totalRate+ 50 + 200;
            }
        }
    }

    class hourly extends fineType{//hourly fine type 
        @Override
        double finePayType(double totalRate , double duration){
            return totalRate + (duration - 24) * 20;
        }
    }
    }
}
