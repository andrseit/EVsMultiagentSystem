package evs;

import messaging.message_types.OfferMessage;
import messaging.message_types.RequestMessage;
import system.Agent;
import main.ChargingSettings;
import messaging.Mailbox;
import messaging.Message;
import messaging.MessageList;
import messaging.message_types.ChargingSettingsMessage;
import messaging.message_types.StringMessage;
import various.SimpleMath;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Thesis on 26/4/2018.
 */
public class EV extends Agent {

    private EVPreferences preferences;
    // maybe make a class like EVPDA, which contains this information
    // or maybe the messenger, too
    private int[][] stationsLocations;

    private OfferSelector offerSelector;

    public EV(String type, int globalID, Mailbox receiversMailbox, MessageList incomingMessages,
              EVPreferences preferences, int[][] stationsLocations) {
        super(type, globalID, receiversMailbox, incomingMessages);
        this.preferences = preferences;
        this.stationsLocations = stationsLocations;
        offerSelector = new OfferSelector(preferences.getSettings(), preferences.getStrategySettings());
    }

    public void createMessage () {
        Message message = new StringMessage(getType(), getGlobalID(), "ACCEPT");
        sendMessage(0, message);
    }

    public ChargingSettingsMessage createRequestMessage () {
        ChargingSettings settings = new ChargingSettings(preferences.getArrival(), preferences.getDeparture(), preferences.getEnergy());
        RequestMessage message = new RequestMessage(getType(), getGlobalID(), settings, preferences.getLocationX(), preferences.getLocationY());
        return message;
    }

    protected void checkStringMessage(StringMessage message) {
        if (isServiced()) {
            System.err.println("EV_" + getGlobalID() + " has already been serviced!");
            System.exit(1);
        }
        String text = message.getText();
        int sender = message.getSenderID();
        if (text.equals("Currently Not Available")) {
            System.out.println("\tStation has no available resources yet");
            offerSelector.addCurrentlyNotAvailableOffer(sender);
        } else if (text.equals("Not Available")) {
            System.out.println("\tStation is unable to charge me");
            // does not add the station in the offer selector as their conversation is over
            // it is unable to charge the EV
        } else if (text.equals("Later")) {
            System.out.println("\tStation_" + sender+ " will compute an offer later");
            offerSelector.addPendingOffer(sender);
        } else
            System.out.println("\tWrong Message");
    }

    protected void manageChargingSettingsMessage(ChargingSettingsMessage message) {
        if (isServiced()) {
            System.err.println("EV_" + getGlobalID() + " has already been serviced!");
            System.exit(1);
        }
        if (!(message instanceof OfferMessage)) {
            System.err.println("\tWrong type of message. Expected Offer!");
            System.exit(1);
        }
        OfferMessage offer = (OfferMessage) message;
        int sender = message.getSenderID();
        ChargingSettings settings = message.getSettings();
        int arrival = settings.getArrival();
        int departure = settings.getDeparture();
        int energy = settings.getEnergy();
        int price = offer.getPrice();

        System.out.println("\tOffer received from Station_" + sender + ": " + arrival + "-" + departure + "/" + energy + " cost: " + price);
        offerSelector.addOffer(new IncomingOffer(arrival, departure, energy, price, sender));
    }

    public String toString () {
        ChargingSettings settings = preferences.getSettings();
        EVStrategySettings strategySettings = preferences.getStrategySettings();
        return getType() + "_" + getGlobalID() + ": " + getInformSlot() + "->" +
                settings.toString() + " --- " + strategySettings.toString();
    }

    // ABSTRACT
    public ArrayList<Integer> chooseStations () {
        ArrayList<Integer> stations = new ArrayList<>(); // stations' ids

        for (int s = 0; s < stationsLocations.length; s++) {
            int stationX = stationsLocations[s][0];
            int stationY = stationsLocations[s][1];
            int evX = preferences.getLocationX();
            int evY = preferences.getLocationY();
            // distance from the station
            int distance = SimpleMath.manhattanDistance(stationX, stationY, evX, evY);
            // min distance that needs to be on time
            int minDistance = preferences.getArrival() - preferences.getInformTimePoint();
            // max distance that the EV has set as a bound
            int maxDistance = preferences.getMaxDistance();

            if (distance <= maxDistance)
                stations.add(s);
        }
        return stations;
    }

    public void evaluateOffers () {
        offerSelector.produceAnswers();
    }

    public void sendRequests () {
        ArrayList<Integer> chosenStations = chooseStations();
        ChargingSettingsMessage message = createRequestMessage();
        for (Integer s: chosenStations) {
            //System.out.println("ev_" + getGlobalID() + ": " + s);
            sendMessage(s, message);
        }
    }

    public void sendAnswers () {
        StringMessage message;
        HashMap<Integer, String> answers = offerSelector.getAnswers();
        for (Integer id: offerSelector.getAnswers().keySet()) {
            String answer = answers.get(id);
            if (!answer.equals("PENDING")) {
                message = new StringMessage("EV", getGlobalID(), answer);
                sendMessage(id, message);
            }
        }
        //offerSelector.resetAnswers();
        offerSelector.clearOffers();
    }

    public boolean isServiced() {
        //return offerSelector.getOffers().isEmpty();
        return offerSelector.isServiced();
    }

    public int getInformSlot () { return preferences.getInformTimePoint(); }

    public void resetRounds () {
        offerSelector.resetRounds();
    }

}
