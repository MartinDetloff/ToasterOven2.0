public class Controller {
    private InputInterface inputInterface;
    private Timer timer;
    private LightController lightController;
    private DoorMonitor doorMonitor;
    private Cooking cooking;

    public Controller(){
        inputInterface = new InputInterface();
        timer = new Timer();
        lightController = new LightController();
        doorMonitor = new DoorMonitor();
        cooking = new Cooking();
    }
}