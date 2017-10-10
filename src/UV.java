import ec.util.MersenneTwisterFast;
import sim.engine.Steppable;
import sim.util.Double2D;
import sim.util.MutableDouble2D;

abstract class UV implements Steppable {
    Simulator simulator;
    Double2D position;
    double velocity;
    MutableDouble2D goal;

    UV() {
        goal = new MutableDouble2D(0.0, 0.0);
    }

    static MersenneTwisterFast random;

    static void initializeRandom(long seed) {
        random = new MersenneTwisterFast(seed);
    }

    abstract void updateGoal();

    void move() {
        MutableDouble2D toGoal = new MutableDouble2D(goal.x - position.x, goal.y - position.y);

        // if the distance to goal is less than velocity,
        // then just move onto goal
        // else move towards goal as its velocity
        if (toGoal.length() < velocity) {
            simulator.area.setObjectLocation(this, new Double2D(goal));
        } else {
            MutableDouble2D newLocation = new MutableDouble2D(position);
            toGoal.resize(velocity);
            newLocation.addIn(toGoal);
            simulator.area.setObjectLocation(this, new Double2D(newLocation));
        }
    }

    void updatePosition() {
        position = simulator.area.getObjectLocation(this);
    }
}
