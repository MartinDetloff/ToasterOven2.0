import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class FXdeviceSimulator extends Application {
    private int toasterHeight = 500;
    private int toasterMainSectionWidth = 700;
    private int toasterRightSectionWidth = 200;
    private int totalToasterWidth = 700;
    private boolean isPowerOn = false;
    private boolean isOnTemp = false;
    private boolean isOnTime = false;
    private boolean isTopHeaterOn = false;
    private boolean isBottomHeaterOn = false;
    private boolean isLightOn = false;
    private boolean doorStatus = false;
    private boolean heaterKill = false;

    private int numberTempButtonPressedIncrement = 0;
    private int numberTimeButtonPressedIncrement = 0;
    private int numberTempButtonPressedDecrement = 0;
    private int numberTimeButtonPressedDecrement = 0;

    private int timesPressed = 0;
    private Circle powerButton;
    private Circle lightButton;
    private Rectangle door = setupDoor();
    private ArrayList<Button> allButtons = new ArrayList<>();
    private SimpleIntegerProperty currentTimeMinutes = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty currentTimeSeconds = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty currentTempF = new SimpleIntegerProperty(0);
    private ObjectOutputStream out;


    Rectangle window = setupWindow();

    private enum setting{
        BAKE,
        BROIL,
        ROAST,
        NOSETT
    }
    private enum preset{
        Nuggets,
        Pizza,
        None
    }
    private setting sett = setting.NOSETT;
    private preset pre = preset.None;
    //Set the heaters
    private Rectangle [] heaters = setupHeaters();


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Method to set up the server socket
     */
    private void setUpServerSocket(){
        int port = 1234;
//            System.out.println("Server start");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port : " + port);

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected");

            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            out = new ObjectOutputStream(clientSocket.getOutputStream());

//                sendMessage(1);

            // make a thread to listen for and process messages
            processMessages(in);
//                updateCavityTemp(100);

        } catch (IOException e) {
            System.out.println("Socket Closed");;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to send messages to the client
     * @param messageOut
     * @throws IOException
     */
    private void sendMessage(ArrayList<Integer> messageOut) throws IOException {
        out.writeObject(messageOut);
        out.flush();
    }

    /**
     * Method to process certain messages
     * @param inputStream the input stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void processMessages(ObjectInputStream inputStream) throws IOException,
            ClassNotFoundException {
        while (inputStream != null){
            int currentMessage = (int) inputStream.readObject();
            switch (currentMessage){
                // toggle power
                case 1 -> {
                    System.out.println("Got a message to turn on power");
                    pressPower(powerButton);

                    // send a message back to make sure the simulator knows we toggled the power
//                    sendMessage(new ArrayList<Integer>(Arrays.asList(1, isPowerOn ? 1 : 2)));

                }
                //toggle light
                case 2 -> {
                    toggleLight(); // toggle light

                    // send message back telling them we turned on the light
                    sendMessage(new ArrayList<Integer>(Arrays.asList(2, isLightOn ? 1 : 2)));
                }
                //toggle top heater
                case 3 -> {

                    if (!isTopHeaterOn){
                        toggleTopHeaterOn();
                    }
                    else{
                        toggleTopHeaterOff();
                    }

                    sendMessage(new ArrayList<Integer>(Arrays.asList(3, isTopHeaterOn ? 1 : 2 )));

                }
                //toggle bottom heater
                case 4 -> {
                    if (!isBottomHeaterOn){
                        toggleBottomHeaterOn();
                    }
                    else {
                        toggleBottomHeaterOff();
                    }

                    sendMessage(new ArrayList<Integer>(Arrays.asList(4,isBottomHeaterOn ? 1 : 2)));

                }

                case 5 -> {
                    // send the temp back
                    sendMessage(new ArrayList<Integer>(Arrays.asList(
                            5,
                            numberTempButtonPressedIncrement,
                            numberTempButtonPressedDecrement))
                    );

                    numberTempButtonPressedDecrement = 0;
                    numberTempButtonPressedIncrement = 0;
                }

                case 6 -> {
                    // send back the time
                    sendMessage(new ArrayList<Integer>(Arrays.asList(
                            6,
                            numberTimeButtonPressedIncrement,
                            numberTimeButtonPressedDecrement))
                    );

                    numberTimeButtonPressedDecrement = 0;
                    numberTimeButtonPressedIncrement = 0;
                }
                // toggle door
                case 7 -> {
                    toggleDoor();

                    sendMessage(new ArrayList<Integer>(Arrays.asList(7, doorStatus ? 1 : 2)));

                }
                // kill the heaters and send a message back
                case 8 -> {

                    killHeaters();

                    sendMessage(new ArrayList<Integer>(Arrays.asList(8)));
                }

                // sets the display to a predetermined time and temp
                case 9 ->{

                    setDisplay(10,0,375);

                    sendMessage(new ArrayList<Integer>(Arrays.asList(9)));
                }

                // this is sending back a message to the simulator saying that we cleared the display
                case 10 ->{

                    clearDisplay();

                    sendMessage(new ArrayList<Integer>(Arrays.asList(10)));
                }


                // this is sending back the status of the cook types as requested by the simulator
                case 11 ->{
                    switch (sett) {
                        case setting.BAKE -> sendMessage(new ArrayList<Integer>(Arrays.asList(11, 1)));
                        case setting.BROIL -> sendMessage(new ArrayList<Integer>(Arrays.asList(11, 2)));
                        case setting.ROAST -> sendMessage(new ArrayList<Integer>(Arrays.asList(11, 3)));
                        case setting.NOSETT -> sendMessage(new ArrayList<Integer>(Arrays.asList(11, 4)));
                    }
                }

                // this is sending back the status of the presets as requested by the simulator
                case 12 ->{
                    switch(pre){
                        case pre.Nuggets-> sendMessage(new ArrayList<Integer>(Arrays.asList(12,1)));
                        case pre.Pizza -> sendMessage(new ArrayList<Integer>(Arrays.asList(12,2)));
                    }
                }
            }
        }
    }

    /**
     * Method to update the cavity temp
     */
    private void updateCavityTemp(int temp){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                currentTempF.set(temp);
            }
        });
    }

    /**
     * Method to be used later to decrement the timer every second
     */
    private void decrementTimer(){
        if (currentTimeSeconds.get() == 0){
            currentTimeMinutes.set(currentTimeMinutes.get() - 1);
            currentTimeSeconds.set(59);
        }
        else{
            currentTimeSeconds.set(currentTimeSeconds.get() - 1);
        }
    }




    @Override
    public void start(Stage primaryStage) {
        new Thread(() -> setUpServerSocket()).start();

        primaryStage.setTitle("Simulator");

        BorderPane root = new BorderPane();
        root.setMaxWidth(1000);
        root.setMaxHeight(1000);

        //The main Pane
        Pane mainSection = setupMainSection();

        //Set the handle
        Rectangle handle = setupHandle();

        //Set the tray
        Rectangle tray = setupTray();

        //Adds all the main section elements.
        mainSection.getChildren().addAll(door,window,handle,tray,heaters[0],heaters[1]);
        // this is just the right hand section in general
        VBox rightHandSection = setUpRightHandSection();

        // set up the display section
        VBox display = setUpDisplay();

        // set up the hbox for the buttons
        HBox bakeSettingButtons = setUpSettingButtons(heaters);

        // set up the button for the preheat
        HBox prebutton = preHeatButtonSetup();

        // set up the button for the stop/clear
        HBox stopButtonBox = stopButtonSetup();

        // set up the button for the start
        HBox startButtonBox = startButtonSetup(heaters);

        // set up the triangle increment and decrement
        VBox triangleButtonsBox = incrementDecrementButtonSetup();

        // now we need to set the power button
        VBox powerButtonBox = powerAndLightButtonSetup();

        // add everything to the rightHandSection
        rightHandSection.getChildren().addAll(display, bakeSettingButtons, prebutton, stopButtonBox,
                startButtonBox, triangleButtonsBox, powerButtonBox);

        // set the right and the center
        root.setRight(rightHandSection);
        root.setCenter(mainSection);

        // turn off all the buttons initially
//            turnOffAllButtons();


        // just setting the scene and the primary stage
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }


    /**
     * Method to turn on all the buttons
     */
    private void turnOnAllButtons(){
        for (Button b : allButtons){
            b.setDisable(false);
        }
    }

    /**
     * Method to turn off all the buttons
     */
    private void turnOffAllButtons(){
        System.out.println("All buttons len" + allButtons.size());
        for (Button b : allButtons){
            b.setDisable(true);
        }
    }

    /**
     * This sets up the main area
     * @return Pane to hold on the features on the left side of the oven
     */
    private Pane setupMainSection(){
        Pane mainSection = new Pane();
        mainSection.setPrefHeight(toasterHeight);
        mainSection.setPrefWidth(toasterMainSectionWidth);
        mainSection.setBorder(Border.stroke(Color.BLACK));
        mainSection.setStyle("-fx-background-color: darkgray;");
        return mainSection;
    }

    /**
     * This houses the door using a rectangle
     * @return The rectangle that has the door features.
     */
    private Rectangle setupDoor(){
        // Set the door
        Rectangle door = new Rectangle(20,20,450,450);
        door.setStroke(Color.BLACK);
        door.setFill(Color.LIGHTGRAY);
        door.setArcWidth(50.0);
        door.setArcHeight(50.0);
        return door;
    }

    /**
     * Rectangle that has the window, can change color based on the settings on the oven.
     * @return the window rectangle with features.
     */
    private Rectangle setupWindow(){
        Rectangle window = new Rectangle(60,60,375,375);
        window.setStroke(Color.BLACK);
        window.setFill(Color.LIGHTGRAY);
        window.setArcHeight(50.0);
        window.setArcWidth(50.0);
        return window;
    }

    /**
     * Rectangle that deals with the handle
     * @return Handle and its features.
     */
    private Rectangle setupHandle(){
        Rectangle handle = new Rectangle(50,35,400,10);
        handle.setStroke(Color.BLACK);
        handle.setFill(Color.BLACK);
        handle.setArcHeight(5);
        handle.setArcWidth(5);
        setupHandleCLick(handle);
        return handle;
    }

    /**
     * Method to handle the handle click
     * @param handle the handle
     */
    private void setupHandleCLick(Rectangle handle){
        handle.setOnMouseClicked(event -> {
            if(doorStatus){
                doorStatus = false;
            }else{
                doorStatus = true;
            }
        });
    }

    /**
     * Method to toggle the door
     */
    private void toggleDoor(){
        if(doorStatus){
            System.out.println("Door is now Closed");
            doorStatus = false;

            //door.setFill(Color.BLACK);
            // window.setFill(Color.LIGHTGRAY);
        }else
        {
            System.out.println("Door is now Open");

            doorStatus = true;
            //door.setFill(Color.TRANSPARENT);
            //window.setFill(Color.TRANSPARENT);

        }
    }



    /**
     * Rectangle that holds the tray.
     * @return tray and its features
     */
    private Rectangle setupTray(){
        Rectangle tray = new Rectangle(60,325,375,20);
        Stop[] stops = new Stop[] {
                new Stop(0, Color.BLACK),
                new Stop(1, Color.BLACK),
                new Stop(0.5, Color.DARKGRAY)
        };
        LinearGradient lg1 = new LinearGradient(60,325 , 385, 345,
                false, CycleMethod.NO_CYCLE, stops);

        tray.setFill(lg1);
        return tray;
    }

    /**
     * Rectangles that have the heaters. Each one is different and can be manipulated sepreatly.
     * @return Array of rectangles that deal with the heaters.
     */
    private Rectangle[] setupHeaters(){
        Rectangle heater1 = new Rectangle(75,60,350,10);
        Rectangle heater2 = new Rectangle(75,425,350,10);
        heater1.setFill(Color.BLACK);
        heater1.setArcHeight(40.0);
        heater1.setArcWidth(15.0);
        heater2.setFill(Color.BLACK);
        heater2.setArcHeight(40.0);
        heater2.setArcWidth(15.0);
        Rectangle[] temp = {heater1,heater2};
        return temp;
    }
    /**
     * Method to set up the right hand side of the toaster
     * @return the vbox of the right hand side
     */
    private VBox setUpRightHandSection(){

        // this is just the right hand section
        VBox rightHandSection = new VBox(30);
        rightHandSection.setPrefWidth(toasterRightSectionWidth);
        rightHandSection.setPrefHeight(toasterHeight);
        // set the styles of the right hand side
        rightHandSection.setStyle("-fx-border-color: black;");
        rightHandSection.setStyle("-fx-background-color: darkGray;");

        return rightHandSection;
    }

    /**
     * This method defines the display section for our right hand side of the toaster.
     * @return the vbox for the right hand side
     */
    private VBox setUpDisplay(){
        // need to define a new hbox for the time/ temp
        VBox display = new VBox(5);
        HBox displayButtonSegment = new HBox(5);
        displayButtonSegment.setPrefHeight(30);
        displayButtonSegment.setPrefWidth(10);

        display.setStyle("-fx-border-color: black;");


        // adding the buttons to the display and we also needthe text
        Button timeButton = new Button("Time");
        timeButton.setStyle("-fx-font-family: 'Comic Sans MS'");
        timeButton.setStyle("-fx-border-color: black");

        Button tempButton = new Button("Temp");
        tempButton.setStyle("-fx-font-family: 'Comic Sans MS'");
        tempButton.setStyle("-fx-border-color: black");

        Label displayText = new Label();
        displayText.textProperty().bind(Bindings.concat(
                currentTimeMinutes.asString("%02d"),
                ":",currentTimeSeconds.asString("%02d"), "    ",
                currentTempF.asString("%02d"), " F°"
        ));
        displayText.setStyle("-fx-font-family: 'Comic Sans MS'");
//            displayText.textProperty().bind(
//                    Bindings.concat(
//                    currentTimeMinutes.asString("%02d"),
//                    ":",currentTimeMinutes.asString("%02d")
//                    )
//            );

        // adding the button to the arrayList
        allButtons.add(timeButton);
        allButtons.add(tempButton);

        // call the handlers for the buttons
        handleTempButtonClick(tempButton, timeButton);
        handleTimeButtonClick(timeButton, tempButton);

        // set the alignments to the center of the right hand sections
        displayButtonSegment.setAlignment(Pos.CENTER);
        display.setAlignment(Pos.CENTER);
        displayButtonSegment.getChildren().addAll(timeButton, tempButton);

        // add onto the vbox the Hbox for the buttons and the text
        display.getChildren().addAll(displayButtonSegment, displayText);

        return display;
    }


    /**
     * Method to set the time text when needed
     * @return the time text
     */
    private Label setTimeText(Label timeLabel){
        timeLabel.textProperty().bind(
                Bindings.concat(
                        currentTimeMinutes.asString("%02d"),
                        ":",currentTimeSeconds.asString("%02d")
                )
        );
        return timeLabel;
    }

    /**
     * set the display from the simulator
     * @param Min
     * @param sec
     * @param temp
     */
    private void setDisplay(int Min,int sec, int temp){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                currentTimeSeconds.set(sec);
                currentTimeMinutes.set(Min);
                currentTempF.set(temp);
            }
        });

    }

    /**
     * clear the Display if told by simulator
     */
    private void clearDisplay(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                currentTempF.set(0);
                currentTimeMinutes.set(0);
                currentTimeSeconds.set(0);
            }
        });
    }

    /**
     * Method to set the time text when needed
     * @return the time text
     */
    private Label setTempText(Label tempLabel){
        tempLabel.textProperty().bind(
                Bindings.concat(
                        currentTempF.asString("%02d"), " F°"
                )
        );
        return tempLabel;
    }


    /**
     * Method to handle the click on the time button on our simulation
     * @param timeButton the time button
     */
    private void handleTimeButtonClick(Button timeButton, Button tempButton){
        timeButton.setOnMouseClicked(event -> {
            System.out.println("Clicked on the time button");

            if (!isOnTime){
//                    setTimeText(displayText);
                timeButton.setStyle("-fx-font-weight: bold;");
                tempButton.setStyle("-fx-font-weight: normal;");
                this.isOnTime = true;
                this.isOnTemp = false;
            }
        });
    }

    /**
     * Method to handle the click on the temp button on our simulation
     * @param tempButton the time button
     */
    private void handleTempButtonClick(Button tempButton, Button timeButton){
        tempButton.setOnMouseClicked(event -> {
            System.out.println("Clicked on the temp button");
            if (!isOnTemp){
//                    setTempText(displayText);
                tempButton.setStyle("-fx-font-weight: bold;");
                timeButton.setStyle("-fx-font-weight: normal;");
                this.isOnTemp = true;
                this.isOnTime = false;
            }
        });
    }


    /**
     * This method sets up the bake broil and roast buttons for the simulation
     * @return the HBox of the buttons
     */
    private HBox setUpSettingButtons(Rectangle[] heaters){
        // in this section we are defining the bake broil and roast
        Button bakeButton = new Button("Bake");
        Button broilButton = new Button("Broil");
        Button roastButton = new Button("Roast");

        // add all the buttons to the arraylist
        allButtons.add(bakeButton);
        allButtons.add(broilButton);
        allButtons.add(roastButton);

        bakeButton.setStyle("-fx-font-family: 'Comic Sans MS'");
        bakeButton.setStyle("-fx-border-color: black");

        broilButton.setStyle("-fx-font-family: 'Comic Sans MS'");
        broilButton.setStyle("-fx-border-color: black");

        roastButton.setStyle("-fx-font-family: 'Comic Sans MS'");
        roastButton.setStyle("-fx-border-color: black");



        handleBakeButtonClick(bakeButton);
        handleBroilButtonClick(broilButton);
        handleRoastButtonClick(roastButton);

        // set up the hbox for the buttons
        HBox bakeSettingButtons = new HBox(5);
        bakeSettingButtons.getChildren().addAll(bakeButton, broilButton, roastButton);
        bakeSettingButtons.setAlignment(Pos.CENTER);
        bakeSettingButtons.setStyle("-fx-border-color: black;");

        return bakeSettingButtons;
    }

    /**
     * Method to handle the click on the bake button on our simulation
     * @param bakeButton the time button
     */
    private void handleBakeButtonClick(Button bakeButton){
        bakeButton.setOnMouseClicked(event -> {

//                try {
//                    sendMessage(new ArrayList<Integer>(Arrays.asList(9)));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }

            sett = setting.BAKE;
            pre = preset.None;
            //System.out.println("Clicked on the bake button");

        });
    }

    /**
     * Method to handle the click on the broil button on our simulation
     * @param broilButton the time button
     */
    private void handleBroilButtonClick(Button broilButton){
        broilButton.setOnMouseClicked(event -> {

            sett = setting.BROIL;
            pre = preset.None;
        });
    }

    /**
     * Method to handle the click on the broil button on our simulation
     * @param roastButton the time button
     */
    private void handleRoastButtonClick(Button roastButton){
        roastButton.setOnMouseClicked(event -> {

//                try {
//                    sendMessage(new ArrayList<Integer>(Arrays.asList(8)));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }

            sett = setting.ROAST;
            pre = preset.None;
            //System.out.println("Clicked on the roast button");
        });
    }

    /**
     * This method just sets up the pre-heat button for the simulation
     * @return the HBox with the button
     */
    private HBox preHeatButtonSetup(){
        // set up the button for the preheat function
        Button pizza = new Button("Pizza");
        allButtons.add(pizza);
        Button nuggets = new Button("Nuggets");
        allButtons.add(nuggets);

        pizza.setStyle("-fx-font-family: 'Comic Sans MS'");
        pizza.setStyle("-fx-border-color: black");
        nuggets.setStyle("-fx-font-family: 'Comic Sans MS'");
        nuggets.setStyle("-fx-border-color: black");

        // handle the mouse clicks
        handlePreButtonClick(pizza,nuggets);
        HBox prebutton = new HBox(5);
        prebutton.getChildren().addAll(pizza,nuggets);
        prebutton.setAlignment(Pos.CENTER);
        prebutton.setStyle("-fx-border-color: black;");

        return prebutton;
    }

    /**
     * Method to handle the click on the pre-het button on our simulation
     * @param pizza and nuggets the time button
     */
    private void handlePreButtonClick(Button pizza, Button nuggets){
        pizza.setOnMouseClicked(event -> {

//                try {
//                    sendMessage(new ArrayList<Integer>(Arrays.asList(11)));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }

//                sett = setting.BAKE;
            pre = preset.Pizza;
            //currentTimeMinutes.set(15);
            //currentTempF.set(375);

        });
        nuggets.setOnMouseClicked(event -> {

//                try {
//                    sendMessage(new ArrayList<Integer>(Arrays.asList(12)));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }

//                sett = setting.ROAST;
            pre = preset.Nuggets;
            //currentTimeMinutes.set(10);
            //currentTempF.set(400);
        });
    }

    /**
     * This method just sets up the stop button for the simulation
     * @return the HBox with the button
     */
    private HBox stopButtonSetup(){
        // set up the button for the stop/clear
        HBox stopButtonBox = new HBox();
        Button stopButton = new Button("Stop/Clear");
        allButtons.add(stopButton);

        stopButton.setStyle("-fx-font-family: 'Comic Sans MS'");
        stopButton.setStyle("-fx-border-color: black");

        // handle the mouse clicks
        handleStopButtonClick(stopButton);
        stopButtonBox.getChildren().add(stopButton);
        stopButtonBox.setAlignment(Pos.CENTER);
        stopButtonBox.setStyle("-fx-border-color: black;");
        return stopButtonBox;
    }

    /**
     * Method to handle the click on the stop button on our simulation
     * @param stopButton the time button
     */
    private void handleStopButtonClick(Button stopButton){
        stopButton.setOnMousePressed(event -> {

            try {
                sendMessage(new ArrayList<Integer>(Arrays.asList(14)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            timesPressed++;

            if(timesPressed == 1)
            {
                System.out.println("Clicked on the stop button");
                //handleStop();
                //Pause the timer
            }
            else if (timesPressed == 2)
            {
                //handleClear();
            }
            //TODO: need to send info over the socket about this
            //TODO: first click is stop, and second click is clear
        });
    }

    /**
     * Method to handle the stop button action
     */
    private void handleStop(){
        System.out.println("Clicked on the stop button");
        // TODO : Pause the timer
    }

    /**
     * Method to handle clear action button
     */
    private void handleClear() {

        System.out.println("Clicked on the clear button");
        currentTempF.set(0); // TODO: Temp decrease over time eventually
        currentTimeMinutes.set(0);
        currentTimeSeconds.set(0);

    }

    /**
     * This method just sets up the stop button for the simulation
     * @return the HBox with the button
     */
    private HBox startButtonSetup(Rectangle[] heaters){
        // set up the button for the start
        HBox startButtonBox = new HBox();
        Button startButton = new Button("Start");
        allButtons.add(startButton);

        startButton.setStyle("-fx-font-family: 'Comic Sans MS'");
        startButton.setStyle("-fx-border-color: black");
//            startButton.setStyle("-fx-background-color: silver");

        // handle the mouse clicks
        handleStartButtonClick(startButton);

        startButtonBox.getChildren().add(startButton);
        startButtonBox.setAlignment(Pos.CENTER);
        startButtonBox.setStyle("-fx-border-color: black;");

        return startButtonBox;
    }

    /**
     * Method to handle the click on the start button on our simulation
     * @param startButton the time button
     */
    private void handleStartButtonClick(Button startButton){
        startButton.setOnMouseClicked(event -> {

            try {
                sendMessage(new ArrayList<Integer>(Arrays.asList(13)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //startButton();
        });
    }
    private void startButton(){
        switch (sett) {
            case ROAST -> {
                toggleTopHeaterOn();
                toggleBottomHeaterOn();
            }
            case BAKE -> {
                toggleTopHeaterOff();
                toggleBottomHeaterOn();
            }
            case BROIL -> {
                toggleTopHeaterOn();
                toggleBottomHeaterOff();
            }
        }
        System.out.println("Clicked on the start button");
    }

    /**
     * Method to toggle the top heater on
     */
    private void toggleTopHeaterOn() {
        if (heaterKill) {
            //heater is dead
        }else {
            if (heaters[0].getFill() == Color.BLACK) {
                FillTransition ft = new FillTransition(Duration.seconds(6), heaters[0], Color.BLACK, Color.RED);
                ft.setCycleCount(0);
                ft.setAutoReverse(false);
                ft.play();
            } else {
                heaters[0].setFill(Color.RED);
            }
            isTopHeaterOn = true;
        }

    }

    /**
     * Method to toggle the bottom heater on
     */
    private void toggleBottomHeaterOn(){

        if(heaterKill){
            //heater is dead
        }else {
            if (heaters[1].getFill() == Color.BLACK) {
                FillTransition ft = new FillTransition(Duration.seconds(6), heaters[1], Color.BLACK, Color.RED);
                ft.setCycleCount(0);
                ft.setAutoReverse(false);
                ft.play();
            } else {
                heaters[1].setFill(Color.RED);
            }
            isBottomHeaterOn = true;
        }

    }

    /**
     * Method to toggle the top heater off
     */
    private void toggleTopHeaterOff(){
        if(heaters[0].getFill() == Color.RED) {
            FillTransition ft = new FillTransition(Duration.seconds(6), heaters[0], Color.RED, Color.BLACK);
            ft.setCycleCount(0);
            ft.setAutoReverse(false);
            ft.play();
        }else{
            heaters[0].setFill(Color.BLACK);
        }
        isTopHeaterOn = false;
    }

    /**
     * Method to toggle the bottom heater off
     */
    private void toggleBottomHeaterOff(){
        if(heaters[1].getFill() == Color.RED) {
            FillTransition ft = new FillTransition(Duration.seconds(6), heaters[1], Color.RED, Color.BLACK);
            ft.setCycleCount(0);
            ft.setAutoReverse(false);
            ft.play();
        }else{
            heaters[1].setFill(Color.BLACK);
        }
        isBottomHeaterOn = false;
    }

    /**
     * This method just sets up the triangle increment/ decrement button for the simulation
     * @return the HBox with the button
     */
    private VBox incrementDecrementButtonSetup(){
        // set up the triangle increment and decrement
        VBox triangleButtonsBox = new VBox(5);
        Polygon triangleIncrement = new Polygon(0, 50, 50, 50, 25, 0);
        Polygon triangleDecrement = new Polygon(0, 0, 50, 0, 25, 50);

//            Image image = new Image(Objects.requireNonNull(getClass().getResource("add-icon3.PNG")).toExternalForm());
//            Image image2 = new Image(Objects.requireNonNull(getClass().getResource("subtract-icon1.PNG")).toExternalForm());

        triangleIncrement.setStyle("-fx-border-color: white");
        triangleDecrement.setStyle("-fx-border-color: white");

        triangleDecrement.setFill(Color.BLACK); //new ImagePattern(image2)
        triangleIncrement.setFill(Color.BLACK); //new ImagePattern(image)

        // handle the mouse clicks
        handleIncrementButtonClick(triangleIncrement);
        handleDecrementButtonClick(triangleDecrement);

        triangleButtonsBox.getChildren().addAll(triangleIncrement, triangleDecrement);
        triangleButtonsBox.setAlignment(Pos.CENTER);
        triangleButtonsBox.setStyle("-fx-border-color: black;");

        return triangleButtonsBox;
    }

    /**
     * Method to handle the click on the broil button on our simulation
     * @param incrementButton the time button
     */
    private void handleIncrementButtonClick(Polygon incrementButton){
        incrementButton.setOnMouseClicked(event -> {

            System.out.println("Clicked on the increment button");
            if (isOnTime) {

                numberTimeButtonPressedIncrement++;
//                    try {
//                        sendMessage(6);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }

                //currentTimeMinutes.set(currentTimeMinutes.get() + 1);
            }
            else if(isOnTemp){

                numberTempButtonPressedIncrement++;
//                    try {
//                        sendMessage(4);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }

                // the max temp is 500
                //if (currentTempF.get() < 500){
                //   currentTempF.set(currentTempF.get() + 15);
                // }
            }
            timesPressed = 0;
        });
    }


    /**
     * Method to handle the click on the broil button on our simulation
     * @param
     * decrementButton the time button
     */
    private void handleDecrementButtonClick(Polygon decrementButton){
        decrementButton.setOnMouseClicked(event -> {



            System.out.println("Clicked on the decrement button");
            if(isOnTime){

                numberTimeButtonPressedDecrement++;

//                    try {
//                        sendMessage(7);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }

                // the min is always 0
                //if (currentTimeMinutes.get() != 0){
                // currentTimeMinutes.set(currentTimeMinutes.get() - 1);
                //  }
            }
            else if (isOnTemp){

                numberTempButtonPressedDecrement++;

//                    try {
//                        sendMessage(5);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }

                // the min is always 0
                // if (currentTempF.get() != 0){
                //    currentTempF.set(currentTempF.get() - 15);
                //  }
            }
            timesPressed = 0;
        });
    }

    /**
     * This method just sets up the power button for the simulation
     * @return the HBox with the button
     */
    private VBox powerAndLightButtonSetup(){

        // now we need to set the power button
        VBox powerButtonBox = new VBox(5);

        // hboxes for the buttons
        HBox powerButtonThing = new HBox(5);
        HBox lightButtonThing = new HBox(11);

        // text for the power/ light button
        Text powerText = new Text("Power");
        Text lightText = new Text("Light");

        // circles for the buttons
        powerButton = new Circle(10);
        lightButton = new Circle(10);

        // handle the mouse clicks
        handlePowerButtonClicks(powerButton);
        handleLightButtonClicks(lightButton);

        // add the button and the
        powerButtonThing.getChildren().addAll(powerButton, powerText);
        lightButtonThing.getChildren().addAll(lightButton, lightText);

        // set the alignment of the HBoxes to the center
        powerButtonThing.setAlignment(Pos.CENTER);
        lightButtonThing.setAlignment(Pos.CENTER);

        powerButtonBox.setStyle("-fx-border-color: black;");
        powerButtonBox.getChildren().addAll(powerButtonThing, lightButtonThing);
        powerButtonBox.setAlignment(Pos.CENTER);

        return powerButtonBox;
    }

    /**
     * Method to handle the click on the power button on our simulation
     * @param powerButton the time button
     */
    private void handlePowerButtonClicks(Circle powerButton){
        powerButton.setOnMouseClicked(event -> {
            try {
                sendMessage(new ArrayList<Integer>(Arrays.asList(1, isPowerOn ? 1 : 2)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            pressPower(powerButton);
        });
    }

    /**
     * Method to handle the click on the power button on our simulation
     * @param lightButton the time button
     */
    private void handleLightButtonClicks(Circle lightButton){
        lightButton.setOnMouseClicked(event -> {
            // Light
            // es not turn on after
            if(!isPowerOn){
                System.out.println("Not On");
            }else{
                if(!isLightOn)
                {
                    System.out.println("Setting light to true");
                    isLightOn = true;
                    lightButton.setFill(Color.GREEN);
                }else{
                    System.out.println("Setting light to false");
                    isLightOn = false;
                    lightButton.setFill(Color.BLACK);
                }
            }
        });
    }

    /**
     * Method to press the power button
     * @param powerButton the power button
     */
    private void pressPower(Circle powerButton){
        // TODO: send some info through socket possibly
        if (isPowerOn){
            System.out.println("Power Off");
            // turn the power off and disable all the buttons
            powerButton.setFill(Color.BLACK);
            isPowerOn = false;

        }
        else {
            System.out.println("Power On");
            powerButton.setFill(Color.GREEN);
            isPowerOn = true;
//                turnOnAllButtons();
        }

        timesPressed = 0;
    }

    /**
     * Method to simulate pressing the lightButton
     * @param lightButton the light button
     */
    private void pressLight(Circle lightButton){
        if(isLightOn){
            lightButton.setFill(Color.GREEN);
            window.setFill(Color.LIGHTYELLOW);

        }else
        {
            lightButton.setFill(Color.BLACK);
            window.setFill(Color.LIGHTGRAY);
        }
    }

    /**
     * Method to turn on/off the light depending on the message from socket.
     */
    private void toggleLight(){

        if (!isLightOn) {
            window.setFill(Color.LIGHTYELLOW);
            lightButton.setFill(Color.GREEN);
            isLightOn = true;
        }
        else {
            window.setFill(Color.LIGHTGRAY);
            lightButton.setFill(Color.BLACK);
            isLightOn = false;
        }
    }

    private void killHeaters(){
        heaterKill = true;

    }
}