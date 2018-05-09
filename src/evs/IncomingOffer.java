package evs;

import main.ChargingSettings;

/**
 * Created by Thesis on 2/5/2018.
 * Represents an offer as seen by an EV
 */
public class IncomingOffer extends ChargingSettings {

    private int price;
    private int stationID;
    private double rating; // how good is the offer based on the EV
    private String answer;

    public IncomingOffer(int arrival, int departure, int energy,
                         int price, int stationID) {
        super(arrival, departure, energy);
        this.price = price;
        this.stationID = stationID;
        answer = "REJECT"; // default answer
        rating = 0;
    }

    public void setAnswer(String answer) {
        if (!answer.equals("ACCEPT") && !answer.equals("REJECT") && !answer.equals("WAITING"))
            System.err.println("Not acceptable answer!");
        else
            this.answer = answer;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

    public String getAnswer() {
        return answer;
    }

    public int getStationID() {
        return stationID;
    }

    public String toString () {
        return "Incoming offer by Station_" + getStationID() + ": " + getArrival() + "-" + getDeparture() + "/" + getEnergy() +
                " cost: " + price + ", rating: " + rating + " -> " + answer;
    }
}
