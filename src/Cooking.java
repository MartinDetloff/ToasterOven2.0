import java.io.IOException;

public class Cooking {
    private heatMonitor sensor;
    private HeaterController heaterController;
    private Controller controller;
    private int cookTemp;
    private String cookMode;
    private int cookTime;
    private int numberOfStopTimesPressed = 0;

    public Cooking(Controller controller){
        heaterController = new HeaterController();
        this.controller = controller;
    }

    public Cooking(Controller controller, String mode, int desiredTemp, int time){
        this.sensor  = new heatMonitor(desiredTemp, controller);
        heaterController = new HeaterController();
        this.controller = controller;
        this.cookMode = mode;
        this.cookTemp = desiredTemp;
        this.cookTime = time;
    }


    /**
     * Method to start the cooking.
     * @throws IOException ..
     */
    public void startCooking(String mode, int desiredTemp, int time) throws IOException {
        this.sensor  = new heatMonitor(desiredTemp, controller);
        System.out.println(cookMode);
        switch(mode){
            case "Roast" -> {
                turnOnTopHeater();
                turnOnBottomHeater();

            }
            case "Bake" ->{
                turnOffBottomHeater();
                turnOnTopHeater();

            }
            case "Broil"->{
                turnOffTopHeater();
                turnOnBottomHeater();

            }
        }
        // preheat
        sensor.turnOnHeaters();


        // start cooking timer
        while(sensor.cavityTemp < cookTemp) {}
        new Thread(() -> controller.setUpTimer()).start();
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

        // pressed the stop button once (Stop)
        if (numberOfStopTimesPressed == 1){
            System.out.println("stopped cooking (Stop Button Pushed)");
            heaterController.turnOffBottomHeater();
            heaterController.turnOffTopHeater();

            // stop timer (Save current values)
            controller.stopCookingTimer();
            controller.stopCooking();
        }

        // pressed the stop button twice (Clear)
        else if (numberOfStopTimesPressed == 2){
            System.out.println("Cleared Presets (Clear Button Pushed)");
            // clear case
            controller.clearEverything();
        }

        // finished cooking naturally
        else {
            System.out.println("Finished cooking");
            // Turn off the top/ Bottom heaters
            heaterController.turnOffBottomHeater();
            heaterController.turnOffTopHeater();

            // send messages to javaFX from the controller
            controller.clearEverything();
            controller.stopCooking();
        }
        sensor.turnOffHeaters();
    }

    private void reset(){

    }

    public int getTempReading(){
        return sensor.getCavityTemp();

    }

    public void setNumberOfStopTimesPressed(int timesPressed){
        this.numberOfStopTimesPressed = timesPressed;
    }

    public int getNumberOfStopTimesPressed(){
        return this.numberOfStopTimesPressed;
    }

}
