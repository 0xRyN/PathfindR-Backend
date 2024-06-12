/**
 * 
 */
package fr.u_paris.gla.project.utils;

/** A utility class for computations related to GPS.
 * 
 * @author Emmanuel Bigeon */
public final class GPS {

    /** The value of a flat angle, in degrees. */
    private static final int    FLAT_ANGLE_DEGREE = 180;
    /** The (approximated) earth radius in km. */
    private static final double EARTH_RADIUS      = 6_370.0;

    /** Hidden constructor for tool class */
    private GPS() {
        // Tool class
    }

    /** Convert a degree angle value in a radian angle one.
     * 
     * @param degree the degree value
     * @return the radian value */
    private static double degreeToRadian(double degree) {
        return degree / FLAT_ANGLE_DEGREE * Math.PI;
    }

    /** Compute the flying distance between two GPS positions.
     * 
     * @param latitude1 the latitude of the first position
     * @param longitude1 the longitude of the first position
     * @param latitude2 the latitude of the second position
     * @param longitude2 the longitude of the second position
     * @return the flying distance */
    public static double distance(double latitude1, double longitude1, double latitude2,
            double longitude2) {
        double deltaLatitude = degreeToRadian(latitude2 - latitude1);
        double deltaLongitude = degreeToRadian(longitude2 - longitude1);

        latitude1 = degreeToRadian(latitude1);
        latitude2 = degreeToRadian(latitude2);

        double a = Math.pow(Math.sin(deltaLatitude / 2), 2)
                + Math.pow(Math.sin(deltaLongitude / 2), 2) * Math.cos(latitude1)
                        * Math.cos(latitude2);
        return 2 * EARTH_RADIUS * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
