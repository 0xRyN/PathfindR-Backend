/**
 * 
 */
package fr.u_paris.gla.project.idfm;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVParser;
import com.opencsv.ICSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import fr.u_paris.gla.project.io.NetworkFormat;
import fr.u_paris.gla.project.utils.GPS;

/** Code of an extractor for the data from IDF mobilite.
 * 
 * @author Emmanuel Bigeon */
public class IDFMNetworkExtractor {
    /** The logger for information on the process */
    private static final Logger LOGGER = Logger
            .getLogger(IDFMNetworkExtractor.class.getName());

    // IDF mobilite API URLs
    private static final String TRACE_FILE_URL = "https://data.iledefrance-mobilites.fr/api/explore/v2.1/catalog/datasets/traces-des-lignes-de-transport-en-commun-idfm/exports/csv?lang=fr&timezone=Europe%2FBerlin&use_labels=true&delimiter=%3B";
    private static final String STOPS_FILE_URL = "https://data.iledefrance-mobilites.fr/api/explore/v2.1/catalog/datasets/arrets-lignes/exports/csv?lang=fr&timezone=Europe%2FBerlin&use_labels=true&delimiter=%3B";

    // IDF mobilite csv formats
    private static final int IDFM_TRACE_ID_INDEX    = 0;
    private static final int IDFM_TRACE_SNAME_INDEX = 1;
    private static final int IDFM_TRACE_SHAPE_INDEX = 6;

    private static final int IDFM_STOPS_RID_INDEX  = 0;
    private static final int IDFM_STOPS_NAME_INDEX = 5;
    private static final int IDFM_STOPS_LON_INDEX  = 6;
    private static final int IDFM_STOPS_LAT_INDEX  = 7;

    // Magically chosen values
    /** A number of stops on each line */
    private static final int    GUESS_STOPS_BY_LINE   = 5;
    /** Maximal speed in km/h */
    private static final double MAX_SPEED             = 5;
    /** Distance to reach maximal speed in km */
    private static final double ACCELERATION_DISTANCE = 0.1;

    // Well named constants
    private static final double _250_METERS      = .25;
    private static final long   SECONDS_IN_HOURS = 3_600;

    private static final Format GPS_FORMATTER = NetworkFormat.getGPSFormatter();

    public static class StopEntry {
        private String      lname;
        public final double longitude;
        public final double latitude;

        /** Create the stop
         * 
         * @param lname
         * @param longitude
         * @param latitude */
        public StopEntry(String lname, double longitude, double latitude) {
            super();
            this.lname = lname;
            this.longitude = longitude;
            this.latitude = latitude;
        }

        @Override
        public String toString() {
            return MessageFormat.format("{0} [{1}, {2}]", this.lname, this.longitude,
                    this.latitude);
        }
    }

    public static final class UnidentifiedStopEntry extends StopEntry {
        /** Create the stop
         * 
         * @param longitude
         * @param latitude */
        public UnidentifiedStopEntry(double longitude, double latitude) {
            super("Unidentified", longitude, latitude);
        }

        List<StopEntry> candidates = new ArrayList<>();

        @Override
        public String toString() {
            return "UnidentifiedStop [candidates=" + this.candidates + "]";
        }
    }

    private static final class TraceEntry {
        String                lname;
        List<List<StopEntry>> stops = new ArrayList<>();
    }

