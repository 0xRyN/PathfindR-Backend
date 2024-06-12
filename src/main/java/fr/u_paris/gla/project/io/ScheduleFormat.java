/**
 * 
 */
package fr.u_paris.gla.project.io;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;

/** A tool class for the schedule format.
 * 
 * @author Emmanuel Bigeon */
public final class ScheduleFormat {
    public static final int LINE_INDEX          = 0;
    public static final int TRIP_SEQUENCE_INDEX = 1;
    public static final int TERMINUS_INDEX      = 2;
    public static final int TIME_INDEX          = 3;

    /** Hidden constructor for tool class */
    private ScheduleFormat() {
        // Tool class
    }

    /** Read a trip sequence from its string representation
     * 
     * @param representation the representation
     * @return the sequence of branching */
    public static List<Integer> getTripSequence(String representation) {
        // TODO Read a trip sequence from a string

        throw new RuntimeException("Not implemented yet");
    }
    
    public static DateTimeFormatter getTimeFormatter() {
        return DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.LENIENT);
    }
}
