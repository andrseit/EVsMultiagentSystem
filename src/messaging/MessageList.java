package messaging;

import java.util.ArrayList;

/**
 * Created by Thesis on 26/4/2018.
 */
public class MessageList {

    private ArrayList<Message> messageList;

    public MessageList() {
        messageList = new ArrayList<>();
    }

    public void addMessage (Message message) {
        messageList.add(message);
    }

    public Message getNextMessage () {
        Message message = messageList.get(0);
        messageList.remove(0);
        return message;
    }

    public boolean isEmpty () {
        return messageList.isEmpty();
    }
}
