public class Control {
    private boolean powerButton;
    private boolean isStartButton;
    private boolean isStopButton;
    private boolean isClearButton;

    /**
     * Method to set button pressed to start/stop/clear
     * @param button (start/stop/clear)
     */
    public void setButtonPressed(String button){
        switch (button){
            case "start":
                isStartButton = true;
                break;

            case "stop":
                isStopButton = true;
                break;

            case "clear":
                isClearButton = true;
                break;
        }
    }

    /**
     * Method to handle a start button click
     */
    public void start(){
        setButtonPressed("stop");
    }

    /**
     * Method to handle a stop button click
     */
    public void stop(){
        setButtonPressed("stop");
    }

    /**
     * Method to handle a clear button click
     */
    public void clear(){
        setButtonPressed("clear");
    }

    /**
     * Getter for the startButton Status
     * @return (true (on)/ false (off))
     */
    public boolean getStartButton(){
        return this.isStartButton;
    }

    /**
     * Getter for the stopButton Status
     * @return (true (on)/ false (off))
     */
    public boolean getStopButton(){
        return this.isStopButton;
    }

    /**
     * Getter for the clearButton Status
     * @return (true (on)/ false (off))
     */
    public boolean getClearButton(){
        return this.isClearButton;
    }

    /**
     * Getter for the powerButton Status
     * @return (true (on)/ false (off))
     */
    public boolean getPowerButton(){
        return this.powerButton;
    }

    /**
     * Method to toggle the power
     * @return true if power is on/ false if it is off
     */
    public boolean togglePower(){
        this.powerButton = !this.powerButton;

        return this.powerButton;
    }

}

