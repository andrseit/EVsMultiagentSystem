package stats;

import station.EVObject;
import station.Station;
import sun.java2d.pipe.SpanShapeRenderer;
import various.ArrayTransformations;
import various.SimpleMath;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Thesis on 1/6/2018.
 */
public class Statistics {

    private ArrayList<Station> stations;
    // to keep the statistics of each station
    // key: globalID, value: Statistics
    private HashMap<Integer, StationStats> stationStats;
    private int slotsNumber;

    public Statistics(ArrayList<Station> stations, int slotsNumber) {
        this.stations = stations;
        stationStats = new HashMap<>();
        this.slotsNumber = slotsNumber;
    }

    public void computeStatisticsPerStation () {
        for (Station station: stations) {
            ArrayList<EVObject> chargedEVs = station.getChargedEvs();
            int[][] schedule = station.getFinalSchedule();
            StationStats stats = new StationStats();

            stats.setRequestsNumber(station.getRequests());
            stats.setChargedEVsNumber(schedule.length);

            int acceptedSuggestions = 0;
            double utility = 0;
            for (EVObject ev: chargedEVs) {
                if (ev.isSuggestion())
                    acceptedSuggestions++;
                utility += SimpleMath.computeUtility(ev.getArrival(), ev.getDeparture(), ev.getEnergy(),
                        ev.getOffer().getOfferArrival(), ev.getOffer().getOfferDeparture(), ev.getOffer().getOfferEnergy(),
                        slotsNumber);
            }
            stats.setAcceptedSuggestionsNumber(acceptedSuggestions);
            stats.setChargersUsed(ArrayTransformations.cellsSum(schedule));
            stats.setAllChargers(station.getChargersSlots());
            stats.setTotalUtility(utility);
            stats.setSchedulingTime(station.getSchedulingTime());
            stats.setSuggestionsTime(station.getSuggestionsTime());
            stats.computePercentages();
            System.out.println(stats.toString());
        }
    }
}
