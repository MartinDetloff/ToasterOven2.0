public class InputInterface {
    private Status status;
    private Control control;
    private Mode mode;

    public InputInterface(){
        this.status = new Status(0, 0);
        this.control = new Control();
        this.mode = new Mode("None", "None");
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


}
