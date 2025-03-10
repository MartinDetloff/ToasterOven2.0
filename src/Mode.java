public class Mode{

    private String currentMode;
    private String currentPreset;

    public Mode(){
        this.currentMode = "Roast";
        this.currentPreset = "None";
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
     * Method to return the current mode
     * @return
     */
    public String getCurrentMode(){
        return currentMode;
    }

    public String getCurrentPreset(){
        return currentPreset;
    }
}
