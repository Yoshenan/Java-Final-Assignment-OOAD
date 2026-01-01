


import java.util.Scanner;

public class Vehicle{
    
    String plateNumber;
    double entryTime;
    double exitTime;
    double duration;
    boolean isHavingCard = true; //handicapped vehicle
    String vehicleType;
    double height;
    int wheels;
    Scanner sc = new Scanner(System.in);
    
    
    
       public Vehicle(){
          
       }

       public Vehicle(String plateNumber, boolean isHavingCard , double height , int wheels){
            this.plateNumber = plateNumber;
            this.isHavingCard = isHavingCard;
            this.height = height;
            this.wheels = wheels;
            

       }

       public double getDuration() {
          return duration;
        }

       public String getPlateNumber() {
         return plateNumber;
        }

      public String getVehicleType(){
        return vehicleType;
      }

       public void vehicleType(){
             
           String[] type = {"Car" , "Motorbike" ,"SUV", "Handicapped Vehicle"};
            if(isHavingCard){vehicleType = type[3];}
            else if(height>1.8){ vehicleType = type[2];}
            else if(wheels == 2 ){ vehicleType = type[1];}
            else{vehicleType = type[0];}    

}


       public  void calculateDuration() {
         
          System.out.println("Enter entry time: ");
          entryTime = sc.nextInt();
          System.out.println("Enter exit time: ");
          exitTime = sc.nextInt();
          if(exitTime < entryTime){System.out.println("Invalid ");}
          
          duration = Math.ceil(exitTime - entryTime);
       }
       public void inputPlateNumber(){
           
           System.out.println("Enter NoPlate: ");
           plateNumber = sc.nextLine().toUpperCase();

           if (plateNumber.matches("[A-Z]{3}\\s\\d{4}")){
                System.out.println("Correct");
           }
           else{
             System.out.println("invalid");
           }
       }
    
        
       }



