import java.io.IOException;

public class Main {
    //Main will serve to test the toaster oven simulation
    public static void main(String[] args) throws IOException, InterruptedException {


        // create the object of the simulator
        Controller controller = new Controller("localhost", 1234);


//        // keep testing for a demo
//        while (true) {
//
//            // test toggle the power
//            simulator.togglePower();
//
//            Thread.sleep(1000);
//
//            // test toggle the light
//            simulator.toggleLight();
//
//            Thread.sleep(1000);
//
//            // test toggle the top heater
//            simulator.toggleTopHeater();
//
//            Thread.sleep(7000);
//
//            // test toggle the bottom heater
//            simulator.toggleBottomHeater();
//
//            Thread.sleep(7000);
//
//            // method to check the temp button status
//            simulator.getTempButtonStatus();
//
//            Thread.sleep(1000);
//
//            // method to get the time button status
//            simulator.getTimeButtonStatus();
//
//            Thread.sleep(1000);
//
//            // method to test toggling the door
//            simulator.toggleDoor();
//
//            Thread.sleep(1000);
//
//            // test the toggle door
//            simulator.toggleDoor();
//
//            Thread.sleep(1000);
//
//            // test setting the display
//            simulator.setDisplay();
//
//            Thread.sleep(5000);
//
//            // test clearing the display
//            simulator.clearDisplay();
//
//            Thread.sleep(1000);
//
//            // test getting the latest cook type (Bake, Broil, Roast)
//            simulator.getLatestCookType();
//
//            Thread.sleep(1000);
//
//            // test latest preset
//            simulator.getLatestPreset();
//
//            Thread.sleep(1000);
//
//        }

    }
}