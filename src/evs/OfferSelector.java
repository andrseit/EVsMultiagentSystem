package evs;

import main.ChargingSettings;
import station.Offer;
import various.SimpleMath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Thesis on 2/5/2018.
 * This class is used to evaluate the incoming offers
 * and set answers to send at each station
 */
public class OfferSelector {

    // replace with priority queue
    private ArrayList<IncomingOffer> offers;
    private HashMap<Integer, String> answers; // Integer = station id, String = answer

    private ChargingSettings initialSettings;
    private EVStrategySettings strategySettings;
    private int roundsCount; // in what round the conversation is
    private boolean serviced; // if the EV is over with the conversation, not sure if this is the right class
                                // for this variable, but it was comfy

    public OfferSelector(ChargingSettings initialSettings, EVStrategySettings strategySettings) {
        offers = new ArrayList<>();
        answers = new HashMap<>();
        this.initialSettings = initialSettings;
        this.strategySettings = strategySettings;
        roundsCount = 0;
        serviced = false;
    }

    public void addOffer (IncomingOffer offer) {
        offers.add(offer);
        answers.put(offer.getStationID(), "");
    }

    public void addCurrentlyNotAvailableOffer (Integer stationID) {
        answers.put(stationID, "WAITING");
    }

    public void produceAnswers () {
        int maxRound = strategySettings.getRounds();
        System.out.println("It is round: " + roundsCount + " and my bound is: " + maxRound);
        if (!offers.isEmpty()) {
            evaluateAllOffers();
            System.out.println(offers.get(0).getRating());
            // if end of the conversation is reached, a final decision must be made
            if (roundsCount == maxRound) {
                IncomingOffer offer = offers.get(0);
                if (offer.getRating() != Integer.MAX_VALUE) {
                    int accept = offer.getStationID();
                    answers.put(accept, "ACCEPT");
                    rejectRemainingStations(accept);
                } else {
                    rejectRemainingStations(-1);
                }
                serviced = true;
            } else if (roundsCount < maxRound) {
                IncomingOffer offer = offers.get(0);
                if (offer.getRating() == 0.0) {
                    int accept = offer.getStationID();
                    answers.put(accept, "ACCEPT");
                    rejectRemainingStations(accept);
                    serviced = true;
                } else {
                    resetAnswers();
                }
            }
        } else {
            System.out.println("My list is empty");
            if (roundsCount == maxRound) {
                rejectRemainingStations(-1);
                System.out.println("Is rounds count");
                serviced = true;
            }
            else
                resetAnswers();
        }
        roundsCount++;
        System.out.println(answersToString());
    }

    public void evaluateAllOffers () {

        if (!offers.isEmpty()) {
            for (IncomingOffer offer : offers) {
                evaluateSingleOffer(offer);
            }
            sortOffers();
            randomizeSameOffers();
            /*
            // ektos evaluate - tha pane sto produce answers
            int accept = offers.get(0).getStationID();
            answers.put(accept, "ACCEPT");
            rejectRemainingStations(accept);
            */
        }
    }

    // @stationID: station the EV accepted its offer
    private void rejectRemainingStations (Integer stationID) {
        for (Integer id: answers.keySet()) {
            if (id != stationID)
                answers.put(id, "REJECT");
        }
    }

    // ABSTRACT - currently how far it is from their initial needs
    protected void evaluateSingleOffer (IncomingOffer offer) {
        int offerArrival = offer.getArrival();
        int offerDeparture = offer.getDeparture();
        int offerEnergy = offer.getEnergy();

        int initialArrival = initialSettings.getArrival();
        int initialDeparture = initialSettings.getDeparture();
        int initialEnergy = initialSettings.getEnergy();

        int distance = 0;
        if (!isInitial(offer))
            distance = SimpleMath.manhattanDistance(offerArrival, offerDeparture, offerEnergy,
                initialArrival, initialDeparture, initialEnergy);

        if (!isWithinStrategy(offer))
            offer.setRating(Integer.MAX_VALUE);
        else
            offer.setRating(distance);
    }

    private void sortOffers () {
        Collections.sort(offers, new Comparator<IncomingOffer>() {
            @Override
            public int compare(IncomingOffer o1, IncomingOffer o2) {
                if (o1.getRating() - o2.getRating() > 0)
                    return 1;
                else if (o1.getRating() - o2.getRating() == 0)
                    return 0;
                return -1;
            }
        });
    }

    private void randomizeSameOffers () {
        ArrayList<IncomingOffer> temp = new ArrayList<>();
        double minRating = offers.get(0).getRating();
        for (IncomingOffer offer: offers) {
            if (offer.getRating() == minRating) {
                temp.add(offer);
            } else
                break;
        }
        Collections.shuffle(temp);
        for (IncomingOffer o: temp) {
            offers.remove(o);
            offers.add(0, o);
        }
    }

    // checks if an offer is in initial preferences
    private boolean isInitial (IncomingOffer offer) {
        if (offer.getArrival() >= initialSettings.getArrival() && offer.getDeparture() <= initialSettings.getDeparture() &&
                offer.getEnergy() == initialSettings.getEnergy())
            return true;
        return false;
    }

    // checks if an offer is within strategy
    private boolean isWithinStrategy (IncomingOffer offer) {
        double newRange = offer.getDeparture() - offer.getArrival();
        double initialRange = initialSettings.getDeparture() - initialSettings.getArrival();
        double rangeDifference = newRange/initialRange;
        if (offer.getArrival() >= strategySettings.getMinArrival() && offer.getDeparture() <= strategySettings.getMaxDeparture() &&
                offer.getEnergy() >= strategySettings.getMinEnergy() && rangeDifference <= strategySettings.getMaxWindowRange())
            return true;
        return false;
    }

    public ArrayList<IncomingOffer> getOffers() {
        return offers;
    }

    public HashMap<Integer, String> getAnswers() {
        return answers;
    }

    public String answersToString () {
        String str = "";
        for (Integer id: answers.keySet()) {
            str += (id + " -> " + answers.get(id) + "\n");
        }
        return str;
    }

    public String offersToString () {
        String str = "";
        for (IncomingOffer offer: offers) {
            str += (offer.toString() + "\n");
        }
        return str;
    }

    public void resetAnswers () {
        for (Integer id: answers.keySet()) {
            answers.put(id, "WAITING");
        }
    }

    public boolean isServiced() {
        return serviced;
    }

    public void clearOffers () {
        offers.clear();
        answers.clear();
    }
}
