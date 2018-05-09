package messaging.message_types;

import messaging.Message;

/**
 * Created by Thesis on 26/4/2018.
 */
public class StringMessage extends Message {

    private String message;

    public StringMessage(String senderType, int senderID, String message) {
        super(senderType, senderID);
        this.message = message;
    }

    public String getText () {
        return message;
    }

    public String toString () {
        return getSenderType() + "_" + getSenderID() + "-> String message: " + message;
    }
}
