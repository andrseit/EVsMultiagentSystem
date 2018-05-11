package station;

import messaging.message_types.OfferMessage;
import messaging.message_types.RequestMessage;
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
    private ArrayList<EVObject> notCharged;
    private int currentSlot;
    private boolean suggestions;

    private boolean distance; // if distance matters

    public Station(String type, int globalID, Mailbox receiversMailbox, MessageList incomingMessages,
                   StationData data, int slotsNumber) {
        super(type, globalID, receiversMailbox, incomingMessages);
        this.data = data;
        evBidders = new ArrayList<>();
        notCharged = new ArrayList<>();
        schedule = new Schedule(slotsNumber, data.getChargersNumber(), data.getScheduler());
        suggestionsComputer = new SuggestionsComputer();
        suggestions = false;
        currentSlot = 0;
    }

    public void createMessage () {
        Message message = new StringMessage(getType(), getGlobalID(), "Not Available");
        sendMessage(0, message);
    }

    public void receiveRequests () {
        System.out.println("Station_" + getGlobalID()+ " is receiving requests:");
        Message message;
        while ((message = getMessenger().nextMessage()) != null) {
            if (message instanceof RequestMessage)
                manageChargingSettingsMessage((RequestMessage) message);
             else {
                System.err.println("Wrong message type!");
                System.exit(1);
            }
        }
    }

    public void sendOfferMessages () {
        // create offer message where an offer exists
        //System.out.println(evBiddersString());
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
            System.out.println("EV_" + sender + " accepted " + ev.getLocalID());
            schedule.addToSchedule(ev.getLocalID());
            evBidders.remove(ev);
        }
        // if the EV is waiting leave it in the evBidders list to continue with the negotiations
        else if (text.equals("WAITING")) {
            System.out.println("EV_" + sender + " will wait for new offer");
        }
        // if the EV rejected the offer then remove it from evBidders
        else if (text.equals("REJECT")) {
            System.out.println("EV_" + sender + " rejected the offer");
            evBidders.remove(ev);
        } else {
            System.err.println("EV_" + sender + " sent wrong Message");
            System.err.println("Message was: " + message.getText());
            System.exit(1);
        }
        if (evBidders.isEmpty())
            suggestions = false;
    }

    protected void manageChargingSettingsMessage(ChargingSettingsMessage message) {
        RequestMessage m = (RequestMessage) message;
        int sender = m.getSenderID();
        ChargingSettings settings = m.getSettings();
        int arrival = settings.getArrival();
        int departure = settings.getDeparture();
        int energy = settings.getEnergy();

        //System.out.println("Request received from ev_ " + sender + ": " + arrival + "-" + departure + "/" + energy);

        int listID = evBidders.size() - 1;
        EVObject ev = new EVObject(sender, listID, new ChargingSettings(arrival, departure, energy));
        ev.setLocalID(evBidders.size());
        if (distance)
            ev.setXY(m.getX(), m.getY(), data.getX(), data.getY());
        evBidders.add(ev);
    }

    public String evBiddersString (ArrayList<EVObject> evs) {
        String str = "";
        for (EVObject ev: evs) {
            str = str + ev.toString() + "\n";
        }
        return str;
    }

    public void computeOffers () {
        notCharged.clear();
        computeSchedule();
        if (suggestions) {
            schedule.updateTemporaryChargers();
            findNotCharged();
            System.out.println(notCharged);
            if (!notCharged.isEmpty()) {
                computeSuggestions();
            }
        } else {
            suggestions = true;
        }
        setOffers();
    }

    public void computeSchedule () {
        updateLocalIDs(evBidders);
        System.out.println("Bidders before optimal: ");
        System.out.println(evBiddersString(evBidders));

        System.out.println("Available Chargers: ");
        ArrayTransformations.printOneDimensionalArray(schedule.getRemainingChargers());

        System.out.println("---");
        Scheduler scheduler = data.getScheduler();
        scheduler.setCurrentSlot(currentSlot);
        scheduler.compute(evBidders, schedule.getRemainingChargers(), schedule.getPrice());
        schedule.setCPSchedule(scheduler.getSchedule());
        schedule.setWhoCharged(scheduler.getWhoCharged());
        System.out.println("Optimal Schedule: ");
        ArrayTransformations.printTwoDimensionalArray(scheduler.getSchedule());
    }

    public void computeSuggestions () {
        updateLocalIDs(notCharged);
        System.out.println("Bidders before suggestions: ");
        System.out.println(evBiddersString(notCharged));

        System.out.println("Available Chargers: ");
        ArrayTransformations.printOneDimensionalArray(schedule.getTempRemainingChargers());

        System.out.println("---");
        suggestionsComputer.setCurrentSlot(currentSlot);
        suggestionsComputer.compute(notCharged, schedule.getTempRemainingChargers(), schedule.getPrice());
        schedule.setCPSchedule(ArrayTransformations.concatMaps(schedule.getCpSchedule(), suggestionsComputer.getSchedule()));
        schedule.setWhoCharged(ArrayTransformations.concatOneDimensionMaps(schedule.getWhoCharged(), suggestionsComputer.getWhoCharged()));
        // add back to evBidders
        evBidders.addAll(notCharged);
        System.out.println("Suggestions: ");
        ArrayTransformations.printTwoDimensionalArray(suggestionsComputer.getSchedule());
        updateLocalIDs(evBidders);
    }

    // updates the local IDs for the EVs, because some of the EVs were removed from the list
    private void updateLocalIDs (ArrayList<EVObject> evs) {
        for (int e = 0; e < evs.size(); e++) {
            EVObject ev = evs.get(e);
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

    // finds not charged evs by optimal
    // removes them from initially computed schedule
    // adds them t
    private void findNotCharged () {
        // remove from temporary schedule
        int[] whoCharged = schedule.getWhoCharged();
        for (int e = 0; e < evBidders.size(); e++) {
            EVObject ev = evBidders.get(e);
            if (whoCharged[e] == 0) {
                System.out.println("IM IN");
                notCharged.add(ev);
            }
        }

        // remove temporary
        for (EVObject ev: notCharged) {
            evBidders.remove(ev);
        }

        // remove from maps also
        schedule.setCPSchedule(ArrayTransformations.removeZeroRows(schedule.getCpSchedule(), schedule.getWhoCharged(), schedule.getSlotsNumber()));
        schedule.setWhoCharged(ArrayTransformations.removeZeroRows(schedule.getWhoCharged()));

    }

    // returns the EV with the given ID from the evBidders list
    private EVObject locateEV (int id) {
        for (EVObject ev: evBidders) {
            if (ev.getGlobalID() == id)
                return ev;
        }
        return null;
    }

    public boolean isFinished () {
        return evBidders.isEmpty();
    }

    public void setCurrentSlot(int currentSlot) {
        this.currentSlot = currentSlot;
    }

    public void setDistance(boolean distance) {
        this.distance = distance;
    }

    public String toString () {
        return "Station_" + getGlobalID() + " <" + data.getX() + ", " + data.getY() + "> : " +
                "chargers: " + data.getChargersNumber();
    }
}
