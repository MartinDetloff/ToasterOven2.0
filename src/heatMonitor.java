import java.io.IOException;

public class heatMonitor extends Thread {
    public int desiredTemp = 350;
    public int cavityTemp;
    public final int restingTemp = 100;
    public boolean heatersOn = false;
    public Controller controller;


    public heatMonitor(Controller controller){
        cavityTemp = 100;
        this.controller = controller;

        new Thread(() -> {
            try {
                handleTemp();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


    /**
     * Setter for the desired temp
     * @param desiredTemp the desired temp
     */
    public void setDesiredTemp(int desiredTemp){
        this.desiredTemp = desiredTemp;
    }
    /**
     * Method to get the current cavity temp
     * @return the current temp
     */
    public int getCavityTemp(){
        return cavityTemp;
    }

    /**
     * Methods to activates the heaters, top and bottom individually or together
     */
    public void turnOnHeaters(){
        this.heatersOn = true;
    }

    public void turnOffHeaters(){
        this.heatersOn = false;
    }


    /**
     * As long as the heaters are on, this will increase the temperature until it reaches the desired temperature.
     */
    public void handleTemp() throws InterruptedException, IOException {
        while (true) {
            if (heatersOn) {
                if (cavityTemp < desiredTemp - 50) {
                    cavityTemp += 50;
                } else {
                    cavityTemp = desiredTemp;
                }
            } else {
                if (cavityTemp > restingTemp) {
                    cavityTemp -= 50;
                } else {
                    cavityTemp = restingTemp;
                }
            }
            Thread.sleep(1000);
            controller.updateCavityTemp(cavityTemp);
        }
    }
}
