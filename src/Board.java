import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Inspector;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.simple.CircledPortrayal2D;
import sim.portrayal.simple.LabelledPortrayal2D;
import sim.portrayal.simple.MovablePortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;

import javax.swing.*;
import java.awt.*;

//visualization
public class Board extends GUIState {
    private static final int SIDE_LENGTH_PIXEL = 800;
    private static int sideLength;

    static {
        sideLength = Variables.getAreaSideLength();
    }

    private Display2D display;
    private JFrame displayFrame;
    private ContinuousPortrayal2D areaPortrayal = new ContinuousPortrayal2D();

    public static void main(String[] args) {
        Board vid = new Board();
        Console c = new Console(vid);
        c.setVisible(true);
    }

    private Board() {
        super(new Simulator(System.currentTimeMillis(), sideLength, sideLength));
    }

    public static String getName() {
        return "Water Area";
    }

    public Object getSimulationInspectedObject() {
        return state;
    }

    public Inspector getInspector() {
        Inspector i = super.getInspector();
        i.setVolatile(true);
        return i;
    }

    public void start() {
        super.start();
        setupPortrayals();
    }

    public void load(SimState state) {
        super.load(state);
        setupPortrayals();
    }

    private void setupPortrayals() {
        Simulator simulator = (Simulator) state;

        areaPortrayal.setField(simulator.area);

        areaPortrayal.setPortrayalForClass(USV.class,
                new MovablePortrayal2D(
                        new CircledPortrayal2D(
                                new LabelledPortrayal2D(
                                        new OvalPortrayal2D() {
                                            public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
                                                paint = new Color(0, 0, 255);
                                                super.draw(object, graphics, info);
                                            }
                                        }, 5.0, null, Color.black, true),
                                0, 5.0, Color.green, true)));

        areaPortrayal.setPortrayalForClass(UAV.class,
                new MovablePortrayal2D(
                        new CircledPortrayal2D(
                                new LabelledPortrayal2D(
                                        new OvalPortrayal2D() {
                                            public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
                                                paint = new Color(255, 0, 0);
                                                super.draw(object, graphics, info);
                                            }
                                        }, 5.0, null, Color.black, true),
                                0.0, UAV.radius * 2, Color.red, false)));

        display.reset();
        display.setBackdrop(Color.white);
        display.repaint();
    }

    public void init(Controller c) {
        super.init(c);
        display = new Display2D(SIDE_LENGTH_PIXEL, SIDE_LENGTH_PIXEL, this);
        display.setClipping(false);
        displayFrame = display.createFrame();
        displayFrame.setTitle("UAV Surveillance Simulation");
        c.registerFrame(displayFrame);
        displayFrame.setVisible(true);
        display.attach(areaPortrayal, "Area");
    }

    public void quit() {
        super.quit();
        if (displayFrame != null) {
            displayFrame.dispose();
        }
        displayFrame = null;
        display = null;
    }
}