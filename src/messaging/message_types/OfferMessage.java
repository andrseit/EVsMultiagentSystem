package messaging.message_types;

import main.ChargingSettings;

/**
 * Created by Thesis on 2/5/2018.
 */
public class OfferMessage extends ChargingSettingsMessage {

    private int price;

    public OfferMessage(String senderType, int senderID, ChargingSettings settings, int price) {
        super(senderType, senderID, settings);
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}
