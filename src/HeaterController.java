public class HeaterController {
    boolean topHeater;
    boolean bottomHeater;

    HeaterController() {
        this.topHeater = false;
        this.bottomHeater = false;
    }
    /**
     * Method to turn on the top heater
     */
    public void turnOnTopHeater(){
        topHeater = true;
    }

    /**
     * Method to turn on the bottom heater
     */
    public void turnOnBottomHeater(){
        bottomHeater = true;
    }

    /**
     * Method to turn off the bottom heater
     */
    public void turnOffBottomHeater(){
        bottomHeater = false;
    }

    /**
     * Method to turn off the top heater
     */
    public void turnOffTopHeater(){
        topHeater = false;
    }
}
