package station;

import main.ChargingSettings;

/**
 * Created by Thesis on 26/4/2018.
 * Maybe a subclass of ChargingSettings?
 */
public abstract class Offer {

    private boolean isSuggestion; // if the offer is not in the initial preferences of the EV
    ChargingSettings settings; // the charging setting of the offer
    private int price; // how much the EV must pay
    private double rating; // how good it is, how much it covers of the initial needs

    public Offer(ChargingSettings settings, int price) {
        isSuggestion = false;
        this.settings = settings;
        this.price = price;
        computeRating();
    }

    protected abstract void computeRating ();

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

    public int getOfferArrival () {
        return settings.getArrival();
    }

    public int getOfferDeparture () {
        return settings.getDeparture();
    }

    public int getOfferEnergy () {
        return settings.getEnergy();
    }

    public String toString () {
        String str = "";
        str = str + "Offer: " + getOfferArrival() + "-" + getOfferDeparture() + "/" + getOfferEnergy();
        if (!isSuggestion)
            return str;
        else
            return str + " - " + rating;
     }

    public boolean isSuggestion() {
        return isSuggestion;
    }

    public void setIsSuggestion(boolean suggestion) {
        isSuggestion = suggestion;
    }

    public int getPrice() {
        return price;
    }



}
