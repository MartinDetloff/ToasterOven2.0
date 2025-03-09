public class Cooking {
    private TempSensor tempSensor;
    private HeaterController heaterController;
    public Cooking(){
        tempSensor = new TempSensor();
        heaterController = new HeaterController();
    }
    private void startCooking(String Mode){
        switch(Mode){
            case "Roast" -> {
                heaterController.turnOnTopHeater();
                heaterController.turnOnBottomHeater();
            }
            case "Bake" ->{
                heaterController.turnOffBottomHeater();
                heaterController.turnOnTopHeater();
            }
            case "Broil"->{
                heaterController.turnOffTopHeater();
                heaterController.turnOnBottomHeater();
            }
        }

    }
    private void stopCooking(){
        heaterController.turnOffBottomHeater();
        heaterController.turnOffTopHeater();
    }

    private void reset(){

    }

    private void getTempReading(){
        //get temp at the time
    }

}
