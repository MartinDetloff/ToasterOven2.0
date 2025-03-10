public class Status {
    private double time;
    private double temp;


    public Status(double temp, double time){
        this.temp = temp;
        this.time = time;
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
     * @param time the new time to display
     */
    public void displayNewTime(double time){
        this.time = time;
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
    public double getTime(){
        return this.time;
    }
}
