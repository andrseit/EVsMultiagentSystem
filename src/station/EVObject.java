package station;

import main.ChargingSettings;
import various.SimpleMath;

/**
 * Created by Thesis on 29/4/2018.
 * How the station represents an EV
 */
public class EVObject {

    // global = system, clpex = row in temporary cplex schedule, local = row in
    // temporary id may not be needed
    private int globalID, cplexID, localID;
    private ChargingSettings settings;
    private Offer offer;
    private int x, y, distance; // location on map, distance from station
    private boolean informed; // informed about later scheduling
    private boolean suggestion; // if the EV accepted a suggestion

    public EVObject(int globalID, int clpexID, ChargingSettings settings) {
        this.globalID = globalID;
        this.cplexID = clpexID;
        this.settings = settings;
    }

    public ChargingSettings getSettings() {
        return settings;
    }

    public int getArrival () { return settings.getArrival(); }

    public int getDeparture () { return settings.getDeparture(); }

    public int getEnergy () { return settings.getEnergy(); }

    public int getCplexID() {
        return cplexID;
    }

    public int getLocalID() {
        return localID;
    }

    public int getGlobalID() {
        return globalID;
    }

    public void setCplexID(int cplexID) {
        this.cplexID = cplexID;
    }

    public void setLocalID(int localID) {
        this.localID = localID;
    }

    public void setOffer(Offer offer) {
        // if it is in its initial needs but out of bounds
        if (!offer.isSuggestion()) {
            if (offer.getOfferArrival() < settings.getArrival())
                System.err.println("Wrong arrival");
            if (offer.getOfferDeparture() > settings.getDeparture())
                System.err.println("Wrong departure");
            if (offer.getOfferEnergy() != settings.getEnergy())
                System.err.println("Wrong energy");
        }
        //if (isDifferent(offer)) {
            this.offer = offer;
            //hasOffer = true;
        //} //else {
            //hasOffer = false;
        //}
    }

    // na min ypologizetai kathe fora to oldDistance
    private boolean isDifferent(Offer newOffer) {
        if (offer == null)
            return true;
        int newArrival = newOffer.getOfferArrival();
        int newDeparture = newOffer.getOfferDeparture();
        int newEnergy = newOffer.getOfferEnergy();

        int oldArrival = this.offer.getOfferArrival();
        int oldDeparture = this.offer.getOfferDeparture();
        int oldEnergy = this.offer.getOfferEnergy();

        return newArrival != oldArrival || newDeparture != oldDeparture || newEnergy != oldEnergy;
    }

    public Offer getOffer() {
        return offer;
    }

    public boolean hasOffer () {
        //return hasOffer;
        return offer != null;
    }


    public void setXY (int x, int y, int sx, int sy) {
        this.x = x;
        this.y = y;
        distance = SimpleMath.manhattanDistance(x, y, sx, sy);
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public String toString () {
        return "EV_" + globalID + ": " + settings.getArrival() + "-" + settings.getDeparture() +
                "/" + settings.getEnergy() + " at < " + x + ", " + y + "> and distance = " + distance +
                " (local id = " + localID + ")";
    }

    public boolean isInformed() {
        return informed;
    }

    public void setInformed(boolean informed) {
        this.informed = informed;
    }

    public void setSuggestion () {
        int arrival = settings.getArrival();
        int departure = settings.getDeparture();
        int energy = settings.getEnergy();

        int offerArrival = offer.getOfferArrival();
        int offerDeparture = offer.getOfferDeparture();
        int offerEnergy = offer.getOfferEnergy();

        if (!(offerArrival >= arrival && offerDeparture <= departure && energy == offerEnergy))
            suggestion = true;
    }

    public boolean isSuggestion() {
        return suggestion;
    }
}
