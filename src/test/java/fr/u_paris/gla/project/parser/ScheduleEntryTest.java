package fr.u_paris.gla.project.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for {@link CSVNetworkParser}.
 * It includes several scenarios to ensure the correctness of the CSV parsing logic
 * for extracting schedule entries from a CSV file.
 *
 * @version 1.0
 *
 * @see ScheduleEntry
 * @see CSVNetworkParser
 * @author: Fouad DJELLALI.
 */
class ScheduleEntryTest {
    private CSVNetworkParser parser;
    private String scheduleCSVPath = "data/schedule.csv";

    /**
     * Setup method to initialize and parse CSV data before each test.
     */
    @BeforeEach
    public void setUp() throws IOException {
        this.parser = new CSVNetworkParser("",scheduleCSVPath);
        this.parser.parseScheduleCSV();
    }

    /**
     * Tests the parser's ability to correctly extract the line information from a CSV entry.
     */
    @Test
    void testParseScheduleCSVLine() {
        assertEquals("B", parser.getScheduleEntries().get(0).line().getName());
    }
    /**
     * Tests the parser's ability to correctly extract the forks information from a CSV entry.
     */
    @Test
    void testParseScheduleCSVForks() {
        assertEquals(List.of(2, 1, 3), parser.getScheduleEntries().get(0).forks());
    }
    /**
     * Tests the parser's ability to correctly extract the station name from a CSV entry.
     */
    @Test
    void testParseScheduleCSVStation() {
        assertEquals("Borderouge", parser.getScheduleEntries().get(0).station().getName());
    }
    /**
     * Tests the parser's ability to correctly extract the departure time from a CSV entry.
     */
    @Test
    void testParseScheduleCSVDepartureTime() {
        assertEquals(LocalTime.of(7, 0), parser.getScheduleEntries().get(0).time());
    }
}