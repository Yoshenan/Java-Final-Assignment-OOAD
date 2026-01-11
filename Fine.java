
public class Fine {
 public  Vehicle vehicle;
 public  Parking parking;
 public boolean isParked;
  

    public Fine(){
     this.vehicle = new Vehicle();
     this.parking = new Parking();
     this.isParked = true;
    }

    public Fine(Vehicle vehicle , Parking parking , boolean isParked){
      this.vehicle = vehicle;
      this.parking = parking;
      this.isParked = isParked;
    }

    public static  class FineType{
        public double  finePayType(double totalRate, Vehicle vehicle, boolean  isParked){
         if(vehicle.getDuration() > 24.0|| isParked == false){
           System.out.println("Your no plate has fine : " + vehicle.getPlateNumber());
        }
        return totalRate;
    }
   
    public static class fixed extends FineType{//fixed fine type
        @Override
        public double finePayType(double totalRate , Vehicle vehicle , boolean isParked){
           super.finePayType(totalRate, vehicle, isParked);
           return  totalRate + 50.00;
        }
    }

    public static class progessive extends FineType{//progessive fine type
        @Override
        public double finePayType(double totalRate , Vehicle vehicle ,boolean  isParked){
            super.finePayType(totalRate, vehicle, isParked);
            double duration = vehicle.getDuration();
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

    public static class hourly extends FineType{//hourly fine type 
        @Override
        public double finePayType(double totalRate ,Vehicle vehicle , boolean isParked){
            super.finePayType(totalRate, vehicle, isParked);
            double duration = vehicle.getDuration();
            return totalRate + (duration - 24) * 20;
        }
    }
    }
}
