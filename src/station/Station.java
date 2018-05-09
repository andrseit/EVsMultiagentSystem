package station;

import messaging.message_types.OfferMessage;
import station.negotiation.SuggestionsComputer;
import station.optimize.Scheduler;
import system.Agent;
import main.ChargingSettings;
import messaging.Mailbox;
import messaging.Message;
import messaging.MessageList;
import messaging.message_types.ChargingSettingsMessage;
import messaging.message_types.StringMessage;
import various.ArrayTransformations;

import java.util.ArrayList;

/**
 * Created by Thesis on 26/4/2018.
 */
public class Station extends Agent {

    private StationData data;
    private Schedule schedule;
    private SuggestionsComputer suggestionsComputer;
    private ArrayList<EVObject> evBidders;

    public Station(String type, int globalID, Mailbox receiversMailbox, MessageList incomingMessages,
                   StationData data, int slotsNumber) {
        super(type, globalID, receiversMailbox, incomingMessages);
        this.data = data;
        evBidders = new ArrayList<>();
        schedule = new Schedule(slotsNumber, data.getChargersNumber(), data.getScheduler());
        suggestionsComputer = new SuggestionsComputer();
    }

    public void createMessage () {
        Message message = new StringMessage(getType(), getGlobalID(), "Not Available");
        sendMessage(0, message);
    }

    public void receiveRequests () {
        System.out.println("Station_" + getGlobalID()+ " is receiving requests:");
        Message message;
        while ((message = getMessenger().nextMessage()) != null) {
            if (message instanceof ChargingSettingsMessage)
                manageChargingSettingsMessage((ChargingSettingsMessage) message);
             else
                System.err.println("Wrong message type!");
        }
    }

    public void sendOfferMessages () {
        // create offer message where an offer exists
        for (int e = 0; e < evBidders.size(); e++) {
            EVObject ev = evBidders.get(e);
            int globalID = ev.getGlobalID();
            int listID = ev.getCplexID();
            if (ev.hasOffer()) {
                Offer offer = ev.getOffer();
                int arrival = offer.getOfferArrival();
                int departure = offer.getOfferDeparture();
                int energy = offer.getOfferEnergy();
                int price = offer.getPrice();
                ChargingSettings settings = new ChargingSettings(arrival, departure, energy);
                OfferMessage message = new OfferMessage("Station", getGlobalID(), settings, price);
                getMessenger().sendMessage(globalID, message);
            } else {
                StringMessage message = new StringMessage("Station", getGlobalID(), "Currently Not Available");
                getMessenger().sendMessage(globalID, message);
            }
        }
        // create unavailability message
    }

    protected void checkStringMessage(StringMessage message) {
        String text = message.getText();
        int sender = message.getSenderID();
        EVObject ev = locateEV(sender);
        // if the EV accepted then add it to the final schedule
        //System.out.println(evBiddersString());
        //System.out.println("Sender: " + sender);
        if (text.equals("ACCEPT")) {
            System.out.println("EV_" + sender + " accepted");
            schedule.addToSchedule(ev.getLocalID());
            removeBidder(ev);
        }
        // if the EV is waiting leave it in the evBidders list to continue with the negotiations
        else if (text.equals("WAITING")) {
            System.out.println("EV_" + sender + " will wait for new offer");
        }
        // if the EV rejected the offer then remove it from evBidders
        else if (text.equals("REJECT")) {
            System.out.println("EV_" + sender + " rejected the offer");
            removeBidder(ev);
        } else {
            System.out.println("EV_" + sender + " sent wrong Message");
        }
    }

    protected void manageChargingSettingsMessage(ChargingSettingsMessage message) {
        int sender = message.getSenderID();
        ChargingSettings settings = message.getSettings();
        int arrival = settings.getArrival();
        int departure = settings.getDeparture();
        int energy = settings.getEnergy();

        //System.out.println("Request received from ev_ " + sender + ": " + arrival + "-" + departure + "/" + energy);

        int listID = evBidders.size() - 1;
        EVObject ev = new EVObject(sender, listID, new ChargingSettings(arrival, departure, energy));
        ev.setLocalID(evBidders.size());
        evBidders.add(ev);
    }

    public String evBiddersString () {
        String str = "";
        for (EVObject ev: evBidders) {
            str = str + ev.toString() + "\n";
        }
        return str;
    }

    public void computeSchedule () {
        Scheduler scheduler = data.getScheduler();
        scheduler.compute(evBidders, schedule.getRemainingChargers(), schedule.getPrice());
        schedule.setCPSchedule(scheduler.getSchedule());
        schedule.setWhoCharged(scheduler.getWhoCharged());
        setOffers();
        ArrayTransformations.printTwoDimensionalArray(scheduler.getSchedule());
    }

    public void computeSuggestions () {
        updateLocalIDs();
        suggestionsComputer.compute(evBidders, schedule.getRemainingChargers(), schedule.getPrice());
        schedule.setCPSchedule(suggestionsComputer.getSchedule());
        schedule.setWhoCharged(suggestionsComputer.getWhoCharged());
        setOffers();
        System.out.println("CP Schedule");
        ArrayTransformations.printTwoDimensionalArray(schedule.getCpSchedule());
    }

    // updates the local IDs for the EVs, because some of the EVs were removed from the list
    private void updateLocalIDs () {
        for (int e = 0; e < evBidders.size(); e++) {
            EVObject ev = evBidders.get(e);
            ev.setLocalID(e);
        }
    }

    private void setOffers () {
        int[][] scheduleMap = schedule.getCpSchedule();
        int[] whoCharged = schedule.getWhoCharged();
        int[] priceList = schedule.getPrice();
        int slotsNumber = schedule.getSlotsNumber();
        for (int e = 0; e < evBidders.size(); e++) {
            EVObject current = evBidders.get(e);
            if (whoCharged[e] == 1) {
                int arrival = -1, departure = -1, energy = 0, price = 0;
                for (int s = 0; s < slotsNumber; s++) {
                    if (scheduleMap[e][s] == 1) {
                        if (arrival == -1)
                            arrival = s;
                        departure = s;
                        energy++;
                        price += priceList[s];
                    }
                }
                // if it is a suggestion - state it
                if (arrival < current.getArrival() || departure > current.getDeparture() || energy < current.getEnergy()) {
                    Offer offer = new SimpleOffer(new ChargingSettings(arrival, departure, energy), price);
                    offer.setIsSuggestion(true);
                    current.setOffer(offer);
                } else {
                    current.setOffer(new SimpleOffer(new ChargingSettings(arrival, departure, energy), price));
                }
            }
        }
    }

    // returns the EV with the given ID from the evBidders list
    private EVObject locateEV (int id) {
        for (EVObject ev: evBidders) {
            if (ev.getGlobalID() == id)
                return ev;
        }
        return null;
    }

    private void removeBidder (EVObject ev) {
        evBidders.remove(ev);
        updateLocalIDs();
    }

    public boolean isFinished () {
        return evBidders.isEmpty();
    }

    public String toString () {
        return "Station_" + getGlobalID() + " <" + data.getX() + ", " + data.getY() + "> : " +
                "chargers: " + data.getChargersNumber();
    }
}
