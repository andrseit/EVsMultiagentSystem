package station;

import station.optimize.Scheduler;

import java.util.HashMap;

/**
 * Created by Thesis on 27/4/2018.
 */
public class StationData {

    private int chargersNumber;
    private int x, y;
    private Scheduler scheduler;
    private HashMap<String, Integer> strategyFlags;

    // to price mallon de xreiazetai
    public StationData(int x, int y, int chargersNumber,
                       Scheduler scheduler, HashMap<String, Integer> strategyFlags) {
        this.x = x;
        this.y = y;
        this.chargersNumber = chargersNumber;
        this.scheduler = scheduler;
        this.strategyFlags = strategyFlags;
    }

    public int getChargersNumber() {
        return chargersNumber;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public HashMap<String, Integer> getStrategyFlags() {
        return strategyFlags;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
