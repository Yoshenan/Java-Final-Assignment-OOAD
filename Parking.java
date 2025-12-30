public class Parking extends Vehicle{
    String parkingType;
    double[] parkingFare;
    double totalRate;
    boolean isHavingCard;
    int status = 0;
    int floor;
    int rows;
    String id;
    public static Parking[][][] parkingStructure;
    
   
      public Parking(String parkingType, double[] parkingFare , int status , int floor ,  int rows , String id){
        this.parkingType = parkingType;
        this.parkingFare = parkingFare;
        this.status = status;
        this.floor = floor;
        this.rows = rows;
        this.id = "F" + floor + "R" + rows + "S" + parkingType;
      }
      public static boolean parkingSpot(String parkingType , double[] parkingFare){
         String[] types = {"Compact" , "Regular" , "Handicapped" , "Reserved"};
         final double[] ratesPerHour  = {2.0 , 5.0 , 2.0 , 10.0};
         
         int n = types.length;
         for(int i = 0 ; i<n ; i++){
             if(parkingType.equals(types[i])){
             parkingFare[0] = ratesPerHour[i];
             return true;
         }

         }
         return false;
      }
      public void parkingCalculation(){
         if(parkingType.equals("Handicapped") && isHavingCard==true){
           this.totalRate = 0.0;
         }else{this.totalRate = this.parkingFare[0] * this.duration;} 
      }

      public void statusCheck(){
        if(status == 0 ) {
             System.out.println("you can  park , space available");
        }else {
          status++;
          System.out.println("Space not available");
        }
      }
      public void parkingLot(String[] types){
        parkingStructure = new Parking[floor + 1][rows+1][types.length+ 1];
        for(int f = 1 ; f<=floor ; f++) {
          for(int r= 1 ; r<= rows; r++){
            for(int s= 0 ; s< types.length ; s++){
              String type = types[s];

              double[] fare = new double[1];
                parkingSpot(type, fare);

                // s+1 because we're using 1-based indexing in the park array
                parkingStructure[f][r][s+1] = new Parking(type , fare , 0 , f, r,id);
            }
            }
          }
          System.out.println("Parking lot initialized: " + floor + " floors, "+ rows + " rows, " + types.length + " spots per row");
      }

    }



