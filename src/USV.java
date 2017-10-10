import sim.engine.SimState;
import sim.util.MutableDouble2D;

public class USV extends UV {
    USV() {
        super();
        velocity = Variables.getUSVVelocity();
    }

    @Override
    void updateGoal() {
        // if the USV reaches its current goal,
        // give it a new goal
        MutableDouble2D toGoal = new MutableDouble2D();
        toGoal.setTo(goal.x - position.x, goal.y - position.y);
        if (toGoal.length() == 0.0) {
            goal.setTo(simulator.area.width * random.nextDouble(), simulator.area.height * random.nextDouble());
            //way point
            toGoal.setTo(goal.x - position.x, goal.y - position.y);
        }
    }

    public void step(SimState state) {
        this.simulator = (Simulator) state;

        updatePosition();
        updateGoal();
        move();
    }
}
