package messaging.message_types;

import main.ChargingSettings;

/**
 * Created by Thesis on 10/5/2018.
 */
public class RequestMessage extends ChargingSettingsMessage {

    private int x, y; // location of the EV on the map

    public RequestMessage(String senderType, int senderID, ChargingSettings settings, int x, int y) {
        super(senderType, senderID, settings);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString () {
        return super.toString() + ", location: <" + x + ", " + y + ">";
    }
}
