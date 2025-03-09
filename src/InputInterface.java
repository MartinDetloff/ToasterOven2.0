import java.io.IOException;

public class InputInterface {
    public Status status;
    private Control control;
    private Mode mode;
    private Controller controller;

    public InputInterface(Controller controller){
        this.status = new Status(0, 0);
        this.control = new Control();
        this.mode = new Mode("None", "None");
        this.controller = controller;
    }

    /**
     * getter for the current status
     * @return status
     */
    public Status getStatus(){
        return status;
    }

    /**
     * Getter for the current control
     * @return control
     */
    public Control getControl(){
        return control;
    }

    /**
     * Getter for the current mode
     * @return mode
     */
    public Mode getMode(){
        return mode;
    }

    public void togglePower() throws IOException {
        control.togglePower();
        controller.togglePower();
        boolean newPowerStatus = !control.getPowerButton();
    }


}
