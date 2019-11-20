package io.github.nearchos.projectiles;

public class Util {

    public static final int BALANCE_POINT_VELOCITY = 50; // assumes 0 .. 100, so balance point is 50

    public static final double BALANCE_POINT_ANGLE_IN_RADIANS = Math.PI / 4d; // assumes 0 .. 90 degrees so balance point is 45 degrees

    /**
     * Computes maximum horizontal distance.
     */
    static double computeRange(final double initialVelocity, final double angleInRadians, final double gravity) {
        // V_0 ^ 2 * sin(2 * thita) / g
        return Math.pow(initialVelocity, 2d) * Math.sin(2 * angleInRadians) / gravity;
    }

    static double computeTimeOfFlight(final double initialVelocity, final double angleInRadians, final double gravity) {
        // 2 * v_0 * sin(thita) / g
        return 2d * initialVelocity * Math.sin(angleInRadians) / gravity;
    }

    static double computeMaxHeight(final double initialVelocity, final double angleInRadians, final double gravity) {
        // V_0 ^ 2  * (sin(theta)) ^ 2 / (2 * g)
        return Math.pow(initialVelocity, 2d) * Math.pow(Math.sin(angleInRadians), 2d) / (2 * gravity);
    }

    static double degreesToRadians(final int degrees) {
        return degrees * Math.PI / 180d;
    }

    static int radiansToDegrees(final double radians) {
        return (int) (radians * 180d / Math.PI);
    }
}