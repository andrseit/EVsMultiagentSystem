package stats;

import various.SimpleMath;

/**
 * Created by Thesis on 1/6/2018.
 */
public class StationStats {

    private int requestsNumber;
    private int chargedEVsNumber;
    private int acceptedSuggestionsNumber;
    private double totalUtility;

    private int chargersUsed;
    private int allChargers;


    private double chargedEVsPercentage;
    private double chargersUsedPercentage;

    private double schedulingTime;
    private double suggestionsTime;

    public void setSchedulingTime(double schedulingTime) {
        this.schedulingTime = schedulingTime;
    }

    public void setSuggestionsTime(double suggestionsTime) {
        this.suggestionsTime = suggestionsTime;
    }

    public void setRequestsNumber(int requestsNumber) {
        this.requestsNumber = requestsNumber;
    }

    public void setChargedEVsNumber(int chargedEVsNumber) {
        this.chargedEVsNumber = chargedEVsNumber;
    }

    public void setAcceptedSuggestionsNumber(int acceptedSuggestionsNumber) {
        this.acceptedSuggestionsNumber = acceptedSuggestionsNumber;
    }

    public void setChargersUsed(int chargersUsed) {
        this.chargersUsed = chargersUsed;
    }

    public void setAllChargers(int allChargers) {
        this.allChargers = allChargers;
    }

    public void setTotalUtility(double totalUtility) {
        this.totalUtility = totalUtility;
    }

    public void computePercentages () {
        chargedEVsPercentage = SimpleMath.round(((double) chargedEVsNumber / (double) requestsNumber)*100, 2);
        chargersUsedPercentage = SimpleMath.round(((double) chargersUsed / (double) allChargers)*100, 2);
    }

    @Override
    public String toString() {
        return "Requests: " + requestsNumber + ", charged: " + chargedEVsNumber + " (" + chargedEVsPercentage + "%)" +
                "\nSuggestions: " + acceptedSuggestionsNumber +
                "\nAll chargers: " + allChargers + ", chargers used: " + chargersUsed + " (" + chargersUsedPercentage + "%)" +
                "\nTotal utility: " + totalUtility +
                "\nScheduling Time: " + schedulingTime + ", Suggestions Time: " + suggestionsTime;
    }
}
