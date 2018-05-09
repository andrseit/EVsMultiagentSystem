package messaging;

/**
 * Created by Thesis on 26/4/2018.
 */
public class Mailbox {

    private MessageList[] mailbox;

    public Mailbox(int mailboxSize) {
        mailbox = new MessageList[mailboxSize];
        for (int i = 0; i < mailboxSize; i++) {
            mailbox[i] = new MessageList();
        }
    }

    public void addMessage (int receiversID, Message message) {
        mailbox[receiversID].addMessage(message);
    }

    public int getMailboxSise () { return mailbox.length; }

    public MessageList getMessageList (int id) {
        return mailbox[id];
    }

}
