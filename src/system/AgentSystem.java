package system;

import evs.EV;
import evs.EVPreferences;
import io.DataGenerator;
import io.JSONFileParser;
import messaging.Mailbox;
import station.Station;
import station.StationData;
import various.ArrayTransformations;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Thesis on 26/4/2018.
 */
public class AgentSystem {

    private ArrayList<EV> evs;
    private ArrayList<Station> stations;
    private Mailbox stationsMailbox;
    private Mailbox evsMailbox;
    private int stationsNumber;
    private int[][] stationsLocations;
    private int evsNumber;
    private int slotsNumber;
    private int[] finishedStations;

    public AgentSystem() {

        try {
            Reader reader = new FileReader("files/system.txt");
            BufferedReader in = new BufferedReader(reader);
            in.readLine();
            String line = in.readLine();
            String[] tokens = line.split(",");
            stationsNumber = Integer.parseInt(tokens[0]);
            evsNumber = Integer.parseInt(tokens[1]);
            slotsNumber = Integer.parseInt(tokens[2]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataGenerator dt = new DataGenerator(stationsNumber, evsNumber, slotsNumber, 3);
        //dt.generateStationFile();
        dt.readStationFile();
        //dt.generateEVsFile(2, 5, 0.4, 1.4);
        dt.generatePriceFile();

        stationsMailbox = new Mailbox(stationsNumber);
        evsMailbox = new Mailbox(evsNumber);
        evs = new ArrayList<>();
        stations = new ArrayList<>();
        stationsLocations = new int[stationsNumber][2];
        finishedStations = new int[stationsNumber];
        int idCounter = 0;
        for (StationData d: JSONFileParser.readOfflineStations("station.json")) {
            stations.add(new Station("Station", idCounter, evsMailbox, stationsMailbox.getMessageList(idCounter), d, slotsNumber));
            stationsLocations[idCounter][0] = d.getX();
            stationsLocations[idCounter][1] = d.getY();
            idCounter++;
        }

        idCounter = 0;
        for (EVPreferences p: JSONFileParser.readEVsData("evs.json")) {
            evs.add(new EV("EV", idCounter, stationsMailbox, evsMailbox.getMessageList(idCounter), p, stationsLocations));
            idCounter++;
        }


        System.out.println("EVs send requests...");
        for (EV ev: evs)
            ev.sendRequests();

        System.out.println("Stations receive requests...");
        for (int s = 0; s < stationsNumber; s++) {
            Station station = stations.get(s);
            station.receiveRequests();
            System.out.println(station.evBiddersString());
            if (!station.isFinished()) {
                station.computeSchedule();
                station.sendOfferMessages();
            } else
                finishedStations[s] = 1;
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
            for (int s = 0; s < stationsNumber; s++) {
                Station station = stations.get(s);
                if (!station.isFinished()) {
                    station.readMessages();
                    System.out.println("Compute suggestions...");
                    station.computeSuggestions();
                    station.sendOfferMessages();
                } else {
                    finishedStations[s] = 1;
                }
            }
        }
        System.out.println("Execution successfully completed!");
        resetFinishedStations();
    }

    // checks if all stations have finished their duties
    private boolean executionOver () {
        if (ArrayTransformations.arraySum(finishedStations) == stationsNumber)
            return true;
        return false;
    }

    private void resetFinishedStations () {
        finishedStations = new int[stationsNumber];
    }
}
