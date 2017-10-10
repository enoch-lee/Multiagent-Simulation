import sim.engine.SimState;
import sim.util.Double2D;

import java.util.ArrayList;

public class UAV extends UV {
    static double radius;

    static ArrayList<Integer> usv_in_view=new ArrayList<Integer>();

    private int usvNumber;
    private Double2D[] usvPositions;

    private static int step;
    private int[] lastVisited;

    private boolean[] visited;

    UAV(int usvNumberIn) {
        super();
        velocity = Variables.getUAVVelocity();
        radius = Variables.getUAVRadius();

        usvNumber = usvNumberIn;
        usvPositions = new Double2D[usvNumber];

        step = 0;
        lastVisited = new int[usvNumber];
        for (int i = 0; i != usvNumber; i++) {
            lastVisited[i] = step;
        }

        visited = new boolean[usvNumber];
        for (int i = 0; i != usvNumber; i++) {
            visited[i] = false;
        }
    }

    private void updateUSVPositions() {
        for (int i = 0; i != usvNumber; i++) {
            USV usv = simulator.usvs.get(i);
            Double2D usvPosition = simulator.area.getObjectLocation(usv);
            usvPositions[i] = usvPosition;
        }
    }

    private void updateCurrentInSurveillance() {
        int currentInSurveillance = 0;
        int totalDataAge = 0;
        for (int i = 0; i != usvNumber; i++) {
            if (distance(position, usvPositions[i]) < radius) {
                lastVisited[i] = step;
                visited[i] = true;
                currentInSurveillance++;
            }
            totalDataAge += step - lastVisited[i];
        }
        double averageDataAge = totalDataAge * 1.0 / usvNumber;

        //System.out.println("Current Under Surveillance: " + currentInSurveillance);
        System.out.println("Average Data Age = " + averageDataAge);
    }

    @Override
    void updateGoal() {
        Double2D goalPosition = findMaxEnclosure();
        if (goalPosition == null) {
            // The distance between any pair of two USVs is more than radius * 2,
            // so there is no such circle that encloses more than 1 USV.
            // So, find the nearest USV
            goalPosition = findNearestUSV();
        }

        goal.setTo(goalPosition.x, goalPosition.y);
    }

    // Condition 1:
    // When the USVs are close to each other,
    // find a circle to enclose the maximum number of USVs within the UAV’s surveillance area.
    private Double2D findMaxEnclosure() {
        double sum=0;
        Double2D maxEnclosure = null;
        int max = 0;
        for (int i = 0; i != usvNumber - 1; i++) {
            Double2D p1 = usvPositions[i];
            for (int j = i + 1; j != usvNumber; j++) {
                Double2D p2 = usvPositions[j];
                if (distance(p1, p2) < radius * 2) {
                    Double2D center = getCenter(p1, p2);
                    int pn = 2;
                    for (int k = 0; k != usvNumber; k++) {
                        if (k != i && k != j) {
                            Double2D p3 = usvPositions[k];
                            if (distance(center, p3) < radius) {
                                pn++;
                            }
                        }
                    }
                    if (pn > max) {
                        max = pn;
                        maxEnclosure = center;
                    }
                }
            }
        }
        usv_in_view.add(max);
        //step++;
        for(int i:usv_in_view){
            sum+=i;
        }
        double avg_usvs=sum*1.0/step;
        System.out.println("Steps: "+ step + " Avg USVs in View: "+ avg_usvs);
        return maxEnclosure;
    }

    private double distance(Double2D p1, Double2D p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    // Given two points on same circle and its radius,
    // find the center of the circle.
    private Double2D getCenter(Double2D a, Double2D b) {
        double t = distance(a, b) / 2.0;
        t = Math.sqrt((radius * radius - t * t));
        double x, y;
        if (a.y == b.y) {
            x = (a.x + b.x) / 2.0;
            y = a.y + t;
        } else if (a.x == b.x) {
            x = a.x + t;
            y = (a.y + b.y) / 2.0;
        } else {
            double kt = Math.atan(-(a.x - b.x) / (a.y - b.y));
            x = (a.x + b.x) / 2.0 + Math.cos(kt) * t;
            y = (a.y + b.y) / 2.0 + Math.sin(kt) * t;
        }
        return new Double2D(x, y);
    }

    private Double2D findNearestUSV() {
        //judge if all usvs have been monitored, if so, change each flag to false
        boolean allVisited = true;
        for (int i = 0; i != usvNumber; i++) {
            if (!visited[i]) {
                allVisited = false;
                break;
            }
        }

        if (allVisited) {
            for (int i = 0; i != usvNumber; i++) {
                visited[i] = false;
            }
        }

        int nearestUSV = 0;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i != usvNumber; i++) {
            double dist = distance(position, usvPositions[i]);
            if (dist < minDistance && !visited[i]) {
                minDistance = dist;
                nearestUSV = i;
            }
        }

        return usvPositions[nearestUSV];
    }

    private static int index = 0;

    //convex hull
    void updateGoal2(Simulator simulator) {
        ArrayList<USV> usvs = simulator.usvs;
        int n = usvs.size();

        ArrayList<Double2D> points = new ArrayList<>();
        for (int i = 0; i != n; i++) {
            USV usv = usvs.get(i);
            points.add(simulator.area.getObjectLocation(usv));
        }

        QuickHull qh = new QuickHull();
        ArrayList<Double2D> p = qh.quickHull(points);
        if (index < p.size()) {
            goal.setTo(p.get(index).x, p.get(index).y);
            index++;
        } else {
            p = qh.quickHull(points);
            index = 0;
        }
        //System.out.println("Now UAV covers " + max + " USV(s).");
        //System.out.println("It will move to " + max_location.x + ", " + max_location.y + ".");
        //goal.setTo(max_location.x, max_location.y);
    }

    public void step(SimState state) {
        this.simulator = (Simulator) state;

        step++;
        //System.out.println("Steps: "+step);
        updatePosition();
        updateUSVPositions();
        updateCurrentInSurveillance();
        updateGoal();
        move();
    }
}
