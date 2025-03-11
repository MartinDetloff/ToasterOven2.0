import java.io.IOException;

public class InputInterface {
    public Status status;
    private Control control;
    private Mode mode;
    private Controller controller;
    private boolean isOnTemp = false;
    private boolean isOnTime = false;
    private double currentTemp = 350;
    private double currentTimeMin = 5;
    private double currentTimeSec = 0;


    public InputInterface(Controller controller){
        this.status = new Status(350, 5, 0);
        this.control = new Control();
        this.mode = new Mode();
        this.controller = controller;
    }

    /**
     * getter for the current status
     * @return status
     */
    public Status getStatus(){
        return status;
    }

    /**
     * Getter for the current control
     * @return control
     */
    public Control getControl(){
        return control;
    }

    /**
     * Getter for the current mode
     * @return mode
     */
    public Mode getMode(){
        return mode;
    }

    /**
     * Method to toggle the power
     * @throws IOException ..
     */
    public void togglePower() throws IOException {
        control.togglePower();
        controller.togglePower();
    }

    public void startButtonPress(){

    }

    /**
     * Method to focus on the temp
     */
    public void focusTemp(){
        isOnTemp = true;
        isOnTime = false;
    }

    /**
     * Method to focus on the time
     */
    public void focusTime(){
        isOnTemp = false;
        isOnTime = true;
    }

    /**
     * Method to increment the button that we are focused on (temp, time)
     */
    public void increment() throws IOException {
        if(mode.getCurrentPreset().equals("None")) {
            if (isOnTemp && currentTemp < 500 ) {
                currentTemp += 15;
            } else if(isOnTime && currentTimeMin < 200){
                currentTimeMin += 1;
            }
            this.updateDisplay();
        }
    }

    /**
     * Method to decrement the button that we are focused on (temp, time)
     */
    public void decrement() throws IOException {
        if(mode.getCurrentPreset().equals("None")) {

            if (isOnTemp && currentTemp > 100) {
                currentTemp -= 15;

            } else if(isOnTime && currentTimeMin > 1){
                currentTimeMin -= 1;
            }
            this.updateDisplay();
        }

    }


    /**
     * Method to update the display
     */
    public void updateDisplay() throws IOException {

        status.displayNewTemp(currentTemp);
        status.displayNewTimeMin(currentTimeMin);
        status.displayNewTimeSec(currentTimeSec);

        System.out.println("updating the display hr");
        controller.sendMessageToUpdateDisplay((int) currentTimeMin,(int) currentTimeSec , (int) currentTemp);
//        controller.setDisplay();
    }

    /**
     * Method to set the preset
     * @param preset the preset
     * @throws IOException ..
     */
    public void setPreset(String preset) throws IOException {
        mode.setPreset(preset);
        if (preset.equals("None")){
            controller.sendMessageToUpdatePreset(0);
        }
        else if (preset.equals("pizza")){
            controller.sendMessageToUpdatePreset(1);
            currentTemp = 400;
            currentTimeMin = 15;
            currentTimeSec = 0;
            controller.sendMessageToUpdateDisplay((int) currentTimeMin, (int) currentTimeSec,(int) currentTemp);
        }
        else if (preset.equals("nuggets")){
            controller.sendMessageToUpdatePreset(2);
            currentTemp = 375;
            currentTimeMin = 10;
            currentTimeSec = 0;
            controller.sendMessageToUpdateDisplay((int) currentTimeMin, (int) currentTimeSec, (int) currentTemp);
        }
    }

    public int getCurrentTemp(){
        return (int)this.currentTemp;
    }

    public int getCurrentTimeMin(){
        return (int) this.currentTimeMin;
    }

    /**
     *
     * @param newTimeMin
     */
    public void setCurrentTimeMin(int newTimeMin){
        this.currentTimeMin = newTimeMin;
        status.displayNewTimeMin(newTimeMin);
    }

    /**
     *
     * @param newTimeSec
     */
    public void setCurrentTimeSec(int newTimeSec){
        System.out.println("Setting the seconds to " + newTimeSec);
        this.currentTimeSec = newTimeSec;
        status.displayNewTimeSec(newTimeSec);
    }

    public void setCurrentTemp(int newTemp){
        this.currentTemp = newTemp;
        status.displayNewTemp(newTemp);
    }
    /**
     * Method to change the mode
     * @param modeToChange the mode to change it to
     */
    public void setMode(String modeToChange){
        mode.setMode(modeToChange);
    }

}
