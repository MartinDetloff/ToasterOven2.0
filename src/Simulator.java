import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

public class Simulator {
    //Fields
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
    TimerTask task = new TimerTask() {
        @Override
        public void run() {

            //Here is where it will handle the pause check, if it is false, it should continue, otherwise it waits
            synchronized (lock){
                while (timerPause){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            try {
                handleHeaters();
                System.out.println("HandleHeaters called.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            timeIncrement++;
            currentTime = cookTime - timeIncrement;
            System.out.println("The current cavity temperature is: " + cavityTemp);

            if(!threadLive){
                timer.cancel();
            }

        }
    };

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


    interruptCheckThread interrupter = new interruptCheckThread();

    int cavityTemp = 70;
    /**An array of integers that stores 4 ints with the following values
     * [minutes, seconds, temperature, mode]
     * Mode key:
     * 1 - Roast (both heaters)
     * 2 - Bake (bottom heater)
     * 3 - Broil (top heater)
     */
    //int cookingInfo[] = new int[4];
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
    public Simulator(String host, int port) throws  IOException{
        try {
            socketClient = new SimulatorSocketClient(host, port);

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
        // TODO: Send a message here
        socketClient.sendMessage(1);

//        powerIsOn = !powerIsOn;
    }

    public void toggleLight() throws IOException{
        // send the message to the fx
        socketClient.sendMessage(2);

//        if(lightIsOn){
//            lightIsOn = false;
//        }
//        else{
//            lightIsOn = true;
//        }

    }
    public void toggleDoorSensor() throws IOException {
//        socketClient.sendMessage(5);
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
        socketClient.sendMessage(7);
    }


    /**
     * Method to turn on the heaters
     * @throws IOException ..
     */
    public void turnHeatersOn() throws IOException{
        if(heatersUsed[0]){

            turnOnTopHeater();

            System.out.println("Turned on top heater");
            socketClient.sendMessage(3);
        }
        if(heatersUsed[1]){

            turnOnBottomHeater();

            System.out.println("Turned on bottom heater");
            socketClient.sendMessage(4);
        }
    }

    /**
     * Method to turn on the top heater
     */
    private void turnOnTopHeater(){
        topHeaterIsOn = true;
    }

    /**
     * Method to turn on the bottom heater
     */
    private void turnOnBottomHeater(){
        bottomHeaterIsOn = true;
    }


    /**
     * Method to turn both of the heaters off
     * @throws IOException ..
     */
    public void turnHeatersOff() throws IOException{
        turnTopHeaterOff();
        turnBottomHeaterOff();

        socketClient.sendMessage(6);
    }

    /**
     * Method to turn off the top heater
     */
    private void turnTopHeaterOff(){
        topHeaterIsOn = false;
    }

    /**
     * Method to turn off the bottom heater
     */
    private void turnBottomHeaterOff(){
        bottomHeaterIsOn = false;
    }


    /**
     * Method to toggle the top heater
     * @throws IOException ..
     */
    public void toggleTopHeater() throws IOException {
        topHeaterIsOn = !topHeaterIsOn;

        // send a message to the GUI to toggle the top heater
        socketClient.sendMessage(3);
    }

    /**
     * Method to toggle the bottom heater
     * @throws IOException ..
     */
    public void toggleBottomHeater() throws IOException {
        bottomHeaterIsOn = !bottomHeaterIsOn;

        // send a message to the GUI to toggle the bottom heater
        socketClient.sendMessage(4);
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
        socketClient.sendMessage(5);
    }

    /**
     * Method to get the status of how many times we
     * @throws IOException ..
     */
    public void getTimeButtonStatus() throws IOException {
        socketClient.sendMessage(6);
    }

    /**
     * Method to clear the Display
     */
    public void clearDisplay() throws IOException{
        socketClient.sendMessage(10);

    }

    /**
     * method to set the display
     */
    public void setDisplay() throws IOException{
        socketClient.sendMessage(9);
    }
    /**
     * Ask for the current temp
     */
    public void getTemp() throws  IOException{
        socketClient.sendMessage(5);
    }
    /**
     * Ask for the current time
     */
    public void getTime() throws IOException{
        socketClient.sendMessage(6);
    }
    /**
     * get if a preset was clicked
     */
    public void getLatestPreset() throws IOException{
        socketClient.sendMessage(12);
    }
    /**
     * get what cook type is
     */
    public void getLatestCookType() throws IOException{
        socketClient.sendMessage(11);
    }
    /**
     * Method to receive an int and do the appropriate action according to the int passed in
     */
    public void handleInput(ArrayList<Integer> listIn) throws IOException {
        int messageNum = listIn.get(0);
        System.out.println("Handling input of number " + listIn.get(0));


        switch(messageNum) {

            case 1:
//                togglePower();
                boolean isPowerOn = listIn.get(1) == 1;

                System.out.println("Successfully toggled the power it is now " +
                        (isPowerOn ? "On" : "Off" ));

                // update the power
                powerIsOn = isPowerOn;

                break;

            case 2:
//                toggleDoorSensor();

                boolean isLightOn = listIn.get(1) == 1;

                System.out.println("Successfully toggled the light it is now " +
                        (isLightOn ? "On" : "Off"));

                // update the light status
                lightIsOn = isLightOn;

                break;

            case 3:
//                toggleLight();

                boolean isTopHeaterOn = listIn.get(1) == 1;

                System.out.println("Successfully toggled the top heater it is now " +
                        (isTopHeaterOn ? "On" : "Off"));

                topHeaterIsOn = isTopHeaterOn;

                break;

            case 4:

                boolean isBotHeaterOn = listIn.get(1) == 1;

                System.out.println("Successfully toggled the bottom heater it is now " +
                        (isBotHeaterOn ? "On" : "Off"));

                bottomHeaterIsOn = isBotHeaterOn;
                break;

            case 5:
                System.out.println("Successfully got the ststus of the temp increments it was incremented " +
                        listIn.get(1) + " Times and decremented " + listIn.get(2) + " Times");
                // TODO : Maybe update the display text status here for later.
                break;

            case 6:
                System.out.println("Successfully got the ststus of the time increments it was incremented " +
                        listIn.get(1) + " Times and decremented " + listIn.get(2) + " Times");
                // TODO : Maybe update teh display text status here for later.
                break;

            case 7:
                boolean doorStat = listIn.get(1) == 1;
                System.out.println("Successfully toggled the door it is now " +
                        (doorStat ? "open" : "closed"));

                doorIsOpen = doorStat;
                break;

            case 8:
//                cookMode = 1;
//                heatersUsed[0] = true;
//                heatersUsed[1] = true;
                System.out.println("Successfully killed the heaters ");
                break;

            case 9:
//                cookMode = 2;
//                heatersUsed[1] = true;
//                heatersUsed[0] = false;
                System.out.println("Set the display");
                break;

            case 10:
//                cookMode = 3;
//                heatersUsed[1] = false;
//                heatersUsed[0] = true;
                System.out.println("Cleared the display");
                break;

            case 11:
//                cookTime = 900;
//                cookTemp = 375;
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

            case 13:
                startCooking();
                break;

            case 14:
                stopCooking();
                break;

        }
    }


    /**
     * Method to start the cooking process. When it is called the method checks to see if the power is on. If the power is on and
     * the door is closed then the cook starts (Begins a thread method that starts a cook).
     */
    public Boolean checkCook(){
        if(!powerIsOn){
            System.out.println("Power is off, cannot start cooking.");
            return false;
        }
        else if(doorIsOpen){
            System.out.println("Door is open, cannot start cooking.");
            return false;
        }
        return true;
    }


    /**
     * Method to handle the temperature in the oven and turn on/off the heaters.
     * If the cavity temperature exceeds 500 degrees, then the heaters are killed.
     * If the temperature is below the set temperature, turn on the heaters
     * If the temperature is above the set temperature, turn off the heaters
     * @throws IOException
     */
    public void handleHeaters() throws IOException {
        if(cavityTemp >= 500){
            heatersDead = true;
            turnHeatersOff();
        }
        else if(cavityTemp <= cookTemp){
            System.out.println("cavityTemp < cookTemp");
            turnHeatersOn();
            if(topHeaterIsOn){ cavityTemp += 5;}
            if(bottomHeaterIsOn) { cavityTemp += 5;}
        }
        else if(cavityTemp > cookTemp){
            turnHeatersOff();
            cavityTemp -= 2;
        }
    }

    /**
     * Stops the cook, but doesn't reset the time or anything
     * Saves the time left on the timer and the temperature.
     */
    public synchronized void stopCooking() throws IOException {
        pause();
        stopButtonPressed=false;
        threadLive=false;
        turnHeatersOff();
    }

    /**
     * Resets the oven
     * @throws IOException
     */
    public void resetOven() throws IOException {
        stopCooking();
        toggleLight();
        cookTemp = 350;
        cookTime = 300;
        cookMode = 1;
    }

    /**
     * Method to start the cook
     */
    public void startCooking(){
        //System.out.println("Starting " + cookMode + " cook at " + cookTemp + " degrees fahrenheit for " + cookTime + " seconds.");
        isCooking = true;
        if(heatersUsed[0]){
            //TODO send message to turn on top heater
        }
        if(heatersUsed[1]){
            //TODO send message to turn on bottom heater
        }

        timer.scheduleAtFixedRate(task, 0, 1000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Cancelling the task after " + cookTime + " seconds.");
                timer.cancel();
            }
        }, cookTime * 1000);

        //Turn on all the stuff
        //Watch interrupts while timer runs
        //At interrupt, pause
        //At timer finish, stop and reset

        //setting up threads to run and cook

        /**
         * I don't think we need this boolean any-more do we?
         */
        threadLive=true;

        interrupter.start();
    }

    /**
     * Method that converts the minutes from the first two elements of cookingInfo into seconds and returns them as an int
     * @return
     */
    public void printInfo(){
        System.out.print("\n\n\nPower: ");
        if(powerIsOn){
            System.out.println("on");
        }
        else{
            System.out.println("off");
        }
        System.out.print("Door: ");
        if(doorIsOpen){
            System.out.println("open");
        }
        else{
            System.out.println("closed");
        }
        System.out.print("Light: ");
        if(lightIsOn){
            System.out.println("on");
        }
        else{
            System.out.println("off");
        }
        System.out.print("Top heater: ");
        if(topHeaterIsOn){
            System.out.println("on");
        }
        else{
            System.out.println("off");
        }
        System.out.print("Bottom heater: ");
        if(bottomHeaterIsOn){
            System.out.println("on");
        }
        else{
            System.out.println("off");
        }
        System.out.println("\nCooking cavity temperature: " + cavityTemp);
        System.out.println("Current cooking info:");
        System.out.println("Temp: " + cookTemp + "\nTime: " + cookTime + "\nMode: " + cookMode);
    }

    /**
     * Thread to act as the simulator cooking conditions other than the timer. This is meant to check for interruptions
     * such as the oven being opened, the pause/reset button pressed, and any other possible interruptions we may wish
     * to include in the future. Calls the stopCooking method that will pause the timer.
     */
    public class interruptCheckThread extends Thread{
        @Override
        public void run(){
            while (threadLive){
                if(!powerIsOn && doorIsOpen && stopButtonPressed){
                    try {
                        stopCooking();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}