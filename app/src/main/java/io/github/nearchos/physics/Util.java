package io.github.nearchos.physics;

public class Util {

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