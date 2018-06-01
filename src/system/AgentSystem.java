package system;

import evs.EV;
import evs.EVPreferences;
import io.DataGenerator;
import io.JSONFileParser;
import messaging.Mailbox;
import station.Station;
import station.StationData;

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
    private Execution execution;

    public AgentSystem(boolean offline) {

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
        //dt.generateEVsFile(2, 3, 0.4, 1.4);
        dt.generatePriceFile();

        stationsMailbox = new Mailbox(stationsNumber);
        evsMailbox = new Mailbox(evsNumber);
        evs = new ArrayList<>();
        stations = new ArrayList<>();
        stationsLocations = new int[stationsNumber][2];
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

        if (offline)
            execution = new OfflineExecution(evs, stations, slotsNumber);
        else
            execution = new OnlineExecution(evs, stations, slotsNumber);
    }

    public void run() {
        execution.execute();
    }

}
