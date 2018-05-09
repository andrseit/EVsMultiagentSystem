package evs;

import main.ChargingSettings;

/**
 * Created by Thesis on 27/4/2018.
 */
public class EVPreferences {

    private ChargingSettings settings;
    private int informTimePoint;
    private int locationX, locationY;
    private int destinationX, destinationY;
    private int maxDistance;
    private EVStrategySettings strategySettings;

    public EVPreferences(ChargingSettings settings, int informTimePoint,
                         int locationX, int locationY, int destinationX, int destinationY,
                         int maxDistance, EVStrategySettings strategySettings) {
        this.settings = settings;
        this.informTimePoint = informTimePoint;
        this.locationX = locationX;
        this.locationY = locationY;
        this.destinationX = destinationX;
        this.destinationY = destinationY;
        this.maxDistance = maxDistance;
        this.strategySettings = strategySettings;
    }

    public ChargingSettings getSettings() {
        return settings;
    }

    public int getInformTimePoint() {
        return informTimePoint;
    }

    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public int getDestinationX() {
        return destinationX;
    }

    public int getDestinationY() {
        return destinationY;
    }

    public int getArrival () { return settings.getArrival(); }

    public int getDeparture () { return settings.getDeparture(); }

    public int getEnergy () { return settings.getEnergy(); }

    public int getMaxDistance() {
        return maxDistance;
    }

    public EVStrategySettings getStrategySettings() {
        return strategySettings;
    }
}
