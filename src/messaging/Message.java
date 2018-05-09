package messaging;



/**
 * Created by Thesis on 26/4/2018.
 */
public class Message {

    private String senderType;
    private int senderID;

    public Message(String senderType, int senderID) {
        this.senderType = senderType;
        this.senderID = senderID;
    }

    public int getSenderID () {
        return senderID;
    }

    public String getSenderType() {
        return senderType;
    }
}
