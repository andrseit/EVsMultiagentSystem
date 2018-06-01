package io;

import main.ChargingSettings;
import evs.EVPreferences;
import evs.EVStrategySettings;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import station.StationData;
import station.optimize.ProfitCPLEX;
import station.optimize.Scheduler;
import station.optimize.ServiceCPLEX;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.toIntExact;

/**
 * Created by Thesis on 27/4/2018.
 */
public class JSONFileParser {

    public static ArrayList<EVPreferences> readEVsData(String path) {

        ArrayList<EVPreferences> evs = new ArrayList<>();
        JSONParser parser = new JSONParser();
        Reader reader;
        try {
            reader = new FileReader("files/" + path);
            BufferedReader in = new BufferedReader(reader);

            String line;
            int id = 0;
            while ((line = in.readLine()) != null) {

                Object object = parser.parse(line);

                JSONObject json_ev = (JSONObject) object;

                int x = toIntExact((long) json_ev.get("x"));
                int y = toIntExact((long) json_ev.get("y"));
                int f_x = toIntExact((long) json_ev.get("f_x"));
                int f_y = toIntExact((long) json_ev.get("f_y"));

                JSONObject preferences = (JSONObject) json_ev.get("preferences");

                int energy = toIntExact((long) preferences.get("energy"));

                //data.setEnergy(energy);

                int inform_slot = toIntExact((long) preferences.get("inform"));

                //data.setInformSlot(inform_slot);

                int start_slot = toIntExact((long) preferences.get("start"));
                int end_slot = toIntExact((long) (preferences.get("end")));
                int max_distance = toIntExact((long) (preferences.get("distance")));

                JSONObject strategy = (JSONObject) json_ev.get("strategy");

                int s_energy = toIntExact((long) strategy.get("energy"));
                int s_start = toIntExact((long) strategy.get("start"));
                int s_end = toIntExact((long) (strategy.get("end")));
                int s_prob = toIntExact((long) (strategy.get("probability")));
                int s_rounds = toIntExact((long) (strategy.get("rounds")));
                double s_range = Double.parseDouble(strategy.get("range").toString());
                String s_priority = strategy.get("priority").toString();


                ChargingSettings settings = new ChargingSettings(start_slot, end_slot, energy);
                EVStrategySettings strategySettings = new EVStrategySettings(s_start, s_end, s_energy,
                        s_rounds, s_prob, s_range, s_priority);
                EVPreferences evPreferences = new EVPreferences(settings, inform_slot,
                        x, y, f_x, f_y, max_distance, strategySettings);

                evs.add(evPreferences);
                id++;
            }
            reader.close();
        } catch (org.json.simple.parser.ParseException | IOException e) {
            e.printStackTrace();
        }

        return evs;
    }

    public static ArrayList<StationData> readOfflineStations (String path) {
        ArrayList<StationData> stations = new ArrayList<>();
        int num_chargers;
        int x, y;
        int id = 0;
        JSONParser parser = new JSONParser();
        Reader reader;
        try {
            reader = new FileReader("files/" + path);
            BufferedReader in = new BufferedReader(reader);

            String line;

            line = in.readLine();
            int slotsNumber = Integer.parseInt(line);

            while ((line = in.readLine()) != null) {
                Object object = parser.parse(line);

                JSONObject station_json = (JSONObject) object;

                num_chargers = toIntExact((long) station_json.get("chargers"));

                JSONObject location = (JSONObject) station_json.get("location");
                x = toIntExact((long) location.get("x"));
                y = toIntExact((long) location.get("y"));
                String pricePath = station_json.get("price_file").toString();

                JSONObject flagsObject = (JSONObject) station_json.get("flags");
                HashMap<String, Integer> flags = new HashMap<>();
                flags.put("window", toIntExact((long) flagsObject.get("window")));
                flags.put("suggestion", toIntExact((long) flagsObject.get("suggestion")));
                int cplex = toIntExact((long) flagsObject.get("cplex"));
                flags.put("cplex", cplex);
                flags.put("instant", toIntExact((long) flagsObject.get("instant")));

                int[] price = readPriceFile(pricePath, slotsNumber);

                Scheduler scheduler;
                // profit
                if (cplex == 0) {
                    scheduler = new ProfitCPLEX();
                } else {
                    scheduler = new ServiceCPLEX();
                }
                StationData data = new StationData(x, y, num_chargers, scheduler, flags);
                stations.add(data);
                id++;
            }
            reader.close();

        } catch (org.json.simple.parser.ParseException | IOException e) {
            e.printStackTrace();
        }
        return stations;
    }

    private static int[] readPriceFile (String path, int slotsNumber) {
        int[] price = new int[slotsNumber];

        try {
            BufferedReader in = new BufferedReader(new FileReader("files/price/" + path + ".txt"));
            String line;
            for (int s = 0; s < slotsNumber; s++) {
                line = in.readLine();
                String[] tokens = line.split(",");
                price[s] = Integer.parseInt(tokens[0]);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return price;
    }

}
