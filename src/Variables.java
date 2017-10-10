// Variables used for experiments in different conditions.
class Variables {
    // Change this number to quickly switch over different experimental conditions.
    private static final int TEST_CONDITION = 0;

    private static final int[] AREA_SIDE_LENGTH = {100, 100, 100};
    private static final int[] USV_NUMBER = {10, 10, 10};

    private static final double[] UAV_VELOCITY = {2, 0.2, 1};
    private static final double[] USV_VELOCITY = {1, 0.1, 0.5};

    private static final double[] UAV_RADIUS = {20, 2, 10};

    static int getAreaSideLength() {
        return AREA_SIDE_LENGTH[TEST_CONDITION];
    }

    static int getUSVNumber() {
        return USV_NUMBER[TEST_CONDITION];
    }

    static double getUAVVelocity() {
        return UAV_VELOCITY[TEST_CONDITION];
    }

    static double getUAVRadius() {
        return UAV_RADIUS[TEST_CONDITION];
    }

    static double getUSVVelocity() {
        return USV_VELOCITY[TEST_CONDITION];
    }
}
