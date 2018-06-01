package system;

import main.Messenger;
import messaging.Mailbox;
import messaging.Message;
import messaging.MessageList;
import messaging.message_types.ChargingSettingsMessage;
import messaging.message_types.StringMessage;

/**
 * Created by Thesis on 26/4/2018.
 */
public abstract class Agent {

    private String type;
    private int globalID;
    private Messenger messenger;

    public Agent(String type, int globalID, Mailbox receiversMailbox, MessageList incomingMessages) {
        this.type = type;
        this.globalID = globalID;
        messenger = new Messenger(type, globalID, receiversMailbox, incomingMessages);
    }

    public void readMessages () {
        Message message;
        System.out.println(getType() + "_" + globalID + " receives message: ");
        while ((message = getMessenger().nextMessage()) != null) {
            System.out.println("MESSAGE: " + message);
            if (message instanceof StringMessage)
                checkStringMessage((StringMessage) message);
            else if (message instanceof ChargingSettingsMessage) {
                manageChargingSettingsMessage((ChargingSettingsMessage) message);
            }
        }
    }

    protected abstract void checkStringMessage (StringMessage message);

    protected abstract void manageChargingSettingsMessage(ChargingSettingsMessage message);

    public void sendMessage (int receiverID, Message message) {
        messenger.sendMessage(receiverID, message);
    }

    public abstract void createMessage();

    public String getType() {
        return type;
    }

    public int getGlobalID() {
        return globalID;
    }

    public Messenger getMessenger () { return messenger; }
}
