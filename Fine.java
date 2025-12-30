
public class fine {
 private Vehicle vehicle;
 private Parking parking;

    public fine(){
     this.vehicle = new Vehicle();
     this.parking = new Parking();
     vehicle.inputPlateNumber();
     vehicle.calculateDuration();


    }

    public class fineType{
        void finePayType(){
         double time = vehicle.duration;
         if(time > 24.00){
           totalRate += 50;
         }
        }
    }
   
    class fixed extends fineType{
        void finePayType(){

        }
    }

    class progessive extends fineType{
        void finePayType(){
            
        }
    }

    class hourly extends fineType{
        void finePayType(){
            
        }
    }
}
