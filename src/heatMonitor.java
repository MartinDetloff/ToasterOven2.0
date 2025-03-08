public class heatMonitor {
    private int temp;
    public heatMonitor(int temp){
        this.temp = temp;
    }

    /**
     * Method to get the current cavity temp
     * @return the current temp
     */
    public int getCavityTemp(){
        return temp;
    }
}
