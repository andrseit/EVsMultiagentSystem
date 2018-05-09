package station.optimize;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.cppimpl.IloNumExpr;
import ilog.cplex.IloCplex;
import station.EVObject;

import java.util.ArrayList;

/**
 * Created by Thesis on 4/5/2018.
 */
public class OptimizeSuggestions {

    private IloCplex cp;
    private IloNumVar[][] vStart;
    private IloNumVar[][] vEnd;
    private IloNumVar[][] chargesInSlot;
    private IloNumVar[] charges;
    private int[] chargers;
    private int slotsNumber;
    private int evsNumber;
    private ArrayList<EVObject> evs;


    public OptimizeSuggestions(int slotsNumber, ArrayList<EVObject> evs) {
        this.slotsNumber = slotsNumber;
        chargers = new int[slotsNumber];
        this.evs = evs;
        evsNumber = evs.size();
        charges = new IloNumVar[evsNumber];
        vStart = new IloNumVar[evsNumber][slotsNumber];
        vEnd = new IloNumVar[evsNumber][slotsNumber];
        chargesInSlot = new IloNumVar[evsNumber][slotsNumber];
        for (int s = 0; s < slotsNumber; s++) {
            chargers[s] = 0;
        }
        chargers[3] = 0;
        chargers[4] = 0;
        chargers[5] = 0;

        try {
            cp = new IloCplex();
            cp.setOut(null);
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public void optimize () {

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
                int arrival = ev.getArrival();
                int departure = ev.getDeparture();
                int energy = ev.getEnergy();

                IloLinearNumExpr energyDistance = cp.linearNumExpr();
                IloLinearNumExpr distance = cp.linearNumExpr();
                IloLinearNumExpr left = cp.linearNumExpr();

                IloLinearNumExpr energyConstraint = cp.linearNumExpr();
                for (int s = 0; s < slotsNumber; s++) {
                    int d = 0;
                    if (s < arrival)
                        d = arrival - s;
                    else if (s > departure)
                        d = s - departure;
                    distance.addTerm(-0.01*(d+1), chargesInSlot[e][s]);
                    left.addTerm(-0.001*(s+1), chargesInSlot[e][s]);
                    energyConstraint.addTerm(1, chargesInSlot[e][s]);
                }
                service.addTerm(1, charges[e]);
                cp.addLe(energyConstraint, energy);
                cp.addLe(energyConstraint, cp.prod(charges[e], energy));
                cp.addGe(energyConstraint, cp.prod(charges[e], 1));
                //cp.addGe(objective, 0.01);
                objective.add(distance);
                objective.add(left);
                objective.add(energyConstraint);
                //objective.addTerm(-energy, dumbTrue[e]);
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
                System.out.println("YAY!");
                System.out.println("Utility: " + cp.getObjValue());
                for (int e = 0; e < evsNumber; e++) {
                    int start = -1;
                    int end = -1;
                    System.out.print("EV_" + e + ": ");
                    for (int s = 0; s < slotsNumber; s++) {
                        if (cp.getValue(chargesInSlot[e][s]) > 0) {
                            System.out.print(1 + " ");
                            if (start == -1)
                                start = s;
                        }
                        else
                            System.out.print(0 + " ");
                    }
                    System.out.println("");
                    for (int s = slotsNumber - 1; s >= 0; s--) {
                        if (cp.getValue(chargesInSlot[e][s]) > 0) {
                            if (end == -1)
                                end = s;
                        }
                    }
                    System.out.println("start: " + start + ", end: " + end);
                }
            } else {
                System.out.println("MEH!");
            }

        } catch (IloException e) {
            e.printStackTrace();
        }
    }
}
