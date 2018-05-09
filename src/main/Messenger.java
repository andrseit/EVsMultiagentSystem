package main;

import messaging.Mailbox;
import messaging.Message;
import messaging.MessageList;

/**
 * Created by Thesis on 26/4/2018.
 */
public class Messenger {

    private int senderID;
    private String senderType;
    private Mailbox receiversMailbox;
    private MessageList incomingMessages;

    public Messenger(String senderType, int senderID, Mailbox receiversMailbox, MessageList incomingMessages) {
        this.senderType = senderType;
        this.senderID = senderID;
        this.receiversMailbox = receiversMailbox;
        this.incomingMessages = incomingMessages;
    }

    public void sendMessage (int receiverID, Message message) {
        receiversMailbox.addMessage(receiverID, message);
    }

    public Message nextMessage() {
        if (!incomingMessages.isEmpty()) {
            return incomingMessages.getNextMessage();
        }
        return null;
    }
}