    public static void readCSVFromURL(String url, Consumer<String[]> contentLineConsumer)
            throws IOException {
        ICSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        try (InputStream is = new URL(url).openStream();
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, StandardCharsets.UTF_8))) {
            CSVReaderBuilder csvBuilder = new CSVReaderBuilder(reader)
                    .withCSVParser(parser);
            try (CSVReader csv = csvBuilder.build()) {
                String[] line = csv.readNextSilently(); // Eliminate header
                while (csv.peek() != null) {
                    line = csv.readNext();
                    contentLineConsumer.accept(line);
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("Invalid csv file for lines", e);
        }
    }

    /** Main entry point for the extractor of IDF mobilite data into a network as
     * defined by this application.
     * 
     * @param args the arguments (expected one for the destination file) */
    public static void main(String[] args) {

        if (args.length != 1) {
            LOGGER.severe("Invalid command line. Missing target file.");
            return;
        }

        Map<String, TraceEntry> traces = new HashMap<>();
        try {
            readCSVFromURL(TRACE_FILE_URL, (String[] line) -> {
                TraceEntry entry = new TraceEntry();
                entry.lname = line[IDFM_TRACE_SNAME_INDEX];
                List<List<StopEntry>> buildPaths = buildPaths(
                        line[IDFM_TRACE_SHAPE_INDEX]);
                entry.stops.addAll(buildPaths);
                if (buildPaths.isEmpty()) {
                    LOGGER.severe(() -> MessageFormat.format(
                            "Line {0} has no provided itinerary and was ignored",
                            entry.lname));
                } else {
                    traces.put(line[IDFM_TRACE_ID_INDEX], entry);
                }
            });
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while reading the line paths", e);
        }

        List<StopEntry> stops = new ArrayList<>(traces.size() * GUESS_STOPS_BY_LINE);
        try {
            readCSVFromURL(STOPS_FILE_URL, (String[] line) -> {
                StopEntry entry = new StopEntry(line[IDFM_STOPS_NAME_INDEX],
                        Double.parseDouble(line[IDFM_STOPS_LON_INDEX]),
                        Double.parseDouble(line[IDFM_STOPS_LAT_INDEX]));
                String rid = line[IDFM_STOPS_RID_INDEX];
                BiFunction<? super String, ? super TraceEntry, ? extends TraceEntry> func = (
                        k, trace) -> {
                    for (List<StopEntry> path : trace.stops) {
                        for (StopEntry stopEntry : path) {
                            if (stopEntry instanceof UnidentifiedStopEntry
                                    && GPS.distance(entry.latitude, entry.longitude,
                                            stopEntry.latitude,
                                            stopEntry.longitude) < _250_METERS) {
                                ((UnidentifiedStopEntry) stopEntry).candidates.add(entry);
                            }
                        }
                    }
                    return trace;
                };
                traces.computeIfPresent(rid, func);
                stops.add(entry);
            });
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while reading the stops", e);
        }

        Set<String> toRemove = new HashSet<>();
        for (Entry<String, TraceEntry> traceEntry : traces.entrySet()) {
            TraceEntry trace = traceEntry.getValue();
            for (List<StopEntry> path : trace.stops) {
                for (int i = 0; i < path.size(); i++) {
                    StopEntry stop = path.get(i);
                    if (stop instanceof UnidentifiedStopEntry)
                        stop = resolve((UnidentifiedStopEntry) stop);
                    if (stop instanceof UnidentifiedStopEntry
                            && ((UnidentifiedStopEntry) stop).candidates.isEmpty()) {
                        LOGGER.severe("Missing stop for line " + trace.lname
                                + ". Line will be removed");
                        toRemove.add(traceEntry.getKey());
                        continue;
                    }
                    path.set(i, stop);
                }
            }
        }

        for (String string : toRemove) {
            traces.remove(string);
        }

        // Export content in required format
        try (FileWriter writer = new FileWriter(args[0], StandardCharsets.UTF_8)) {
            CSVWriterBuilder wBuilder = new CSVWriterBuilder(writer).withSeparator(';');
            try (ICSVWriter csv = wBuilder.build()) {
                for (Entry<String, TraceEntry> traceEntry : traces.entrySet()) {
                    Map<StopEntry, Set<StopEntry>> lineSegments = new HashMap<>();
                    String[] nextLine = new String[NetworkFormat.NUMBER_COLUMNS];
                    nextLine[NetworkFormat.LINE_INDEX] = traceEntry.getValue().lname;
                    for (List<StopEntry> path : traceEntry.getValue().stops) {
                        for (int i = 0; i < path.size() - 1; i++) {
                            StopEntry stop1 = path.get(i);
                            lineSegments.putIfAbsent(stop1, new HashSet<>());
                            StopEntry stop2 = path.get(i + 1);
                            if (!lineSegments.get(stop1).contains(stop2)) {
                                fillStation(stop1, nextLine, NetworkFormat.START_INDEX);
                                fillStation(stop2, nextLine, NetworkFormat.STOP_INDEX);
                                double distance = GPS.distance(stop1.latitude,
                                        stop1.longitude, stop2.latitude, stop2.longitude);
                                nextLine[NetworkFormat.DISTANCE_INDEX] = NumberFormat
                                        .getInstance(Locale.ENGLISH).format(distance);
                                nextLine[NetworkFormat.DURATION_INDEX] = formatTime(
                                        (long) Math.ceil(distanceToTime(distance)
                                                * SECONDS_IN_HOURS));
                                nextLine[NetworkFormat.VARIANT_INDEX] = Integer
                                        .toString(lineSegments.get(stop1).size());
                                csv.writeNext(nextLine);
                                lineSegments.get(stop1).add(stop2);
                            }
                        }
                    }

                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Could not write in file " + args[1]);
        }
    }

    /** @param distanceToTime
     * @return */
    private static String formatTime(long time) {
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setMinimumIntegerDigits(2);
        return MessageFormat.format("{0}:{1}", format.format(time / 60),
                format.format(time % 60));
    }

    /** A tool method to give a delay to go through a certain distance.
     * <p>
     * This is a model with an linear acceleration and deceleration periods and a
     * constant speed in between.
     * 
     * @param distance the distance (in km)
     * @return the duration of the trip (in hours) */
    private static double distanceToTime(double distance) {
        return Math.max(0, distance - 2 * ACCELERATION_DISTANCE) / MAX_SPEED
                + Math.pow(Math.min(distance, 2 * ACCELERATION_DISTANCE) / MAX_SPEED, 2);
    }

    /** @param stop1
     * @param nextLine
     * @param i */
    private static void fillStation(StopEntry stop, String[] nextLine, int index) {
        nextLine[index] = stop.lname;
        nextLine[index + 1] = MessageFormat.format("{0}, {1}",
                GPS_FORMATTER.format(stop.latitude),
                GPS_FORMATTER.format(stop.longitude));

    }

    /** @param stop
     * @return */
    private static StopEntry resolve(UnidentifiedStopEntry stop) {

        if (stop.candidates.isEmpty()) {
            LOGGER.severe("Unable to find stop name, will use a placeholder");
            return stop;
        }
        if (stop.candidates.size() == 1) {
            return stop.candidates.get(0);
        }
        Collections.sort(stop.candidates,
                (Comparator<? super StopEntry>) (StopEntry s1,
                        StopEntry s2) -> (int) Math.signum((GPS.distance(stop.latitude,
                                stop.longitude, s1.latitude, s1.longitude)
                                - GPS.distance(stop.latitude, stop.longitude, s2.latitude,
                                        s2.longitude))));

        return stop.candidates.get(0);
    }

    private static List<List<StopEntry>> buildPaths(String pathsJSON) {
        List<List<StopEntry>> all = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(pathsJSON);
            JSONArray paths = json.getJSONArray("coordinates");
            for (int i = 0; i < paths.length(); i++) {
                JSONArray path = paths.getJSONArray(i);
                List<StopEntry> stopsPath = new ArrayList<>();
                for (int j = 0; j < path.length(); j++) {
                    JSONArray coordinates = path.getJSONArray(j);

                    StopEntry entry = new UnidentifiedStopEntry(coordinates.getDouble(0),
                            coordinates.getDouble(1));

                    stopsPath.add(entry);
                }

                all.add(stopsPath);
            }
        } catch (JSONException e) {
            // Ignoring invalid element!
            LOGGER.log(Level.FINE, "Invalid json element " + pathsJSON, e); //$NON-NLS-1$
        }
        return all;
    }
}
