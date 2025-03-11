public class Status {
    private double timeMin;
    private double timeSec;
    private double temp;


    public Status(double temp, double timeMin, double timeSec){
        this.temp = temp;
        this.timeMin = timeMin;
        this.timeSec = timeSec;
    }

    /**
     * Method to display a new temp
     * @param temp the new temp to display
     */
    public void displayNewTemp(double temp){
        this.temp = temp;
    }

    /**
     * Method to display a new time
     * @param timeMin the new time to display
     */
    public void displayNewTimeMin(double timeMin){
        this.timeMin = timeMin;
    }

    /**
     * Method to display a new time
     * @param timeSec the new time to display
     */
    public void displayNewTimeSec(double timeSec){
        this.timeSec = timeSec;
    }

    /**
     * Getter for the temp
     * @return the temp
     */
    public double getTemp(){
        return this.temp;
    }

    /**
     * Getter for the time
     * @return the time
     */
    public double getTimeMin(){
        return this.timeMin;
    }
    /**
     * Getter for the time
     * @return the time
     */
    public double getTimeSec(){
        return this.timeSec;
    }
}
