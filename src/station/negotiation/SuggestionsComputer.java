package station.negotiation;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import station.EVObject;
import station.optimize.OptimizeSuggestions;
import station.optimize.Scheduler;
import various.ArrayTransformations;

import java.util.ArrayList;

/**
 * Created by Thesis on 3/5/2018.
 */
public class SuggestionsComputer extends Scheduler {

    private int slotsNumber;
    private int evsNumber;

    private IloCplex cp;
    private IloNumVar[][] chargesInSlot;
    private IloNumVar[] charges;

    public SuggestionsComputer() {
        try {
            cp = new IloCplex();
            cp.setOut(null);
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    // maybe protect them somehow so if the user makes changes in them
    // the original instances won't be affected

    // ABSTRACT
    public void compute (ArrayList<EVObject> evs, int[] chargers, int[] price) {
        initializeVariables(evs, chargers, price);

        try {
            IloLinearNumExpr objective = cp.linearNumExpr();
            for (int e = 0; e < evsNumber; e++) {
                charges[e] = cp.boolVar("charges(" + e + ")");
                for (int s = 0; s < slotsNumber; s++) {
                    chargesInSlot[e][s] = cp.boolVar("cSlot(" + e + ", " + s + ")");
                }
            }

            IloLinearNumExpr service = cp.linearNumExpr();
            for (int e = 0; e < evsNumber; e++) {
                EVObject ev = evs.get(e);
                System.out.println(""+(getCurrentSlot() + ev.getDistance()));
                int arrival = ev.getArrival();
                int departure = ev.getDeparture();
                int energy = ev.getEnergy();

                IloLinearNumExpr distance = cp.linearNumExpr();
                IloLinearNumExpr left = cp.linearNumExpr();

                IloLinearNumExpr energyConstraint = cp.linearNumExpr();
                for (int s = 0; s < slotsNumber; s++) {

                    if (s >= getCurrentSlot() + ev.getDistance()) {
                        int d = 0;
                        if (s < arrival)
                            d = arrival - s;
                        else if (s > departure)
                            d = s - departure;
                        distance.addTerm(-0.01 * (d + 1), chargesInSlot[e][s]);
                        left.addTerm(-0.001 * (s + 1), chargesInSlot[e][s]);
                        energyConstraint.addTerm(1, chargesInSlot[e][s]);
                    } else {
                        cp.addEq(0, chargesInSlot[e][s]);
                    }
                }
                service.addTerm(1, charges[e]);
                //cp.addLe(energyConstraint, energy);
                cp.addLe(energyConstraint, cp.prod(charges[e], energy));
                cp.addGe(energyConstraint, cp.prod(1, charges[e]));

                objective.add(distance);
                objective.add(left);
                objective.add(energyConstraint);
            }
            //objective.add(service);
            cp.addMaximize(objective);
            for (int s = 0; s < slotsNumber; s++) {
                IloLinearNumExpr chargersConstraint = cp.linearNumExpr();
                for (int e = 0; e < evsNumber; e++) {
                    chargersConstraint.addTerm(chargesInSlot[e][s], 1);
                }
                cp.addLe(chargersConstraint, chargers[s]);
            }

            if (cp.solve()) {
                int[][] schedule = new int[evs.size()][slotsNumber];
                int [] whoCharged = new int[evs.size()];

                for (int e = 0; e < evsNumber; e++) {
                    if (cp.getValue(charges[e]) > 0)
                        whoCharged[e] = 1;
                    for (int s = 0; s < slotsNumber; s++) {
                        if (cp.getValue(chargesInSlot[e][s]) > 0) {
                            schedule[e][s] = 1;
                        }
                    }
                }
                setSchedule(schedule);
                setWhoCharged(whoCharged);
            } else {
                System.err.println("Problem could not be solved. Suggestions not computed!");
            }
            cp.clearModel();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    private void initializeVariables (ArrayList<EVObject> evs, int[] chargers, int[] price) {
        slotsNumber = chargers.length;
        evsNumber = evs.size();
        chargesInSlot = new IloNumVar[evsNumber][slotsNumber];
        charges = new IloNumVar[evsNumber];
    }

}
