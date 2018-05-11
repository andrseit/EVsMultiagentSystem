package station.optimize;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import station.EVObject;

import java.util.ArrayList;

/**
 * Created by Thesis on 30/4/2018.
 */
public class CPLEX extends Scheduler {

    // take these as inputs
    private ArrayList<EVObject> evs;
    private int[] chargers;
    private int[] price;
    private int allSlotsNumber; // because in the model a part of them is only used
    private int slotsNumber;

    // compute these slots so that a subproblem is computed
    private int minSlot, maxSlot;



    // cplex variables
    private IloCplex cp; // the model
    private IloNumVar[][] chargeInSlot; // if an EV charges in a slot
    private IloNumVar[] charges; // if an EV charges in general
    private IloLinearNumExpr objective; // the objective function


    private void setMinMaxSlot () {
        minSlot = slotsNumber;
        maxSlot = 0;
        for (EVObject ev: evs) {
            if (ev.getArrival() < minSlot)
                minSlot = ev.getArrival();
            if (ev.getDeparture() > maxSlot)
                maxSlot = ev.getDeparture();
        }
    }

    private void initializeVariables (ArrayList<EVObject> evs, int[] chargers, int[] price) {
        this.evs = evs;
        this.chargers = chargers;
        this.price = price;
        this.allSlotsNumber = chargers.length;
        setMinMaxSlot();
        slotsNumber = maxSlot - minSlot + 1;

        try {
            cp = new IloCplex();
            cp.setParam(IloCplex.DoubleParam.TiLim, 2000);
            cp.setParam(IloCplex.DoubleParam.EpGap, 0.09);
            cp.setOut(null);
        } catch (IloException e) {
            e.printStackTrace();
        }


        int evsNumber = evs.size();
        chargeInSlot = new IloNumVar[evsNumber][slotsNumber];
        charges = new IloNumVar[evsNumber];

        try {
            objective = cp.linearNumExpr();

            for (int e = 0; e < evsNumber; e++) {
                for (int s = 0; s < slotsNumber; s++) {
                    chargeInSlot[e][s] = cp.boolVar("var(" + e + ", " + s + ")");
                }
                charges[e] = cp.boolVar("c(" + e + ")");
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    private void addEnergySatisfactionConstraints () {
        try {
            for (int ev = 0; ev < evs.size(); ev++) {
                EVObject current = evs.get(ev);
                boolean[] checkedSlots = new boolean[slotsNumber];

                IloLinearNumExpr evEnergyConstraint = cp.linearNumExpr();

                int arrival = current.getArrival();
                int departure = current.getDeparture();
                int energy = current.getEnergy();

                for (int s = 0; s < slotsNumber; s++) {
                    if (s >= arrival - minSlot && s <= departure - minSlot) {
                        evEnergyConstraint.addTerm(1, chargeInSlot[ev][s]);
                        checkedSlots[s] = true;
                    }
                }

                for (int s = 0; s < slotsNumber; s++) {
                    if (!checkedSlots[s])
                        cp.addEq(0, chargeInSlot[ev][s]);
                }

                cp.addEq(evEnergyConstraint, cp.prod(charges[ev], energy));
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    private void addChargersConstraint () {
        try {
            int evsNumber = evs.size();
            for (int s = 0; s < slotsNumber; s++) {
                IloLinearNumExpr chargers_constraint = cp.linearNumExpr();
                for (int ev = 0; ev < evsNumber; ev++) {
                    chargers_constraint.addTerm(1, chargeInSlot[ev][s]);
                }
                // s + min_slot e.g. min_slot = 5, current slot = 0, charger[current] = charger[5] -> current + min
                cp.addLe(chargers_constraint, chargers[s + minSlot]);
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    // ABSTRACT - this one is for profit
    private void addObjectiveFunction () {
        try {
            for (int ev = 0; ev < evs.size(); ev++) {
                EVObject current = evs.get(ev);
                int arrival = current.getArrival();
                int departure = current.getDeparture();

                for (int s = 0; s < slotsNumber; s++) {
                    if (s >= arrival - minSlot && s <= departure - minSlot) {
                        objective.addTerm(price[s], chargeInSlot[ev][s]);
                    }
                }
            }
            cp.addMaximize(objective);
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    private void solve () {
        try {
            if (cp.solve()) {
                int[][] schedule = new int[evs.size()][allSlotsNumber];
                int [] whoCharged = new int[evs.size()];

                for (int ev = 0; ev < evs.size(); ev++) {
                    for (int s = 0; s < allSlotsNumber; s++) {
                        if (s >= minSlot && s <= maxSlot)
                            schedule[ev][s] = (int) cp.getValue(chargeInSlot[ev][s - minSlot]);
                         else
                            schedule[ev][s] = 0;
                    }
                    for (int s = minSlot; s <= maxSlot; s++) {

                    }
                    if (cp.getValue(charges[ev]) > 0) {
                        whoCharged[ev] = 1;
                    } else {
                        whoCharged[ev] = 0;
                    }
                }

                setSchedule(schedule);
                setWhoCharged(whoCharged);
            } else {
                System.err.println("Optimal Schedule could not be computed!");
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    private void clearModel () {
        try {
            cp.clearModel();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public void compute (ArrayList<EVObject> evs, int[] chargers, int[] price) {
        initializeVariables(evs, chargers, price);
        addEnergySatisfactionConstraints();
        addChargersConstraint();
        addObjectiveFunction();
        solve();
        clearModel();
    }

}
