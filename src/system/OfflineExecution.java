package system;

import evs.EV;
import station.Station;
import stats.Statistics;

import java.util.ArrayList;

/**
 * Created by Thesis on 10/5/2018.
 */
public class OfflineExecution extends Execution{

    public OfflineExecution(ArrayList<EV> evs, ArrayList<Station> stations, int slotsNumber) {
        super(evs, stations, slotsNumber);
    }

    @Override
    public void execute() {

        printAgents();

        System.out.println("EVs send requests...");
        for (EV ev: getEvs())
            ev.sendRequests();

        System.out.println("Stations receive requests...");
        for (int s = 0; s < getStations().size(); s++) {
            Station station = getStations().get(s);
            station.setDistance(false);
            station.receiveRequests();
            //System.out.println(station.evBiddersString());
            if (!station.isFinished()) {
                station.computeOffers();
                station.sendOfferMessages();
            } else
                getFinishedStations()[s] = 1;
        }


        while (!executionOver()) {
            System.out.println("EVs receive offers and answer...");
            for (EV ev : getEvs()) {
                if (!ev.isServiced()) {
                    ev.readMessages();
                    ev.evaluateOffers();
                    ev.sendAnswers();
                }
            }

            System.out.println("Stations read answers and compute suggestions...");
            for (int s = 0; s < getStations().size(); s++) {
                Station station = getStations().get(s);
                station.readMessages();
                if (!station.isFinished()) {
                    System.out.println("Compute suggestions...");
                    station.computeOffers();
                    station.sendOfferMessages();
                } else {
                    getFinishedStations()[s] = 1;
                }
            }
        }
        checkResults();
        System.out.println("Execution successfully completed!");
        resetFinishedStations();
        Statistics statistics = new Statistics(getStations(), getSlotsNumber());
        statistics.computeStatisticsPerStation();
    }
}
