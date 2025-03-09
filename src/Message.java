public class Message {
    final int id;

    public Message(int id) {
        this.id = id;
    }
}


/**
 * Messages TO FXdeviceSimulator
 *  1: Toggle power
 *  2: Toggle light
 *  3: Turn on top heater
 *  4: Turn on bottom heater
 *  5: Turn off heaters
 *  5: getCavityTemp
 *  7: getTimeLeftOnTimer
 *  6: getTempInput
 *  9: getTimeInput
 * 10: Toggledoor
 * 11: Kill heaters
 * 12: focusTime
 * 13: focusTemp
 * 14: ClearDisplay
 *
 * Messages FROM FXdeviceSimulator
 * 15: Time focus pressed
 * 16: Temp focus pressed
 * 17: Bake pressed
 * 18: Broil pressed
 * 19: Roast pressed
 * 20: Pizza pressed
 * 21: Nuggets pressed
 * 22: Light pressed
 * 23: Power pressed
 * 24: Increment pressed
 * 25: Decrement pressed
 * 26: Start pressed
 * 27: 
 */