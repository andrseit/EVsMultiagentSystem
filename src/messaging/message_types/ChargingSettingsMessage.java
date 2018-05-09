package messaging.message_types;

import messaging.Message;
import main.ChargingSettings;

/**
 * Created by Thesis on 26/4/2018.
 */
public class ChargingSettingsMessage extends Message {

    private ChargingSettings settings;

    public ChargingSettingsMessage(String senderType, int senderID, ChargingSettings settings) {
        super(senderType, senderID);
        this.settings = new ChargingSettings(settings.getArrival(), settings.getDeparture(), settings.getEnergy());
    }

    public String toString () {
        return getSenderType() + "_" + getSenderID() + "-> Settings message: " + "arrival: " + settings.getArrival() +
                ", departure: " + settings.getDeparture() +
                ", energy: " + settings.getEnergy();
    }

    public ChargingSettings getSettings() {
        return settings;
    }
}
