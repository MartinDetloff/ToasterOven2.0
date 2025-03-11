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
        this.sensor  = new heatMonitor(controller);
    }



    /**
     * Method to start the cooking.
     * @throws IOException ..
     */
    public void startCooking(String mode, int desiredTemp, int time) throws IOException, InterruptedException {
        controller.setIsCooking(true);
        sensor.setDesiredTemp(desiredTemp);

        switch(mode){
            case "Roast" -> {
                turnOnTopHeater();
                turnOnBottomHeater();

            }
            case "Broil" ->{
                turnOffBottomHeater();
                turnOnTopHeater();

            }
            case "Bake"->{
                turnOffTopHeater();
                turnOnBottomHeater();

            }
        }
        // preheat
        sensor.turnOnHeaters();

        Thread.sleep(((desiredTemp ) / 50) * 1000);


        new Thread(() -> controller.setUpTimer()).start();
    }

    /**
     * Methods to access the heaters between the controller and the heat controller
     */
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

    /**
     * Method that stops the cooking
     */
    public void stopCooking(boolean isDoor) throws IOException {
        controller.setIsCooking(false);

        if (numberOfStopTimesPressed == 1 || isDoor){
            heaterController.turnOffBottomHeater();
            heaterController.turnOffTopHeater();
            controller.stopCookingTimer();
            controller.stopCooking();
        }
        else if (numberOfStopTimesPressed == 2){
            controller.clearEverything();
        }
        else {
            heaterController.turnOffBottomHeater();
            heaterController.turnOffTopHeater();
            controller.clearEverything();
            controller.stopCooking();
            controller.stopCookingTimer();
        }
        sensor.turnOffHeaters();
    }

    /**
     * Returns cavity temperature
     * @return
     */
    public int getTempReading(){
        return sensor.getCavityTemp();

    }

    /**
     * Sets the number of times the stop button was pressed
     * @param timesPressed
     */
    public void setNumberOfStopTimesPressed(int timesPressed){
        this.numberOfStopTimesPressed = timesPressed;
    }

    /**
     * Returns the number of times the stop button was pressed
     * @return
     */
    public int getNumberOfStopTimesPressed(){
        return this.numberOfStopTimesPressed;
    }

}
