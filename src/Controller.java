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

        Timer timer = new Timer();

        boolean powerIsOn = false;
        boolean lightIsOn = false;
        boolean doorIsOpen = false;
        boolean topHeaterIsOn = false;
        boolean heatersDead = false;
        boolean bottomHeaterIsOn = false;
        boolean isCooking = false;
        public SimulatorSocketClient socketClient;
        int cookTime = 300;
        int cookTemp = 350;
        int currentTime = cookTime;
        int timeIncrement = 0;
        int cookMode = 1;



        /**Methods and objects used to halt the cooking process or continue it should certain events occur. The lock object
         * is used to */
        boolean timerPause = false;
        final Object lock = new Object();
        public void pause(){
            synchronized (lock){
                timerPause = true;
                isCooking = false;
            }
        }
        public void resume(){
            synchronized (lock){
                timerPause = false;
                isCooking = true;
                lock.notifyAll();
            }
        }



        int cavityTemp = 70;

        /**
         * An array of booleans that shows which heater will be used during the cook.
         * The first element represents the top heater and the second element represents the bottom heater
         */
        boolean heatersUsed[] = new boolean[2];
        /**
         * Booleans relevant to the threads, threadLive is a way to kill the threads in the event of an interruption or
         * if the timer runs out. stopButtonPressed will serve a similar purpose but more to help with event handling from
         * the javaFX or any future port connections.
         * */
        boolean threadLive=false;
        boolean stopButtonPressed=false;

        /**
         * Constructor that just initializes the new socket object
         * @param host the host info
         * @param port the port
         */
        public Controller(String host, int port) throws  IOException{
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


        //Toggle methods for toggleable fields
        public void togglePower() throws IOException {
            // Send a message here
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(1)));
        }

        public void sendLightMessage() throws IOException{
            // send the message to the fx
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(2)));

        }
        public void toggleDoorSensor() throws IOException {
            doorIsOpen = !doorIsOpen;
            if(doorIsOpen){
                System.out.print("Opened ");
            }
            else{
                System.out.print("Closed ");
            }
            System.out.println("door.");
        }

        public void toggleDoor() throws IOException {
            // send a message saying to toggle the door
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(7)));
        }


        /**
         * Method to turn on the heaters
         * @throws IOException ..
         */
        public void turnHeatersOn() throws IOException{
            if(heatersUsed[0]){

                turnOnTopHeater();

                System.out.println("Turned on top heater");
            }
            if(heatersUsed[1]){

                turnOnBottomHeater();

                System.out.println("Turned on bottom heater");
            }
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

            // send a message to the GUI to toggle the top heater
            socketClient.sendMessage(new ArrayList<>(Arrays.asList(3)));
        }

        /**
         * Method to toggle the bottom heater
         * @throws IOException ..
         */
        public void toggleBottomHeater() throws IOException {
            bottomHeaterIsOn = !bottomHeaterIsOn;

            // send a message to the GUI to toggle the bottom heater
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
     *  * 16: Temp focus pressed
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
            System.out.println("Handling input of number " + listIn.get(0));

            if (messageNum != 27){
                if (cooking != null){
                    cooking.setNumberOfStopTimesPressed(0);
                }
            }

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
                    if(lightController.getStatus()) {
                        lightController.turnOffLight();
                    }
                    else{
                        lightController.turnOnLight();
                    }
                    break;
                case 23:
                    boolean isPowerOn = listIn.get(1) == 1;
                    powerIsOn = isPowerOn;
                    iiFace.togglePower();
                    if(!powerIsOn){
                        stopCooking();
                    }
                    break;
                case 24:
                    iiFace.increment();
                    break;
                case 25:
                    iiFace.decrement();
                    break;
                    // start cooking
                case 26:

//                    cooking.setCookMode(iiFace.getMode().getCurrentMode());
//                    cooking.setCookTime(iiFace.getCurrentTimeMin());
//                    cooking.setCookTemp(iiFace.getCurrentTemp());
                    if (!doorMonitor.getDoorStatus()) {

                        System.out.println("Cooking procedure started");
//                        isCooking = true;

                        new Thread(() -> {
                            try {
                                System.out.println("THis is the mode " + iiFace.getMode().getCurrentMode());
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
                    else {
                        System.out.println("Door Is Open");
                    }


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

                    iiFace.togglePower();

                    break;

                case 2:

                    boolean isLightOn = listIn.get(1) == 1;
                    lightIsOn = isLightOn;

                    if (isLightOn){
                        lightController.turnOnLight();
                    }
                    else {
                        lightController.turnOffLight();
                    }

                    break;



                case 9:
                cookMode = 2;
                heatersUsed[1] = true;
                heatersUsed[0] = false;
                    System.out.println("Set the display");
                    break;

                case 10:
                cookMode = 3;
                heatersUsed[1] = false;
                heatersUsed[0] = true;
                    System.out.println("Cleared the display");
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

                    System.out.println("This is the most recent preset pressed" + cookingTypeString);

                    break;


                case 12:

                    int cookingPreset = listIn.get(1);
                    String cookingPresetString = "None";

                    switch (cookingPreset){
                        case 1 -> cookingPresetString = "Nuggets";
                        case 2 -> cookingPresetString = "Pizza";
                    }

                    System.out.println("This is the most recent preset pressed" + cookingPresetString);

                    break;

            }
        }

        public void tempDis(){
                    System.out.println("In the temp display");
                    System.out.println(cooking.getTempReading());
                    iiFace.setCurrentTemp(cooking.getTempReading());
                    System.out.println(iiFace.getCurrentTemp());
                   try{
                       iiFace.updateDisplay();
                    } catch (RuntimeException | IOException e) {
                        throw new RuntimeException(e);
                    }

        }

        public void setIsCooking(boolean isCooking){
            this.isCooking = isCooking;
        }



        public void setUpTimer(){
            cookingTimer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {

                    System.out.println("This is the time " + iiFace.status.getTimeMin() +
                            "This is the time secs " + iiFace.status.getTimeSec());

                    if (iiFace.status.getTimeMin() > 0 || iiFace.status.getTimeSec() > 0){
                        System.out.println("The timer is not done");

                        // send a message to the javafx to update a timer
                        if (iiFace.status.getTimeSec() == 0){
                            int newMin = (int) iiFace.status.getTimeMin() - 1;

                            iiFace.status.displayNewTimeMin(newMin);
                            iiFace.status.displayNewTimeSec(59);

                            iiFace.setCurrentTimeSec(59);
                            iiFace.setCurrentTimeMin(newMin);
                        }
                        else {
                            System.out.println("IN the else i thought ");
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
                        // send a message to the cooking to stop cooking
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
            System.out.println(doorMonitor.getDoorStatus());
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