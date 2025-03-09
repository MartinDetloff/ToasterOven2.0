public class Mode{

    private String currentMode;
    private String currentPreset;
    private boolean isOnTemp;
    private boolean isOnTime;

    public Mode(String currentMode, String currentPreset){
        this.currentMode = currentMode;
        this.currentPreset = currentPreset;
    }
    /**
     * Method to set the mode
     * @param mode the mode (bake, broil, roast)
     */
    public void setMode(String mode){
        currentMode = mode;
    }

    /**
     * Method to set the preset
     * @param preset the preset (nuggets, pizza)
     */
    public void setPreset(String preset){
        currentPreset = preset;
    }

    /**
     * Method to focus on the temp
     */
    public void focusTemp(){

    }

    /**
     * Method to focus on the time
     */
    public void focusTime(){

    }

    /**
     * Method to increment the button that we are focused on (temp, time)
     */
    public void increment(){
        if(isOnTemp){

        }

    }

    /**
     * Method to decrement the button that we are focused on (temp, time)
     */
    public void decrement(){

    }



}
