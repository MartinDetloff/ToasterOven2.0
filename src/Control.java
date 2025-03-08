public class Control {

    /**
     * Method to set button pressed to start/stop/clear
     * @param button (start/stop/clear)
     */
    public void setButtonPressed(String button){
        switch (button){
            case "start":
                System.out.println("Start button");
                break;

            case "stop":
                System.out.println("stop button");
                break;

            case "clear":
                System.out.println("clear button");
                break;
        }
    }

    /**
     * Method to handle a start button click
     */
    public void start(){
        setButtonPressed("stop");
    }

    /**
     * Method to handle a stop button click
     */
    public void stop(){
        setButtonPressed("stop");
    }

    /**
     * Method to handle a clear button click
     */
    public void clear(){
        setButtonPressed("clear");
    }
}

