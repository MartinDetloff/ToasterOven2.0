import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class Controller {
    private InputInterface iiFace;
    private Timer cookingTimer;
    private LightController lightController;
    private DoorMonitor doorMonitor;
    private Cooking cooking;

    boolean powerIsOn = false;
        boolean lightIsOn = false;
        boolean doorIsOpen = false;
        boolean topHeaterIsOn = false;
    boolean bottomHeaterIsOn = false;
        boolean isCooking = false;
        public SimulatorSocketClient socketClient;
        int cookTime = 300;
        int cookTemp = 350;
    int cookMode = 1;



        /**Methods and objects used to halt the cooking process or continue it should certain events occur. The lock object
         * is used to */
        boolean timerPause = false;
        final Object lock = new Object();

        public void resume(){
            synchronized (lock){
                timerPause = false;
                isCooking = true;
                lock.notifyAll();
            }
        }


    /**
         * An array of booleans that shows which heater will be used during the cook.
         * The first element represents the top heater and the second element represents the bottom heater
         */
        boolean heatersUsed[] = new boolean[2];


        /**
         * Constructor that just initializes the new socket object
         * @param host the host info
         * @param port the port
         */
        public Controller(String host, int port) {
            try {
                socketClient = new SimulatorSocketClient(host, port);
                iiFace = new InputInterface(this);
                lightController = new LightController(this);
                doorMonitor = new DoorMonitor();
                cooking = new Cooking(this);


                new Thread(() -> {
                    try {
                        grabNextMessage();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        /**
         * Method to constantly grab the messages
         */
        public void grabNextMessage() throws InterruptedException, IOException {
            while (true){
                this.handleInput(socketClient.grabMessage());
            }
        }


        /**
        * Toggle methods for toggleable fields
        * @throws IOException
        */
        public void togglePower() throws IOException {
            this.powerIsOn = !this.powerIsOn;

            if (isCooking){
                cooking.stopCooking(false);
            }
            if (lightIsOn){
                lightController.turnOffLight();
                lightIsOn = !lightIsOn;
            }

            socketClient.sendMessage(new ArrayList<>(Arrays.asList(1)));
        }
        public void sendLightMessage() throws IOException{
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(2)));

        }
        public void toggleDoorSensor() throws IOException {
            doorIsOpen = !doorIsOpen;
        }
        public void toggleDoor() throws IOException {
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(7)));
        }

        /**
         * Method to turn on the heaters
         * @throws IOException ..
         */
        public void turnHeatersOn() throws IOException{
            if(heatersUsed[0]){turnOnTopHeater();}
            if(heatersUsed[1]){turnOnBottomHeater();}
        }

        /**
         * Method to turn on the top heater
         */
        public void turnOnTopHeater() throws IOException {
            topHeaterIsOn = true;
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(3)));
        }

        /**
         * Method to turn on the bottom heater
         */
        public void turnOnBottomHeater() throws IOException {
            bottomHeaterIsOn = true;
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(4)));
        }

        /**
         * Method to turn both of the heaters off
         * @throws IOException ..
         */
        public void turnHeatersOff() throws IOException{
            turnTopHeaterOff();
            turnBottomHeaterOff();
        }

        /**
         * Method to turn off the top heater
         */
        public void turnTopHeaterOff() throws IOException {
            topHeaterIsOn = false;
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(10)));
        }

        /**
         * Method to turn off the bottom heater
         */
        public void turnBottomHeaterOff() throws IOException {
            bottomHeaterIsOn = false;
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(11)));
        }

        /**
         * Method to toggle the top heater
         * @throws IOException ..
         */
        public void toggleTopHeater() throws IOException {
            topHeaterIsOn = !topHeaterIsOn;
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(3)));
        }

        /**
         * Method to toggle the bottom heater
         * @throws IOException ..
         */
        public void toggleBottomHeater() throws IOException {
            bottomHeaterIsOn = !bottomHeaterIsOn;
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(4)));
        }

        /**
         * t: 1 = time, 2 = temp
         * d; 1 = up, -1 = down
         * @param t
         * @param d
         */
        public void incrementTimeOrTemp(int t, int d){
            if(t == 1){
                cookTime += (60*d);
            }
            else{
                cookTemp += (15*d);
            }
        }


        /**
         * Method to get the temp button status
         * @throws IOException ..
         */
        public void getTempButtonStatus() throws IOException {
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(5)));
        }

        /**
         * Method to get the status of how many times we
         * @throws IOException ..
         */
        public void getTimeButtonStatus() throws IOException {
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(6)));
        }

        /**
         * Method to clear the Display
         */
        public void clearDisplay() throws IOException{
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(10)));
        }

        /**
         * method to set the display
         */
        public void setDisplay() throws IOException{
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(9)));
        }

        /**
         * Ask for the current temp
         */
        public void getTemp() throws  IOException{
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(5)));
        }

        /**
         * Ask for the current time
         */
        public void getTime() throws IOException{
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(6)));
        }

        /**
         * get if a preset was clicked
         */
        public void getLatestPreset() throws IOException{
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(12)));
        }

        /**
         * get what cook type is
         */
        public void getLatestCookType() throws IOException{
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(11)));
        }

        /**
         * Method to receive an int and do the appropriate action according to the int passed in
         */

        /**
         *  * Messages FROM FXdeviceSimulator
         *  * 15: Time focus pressed
         *  *  * 16: Temp focus pressed
         *  * 17: Bake pressed
         *  * 18: Broil pressed
         *  * 19: Roast pressed
         *  * 20: Pizza pressed
         *  * 21: Nuggets pressed
         *  * 22: Light pressed
         *  * 23: Power pressed
         *  * 24: Increment pressed
         *  * 25: Decrement pressed
         *  * 26: Start pressed
         *  * 27: Stop/clear pressed
         * @param listIn
         * @throws IOException
         */
        public void handleInput(ArrayList<Integer> listIn) throws IOException {
            int messageNum = listIn.get(0);
            if (messageNum != 27){if (cooking != null){cooking.setNumberOfStopTimesPressed(0);}}
            switch(messageNum) {
                case 15:
                    iiFace.focusTime();
                    break;
                case 16:
                    iiFace.focusTemp();
                    break;
                case 17:
                    iiFace.setMode("Bake");
                    sendMessageToUpdateMode(1);
                    break;
                case 18:
                    iiFace.setMode("Broil");
                    sendMessageToUpdateMode(2);
                    break;
                case 19:
                    iiFace.setMode("Roast");
                    sendMessageToUpdateMode(0);
                    break;
                case 20:
                    iiFace.setPreset("pizza");
                    break;
                case 21:
                    iiFace.setPreset("nuggets");
                    break;
                case 22:
                    if(lightController.getStatus()) {lightController.turnOffLight();}
                    else{lightController.turnOnLight();}
                    break;
                case 23:
                    System.out.println("POWER!");
                    boolean isPowerOn = listIn.get(1) == 1;
                    powerIsOn = isPowerOn;
                    iiFace.togglePower();
                    if(!powerIsOn){stopCooking();}
                    break;
                case 24:
                    iiFace.increment();
                    break;
                case 25:
                    iiFace.decrement();
                    break;

                case 26:
                    if (!doorMonitor.getDoorStatus() && powerIsOn) {
                        new Thread(() -> {
                            try {
                                String cookMode = iiFace.getMode().getCurrentMode();
                                cooking.startCooking(
                                        cookMode,
                                        iiFace.getCurrentTemp(),
                                        iiFace.getCurrentTimeMin()
                                );
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }).start();
                    }
//                    else {}
                    break;

                case 27:
                    cooking.setNumberOfStopTimesPressed(cooking.getNumberOfStopTimesPressed() + 1);
                    cooking.stopCooking(false);
                    break;

                case 28:
                    iiFace.setPreset("None");
                    break;

                case 7:
                    if(listIn.get(1)== 1 )
                    {
                        doorMonitor.changeStatus();
                        UpdateBasedOnDoor();
                        sendMessageToUpdateDoor();
                    }else if (listIn.get(1) == 2) {
                        doorMonitor.changeStatus();
                        UpdateBasedOnDoor();
                        sendMessageToUpdateDoor();
                    }
                    break;

                case 1:
                    System.out.println("Power!!!!!");
                    iiFace.togglePower();
                    break;

                case 2:
                    boolean isLightOn = listIn.get(1) == 1;
                    lightIsOn = isLightOn;
                    System.out.println("Light is now " + lightIsOn);
                    if (isLightOn){lightController.turnOnLight();}
                    else {lightController.turnOffLight();}
                    break;

                case 9:
                    cookMode = 2;
                    heatersUsed[1] = true;
                    heatersUsed[0] = false;
                    break;

                case 10:
                    cookMode = 3;
                    heatersUsed[1] = false;
                    heatersUsed[0] = true;
                    break;
                case 11:
                    cookTime = 900;
                    cookTemp = 375;
                    int cookingType = listIn.get(1);
                    String cookingTypeString = "None";
                    switch (cookingType){
                        case 1 -> cookingTypeString = "Bake";
                        case 2 -> cookingTypeString = "Broil";
                        case 3 -> cookingTypeString = "Roast";
                    }
                    break;
                case 12:
                    int cookingPreset = listIn.get(1);
                    String cookingPresetString = "None";
                    switch (cookingPreset){
                        case 1 -> cookingPresetString = "Nuggets";
                        case 2 -> cookingPresetString = "Pizza";
                    }
                    break;
            }
        }

        public void tempDis(){
                    iiFace.setCurrentTemp(cooking.getTempReading());
                   try{
                       iiFace.updateDisplay();
                    } catch (RuntimeException | IOException e) {
                        throw new RuntimeException(e);
                    }
        }

        /**
         * Setter for the isCooking boolean
         * @param isCooking
         */
        public void setIsCooking(boolean isCooking){
            this.isCooking = isCooking;
        }

        /**
         * A method to set up the timer used during the cooking process
         */
        public void setUpTimer(){
            cookingTimer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (iiFace.status.getTimeMin() > 0 || iiFace.status.getTimeSec() > 0){
                        if (iiFace.status.getTimeSec() == 0){
                            int newMin = (int) iiFace.status.getTimeMin() - 1;
                            iiFace.status.displayNewTimeMin(newMin);
                            iiFace.status.displayNewTimeSec(59);
                            iiFace.setCurrentTimeSec(59);
                            iiFace.setCurrentTimeMin(newMin);
                        }
                        else {
                            int newTime = (int) iiFace.status.getTimeSec() - 1;
                            iiFace.status.displayNewTimeSec(newTime);
                            iiFace.setCurrentTimeSec(newTime);
                        }
                        try {
                            iiFace.updateDisplay();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        try {
                            cooking.stopCooking(false);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        cookingTimer.cancel();
                    }
                }
            };
            cookingTimer.schedule(task,0, 1000);
        }

        /**
         * Method to sotp the cooking timer
         */
        public void stopCookingTimer(){
            cookingTimer.cancel();
        }

        /**
         * Method to send message to the FX to update the cavity
         * @param cavityTemp the temp
         * @throws IOException ..
         */
        public void updateCavityTemp(int cavityTemp) throws IOException {
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(14, cavityTemp)));
        }

    /**
     * Update the door status based on the door sensor
     * @throws IOException
     */
    public void UpdateBasedOnDoor() throws IOException {
            if(doorMonitor.getDoorStatus() && isCooking){
                cooking.stopCooking(true);
            }
        }

        /**
         * Method to send a message to update the display
         * @param timeMin the time
         * @param temp the temp
         * @throws IOException ..
         */
        public void sendMessageToUpdateDisplay(int timeMin, int timeSec, int temp) throws IOException {
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(5, timeMin, timeSec, temp)));
        }

        /**
        * Method to update the door
        */
        public void sendMessageToUpdateDoor() throws IOException{
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(7, doorMonitor.getDoorStatus() ? 1 : 2)));
        }

        /**
         * Method to update the preset
         */
        public void sendMessageToUpdatePreset(int preset) throws IOException {
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(6, preset)));
        }

        /**
         * Method to send the mode of cooking.
         */
        public void sendMessageToUpdateMode(int mode) throws IOException {
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(8, mode)));
        }

        /**
         * Send a message to clear everything
         * @throws IOException ..
         */
        public void clearEverything() throws IOException {
            iiFace.setPreset("None");
            iiFace.setMode("Bake");
            iiFace.setCurrentTemp(350);
            iiFace.setCurrentTimeMin(5);
            iiFace.setCurrentTimeSec(0);
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(9)));
        }


        /**
         * Stops the cook, but doesn't reset the time or anything
         * Saves the time left on the timer and the temperature.
         */
        public synchronized void stopCooking() throws IOException {
            turnHeatersOff();
        }
    }