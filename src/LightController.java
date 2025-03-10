import java.io.IOException;

public class LightController {
    private Controller controller;
    private boolean lightStatus = false;

    public LightController(Controller controller){
        this.controller = controller;
    }

    /**
     * Method to turn on the light
     */
    public void turnOnLight() throws IOException {
        lightStatus = !lightStatus;
        controller.sendLightMessage();
    }

    /**
     * Method to turn off the light
     */
    public void turnOffLight() throws IOException {
        lightStatus = !lightStatus;
        controller.sendLightMessage();
    }

    public boolean getStatus(){
        return this.lightStatus;
    }
}
