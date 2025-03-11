public class heatMonitor extends Thread {
    public int desiredTemp;
    public int cavityTemp;
    public final int restingTemp = 100;
    public boolean heatersOn = false;
    public Controller controller;

    public heatMonitor(int temp, Controller controller){
        cavityTemp = 100;
        this.controller = controller;
        this.desiredTemp = temp;
        new Thread(() -> {
            try {
                handleTemp();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }



    /**
     * Method to get the current cavity temp
     * @return the current temp
     */
    public int getCavityTemp(){
        return cavityTemp;
    }

    public void turnOnHeaters(){
        this.heatersOn = true;
    }

    public void turnOffHeaters(){
        this.heatersOn = false;
    }

    public void handleTemp() throws InterruptedException {
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
            controller.tempDis();
        }
    }
}
