import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;

import java.util.ArrayList;

public class Simulator extends SimState {
    Continuous2D area;

    ArrayList<USV> usvs;

    Simulator(long seed, int width, int height) {
        super(seed);
        UV.initializeRandom(seed);

        this.area = new Continuous2D(1.0, width, height);
    }

    public void start() {
        super.start();

        // clear the area
        area.clear();

        // add some USVs to the area
        int usvNumber = Variables.getUSVNumber();
        usvs = new ArrayList<>();
        for (int i = 0; i != usvNumber; i++) {
            USV usv = new USV();
            usvs.add(usv);
            area.setObjectLocation(usv, new Double2D(0.0, 0.0));
            schedule.scheduleRepeating(usv);
        }

        // add a UAV to the area
        UAV uav = new UAV(usvNumber);
        area.setObjectLocation(uav, new Double2D(0.0, 0.0));
        schedule.scheduleRepeating(uav);
    }

    public static void main(String[] args) {
        doLoop(Simulator.class, args);
        System.exit(0);

    }
}