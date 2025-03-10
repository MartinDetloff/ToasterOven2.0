import java.io.IOException;

public class Cooking {
    private TempSensor tempSensor;
    private HeaterController heaterController;
    private Controller controller;
    private int cookTemp;
    private String cookMode;
    private int cookTime;
    private int numberOfStopTimesPressed = 0;

    public Cooking(Controller controller){
        tempSensor = new TempSensor();
        heaterController = new HeaterController();
        this.controller = controller;
    }

    public Cooking(Controller controller, String mode, int temp, int time){
        tempSensor = new TempSensor();
        heaterController = new HeaterController();
        this.controller = controller;
        this.cookMode = mode;
        this.cookTemp = temp;
        this.cookTime = time;
    }


    public void setCookMode(String mode ){
        this.cookMode = mode;
    }
    public void setCookTemp(int temp){
        this.cookTemp = temp;
    }
    public void setCookTime(int time){
        this.cookTime = time;
    }
    private void startCooking(String Mode) throws IOException {
        switch(Mode){
            case "Roast" -> {
                turnOnTopHeater();
                turnOnBottomHeater();

            }
            case "Bake" ->{
                turnOffBottomHeater();
                turnOffTopHeater();

            }
            case "Broil"->{
                turnOffTopHeater();
                turnOnBottomHeater();

            }
        }

    }

    public void turnOnTopHeater() throws IOException {
        heaterController.turnOnTopHeater();
        controller.turnOnTopHeater();
    }

    public void turnOffTopHeater() throws IOException {
        heaterController.turnOffTopHeater();
        controller.turnTopHeaterOff();
    }

    public void turnOnBottomHeater() throws IOException {
        heaterController.turnOnBottomHeater();
        controller.turnOnBottomHeater();
    }

    public void turnOffBottomHeater() throws IOException {
        heaterController.turnOffBottomHeater();
        controller.turnBottomHeaterOff();
    }

    public void stopCooking() throws IOException {
        if (numberOfStopTimesPressed == 1){
            heaterController.turnOffBottomHeater();
            heaterController.turnOffTopHeater();
            System.out.println("stopped cooking");

            controller.stopCooking();
        }
        else {
            // clear case
            System.out.println("cleared presets");
            controller.clearEverything();
        }

    }

    private void reset(){

    }

    private void getTempReading(){
        //get temp at the time
    }

    public void setNumberOfStopTimesPressed(int timesPressed){
        this.numberOfStopTimesPressed = timesPressed;
    }

    public int getNumberOfStopTimesPressed(){
        return this.numberOfStopTimesPressed;
    }

}
