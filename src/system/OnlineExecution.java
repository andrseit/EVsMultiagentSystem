package system;

import evs.EV;
import station.EVObject;
import station.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Thesis on 10/5/2018.
 */
public class OnlineExecution extends Execution {

    public OnlineExecution(ArrayList<EV> evs, ArrayList<Station> stations, int slotsNumber) {
        super(evs, stations, slotsNumber);
    }

    @Override
    public void execute() {

        sortEVs();

        for (int s = 0; s < getSlotsNumber(); s++) {
            ArrayList<EV> evs = chooseEVs(s);
            System.out.println("Slot: " + s);
            if (!evs.isEmpty()) {
                System.out.println("EVs send requests...");
                for (EV ev : evs) {
                    if (ev.getInformSlot() == s)
                        ev.sendRequests();
                }

                System.out.println("Stations receive requests...");
                for (int st = 0; st < getStations().size(); st++) {
                    Station station = getStations().get(st);
                    station.setDistance(true);
                    station.setCurrentSlot(s);
                    station.receiveRequests();
                    //System.out.println(station.evBiddersString());
                    if (!station.isFinished()) {
                        station.computeOffers();
                        station.sendOfferMessages();
                    } else
                        getFinishedStations()[st] = 1;
                }


                while (!executionOver()) {
                    System.out.println("EVs receive offers and answer...");
                    for (EV ev : evs) {
                        if (!ev.isServiced()) {
                            ev.readMessages();
                            ev.evaluateOffers();
                            ev.sendAnswers();
                        }
                    }

                    System.out.println("Stations read answers and compute suggestions...");
                    for (int st = 0; st < getStations().size(); st++) {
                        Station station = getStations().get(st);
                        station.readMessages();
                        if (!station.isFinished()) {
                            System.out.println("Compute suggestions...");
                            station.computeOffers();
                            station.sendOfferMessages();
                        } else {
                            getFinishedStations()[st] = 1;
                        }
                    }
                }
                resetFinishedStations();
            } else {
                System.out.println("No requests!");
            }
        }
        System.out.println("Execution successfully completed!");

    }

    // chooses EVs that inform in a specific slot
    private ArrayList<EV> chooseEVs (int slot) {
        ArrayList<EV> chosenEVs = new ArrayList<>();
        for (EV ev: getEvs()) {
            //System.out.println("Slot: " + ev.getInformSlot());
            if (ev.getInformSlot() == slot) {
                chosenEVs.add(ev);
            } else if (ev.getInformSlot() > slot)
                break;
        }
        return chosenEVs;
    }

    // sorts EVs by inform slot
    private void sortEVs () {
        Collections.sort(getEvs(), new Comparator<EV>() {
            @Override
            public int compare(EV o1, EV o2) {
                return o1.getInformSlot() - o2.getInformSlot();
            }
        });
        for (EV ev: getEvs()) {
            System.out.println(ev);
        }
    }
}
