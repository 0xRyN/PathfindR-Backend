package fr.u_paris.gla.project.parser;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import fr.u_paris.gla.project.model.*;
import fr.u_paris.gla.project.utils.GPSCoordinates;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class CSVNetworkParser {

    private static final Logger LOGGER = Logger
            .getLogger(CSVNetworkParser.class.getName());
    private final String networkCSV;
    private final String scheduleCSV;

    private final List<NetworkEntry> networkEntries;
    private final List<ScheduleEntry> scheduleEntries;

    private final Network network;


    /**
     * Creates a new CSVNetworkParser with the given network and schedule CSV files.
     *
     * @param networkCSV  the path to the network CSV file
     * @param scheduleCSV the path to the schedule CSV file
     */
    public CSVNetworkParser(String networkCSV, String scheduleCSV) {
        this.networkCSV = networkCSV;
        this.scheduleCSV = scheduleCSV;
        this.networkEntries = new ArrayList<>();
        this.scheduleEntries = new ArrayList<>();
        this.network = new Network(networkCSV);
    }

    /**
     * Parses a list of forks from a string in the format "[fork1, fork2, fork3]".
     *
     * @param forks the forks to parse
     * @return the parsed forks
     */
    private static List<Integer> parseForks(String forks) {
        List<Integer> result = new ArrayList<>();
        String[] split = forks.substring(1, forks.length() - 1).split(",");
        for (String s : split) {
            result.add(Integer.parseInt(s));
        }
        return result;
    }

    /**
     * Parses GPS coordinates from a string in the format "latitude,longitude".
     *
     * @param coords the coordinates to parse
     * @return the parsed coordinates
     */
    private static GPSCoordinates parseCoords(String coords) {
        String[] split = coords.split(",");
        return new GPSCoordinates(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
    }

    /**
     * Parses a time from the network CSV. The time is in the format MM:SS.
     * However, the minutes can be greater than 60, in which case they represent hours.
     *
     * @param time the time to parse
     * @return the parsed time
     */
    private static LocalTime parseNetworkTime(String time) {
        String[] split = time.split(":");
        int seconds = Integer.parseInt(split[1]);
        int minutes = Integer.parseInt(split[0]) % 60;
        int hours = Integer.parseInt(split[0]) / 60;
        return LocalTime.of(hours, minutes, seconds);
    }

    /**
     * Parses a time from the schedule CSV.
     *
     * @param time the time to parse
     * @return the parsed time
     */
    private static LocalTime parseScheduleTime(String time) {
        String[] split = time.split(":");
        return LocalTime.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    /**
     * Parses a row from the network CSV and creates a NetworkEntry object from it.
     *
     * @param row the row to parse
     */
    private static NetworkEntry findNetworkEntry(String[] row) {
        String lineName = row[0].strip();
        Integer lineDirection = Integer.parseInt(row[1].strip());
        String startStationName = row[2].strip();
        GPSCoordinates startCoords = parseCoords(row[3].strip());
        String endStationName = row[4].strip();
        GPSCoordinates endCoords = parseCoords(row[5].strip());
        LocalTime travelTime = parseNetworkTime(row[6].strip());
        Float distance = Float.parseFloat(row[7].strip());

        OldLine line = new OldLine(lineName, lineDirection);
        OldStation startStation = new OldStation(startStationName, startCoords);
        OldStation endStation = new OldStation(endStationName, endCoords);

        return new NetworkEntry(line, startStation, endStation, travelTime, distance);
    }

    /**
     * Parses a row from the schedule CSV and creates a ScheduleEntry object from it.
     *
     * @param row the row to parse
     */
    private void findScheduleEntry(String[] row) {
        OldLine line = new OldLine(row[0].strip());
        List<Integer> forks = parseForks(row[1].strip());
        OldStation startStation = new OldStation(row[2].strip());
        LocalTime time = parseScheduleTime(row[3].strip());

        ScheduleEntry entry = new ScheduleEntry(line, forks, startStation, time);
        scheduleEntries.add(entry);
    }

    /**
     * Processes a network entry and adds it to the network. Also creates the corresponding Station and Node objects if
     * they don't exist yet. Populates the Graph object with the new edge.
     *
     * @param entry         the network entry to process
     * @param knownStations the map of known stations
     * @param knownNodes    the map of known nodes
     */
    private void processNetworkEntry(NetworkEntry entry, Map<String, Station> knownStations, Map<String, Node> knownNodes) {
        // Create Station objects if they don't exist
        String fromStationName = entry.startStation().getName();
        GPSCoordinates fromStationCoords = entry.startStation().getCoordinates();
        String stationFromCompositeKey =
                fromStationName + "," + fromStationCoords.latitude() + "," + fromStationCoords.longitude();
        Station fromStation =
                knownStations.computeIfAbsent(stationFromCompositeKey, compositeKey -> new Station(fromStationName, fromStationCoords));

        String toStationName = entry.endStation().getName();
        GPSCoordinates toStationCoords = entry.endStation().getCoordinates();
        String stationToCompositeKey =
                toStationName + "," + toStationCoords.latitude() + "," + toStationCoords.longitude();
        Station toStation =
                knownStations.computeIfAbsent(stationToCompositeKey, compositeKey -> new Station(toStationName, toStationCoords));

        // Create Node objects if they don't exist
        String lineId = entry.line().getName();
        int branchId = entry.line().getDirection();

        String fromNodeId = new Node(lineId, fromStation).getUniqueIdentifier();
        Node fromNode = knownNodes.computeIfAbsent(fromNodeId, id -> new Node(lineId, fromStation));

        String toNodeId = new Node(lineId, toStation).getUniqueIdentifier();
        Node toNode = knownNodes.computeIfAbsent(toNodeId, id -> new Node(lineId, toStation));

        // Add the edge to the network
        float distance = entry.distance();
        LocalTime travelTime = entry.travelTime();

        Graph graph = this.network.getGraph();
        graph.addEdge(fromNode, toNode, branchId, distance, travelTime);
    }

    /**
     * Builds the network from the parsed CSV files.
     */
    private void buildNetwork() {
        final Map<String, Station> knownStations = new HashMap<>();
        final Map<String, Node> knownNodes = new HashMap<>();
        for (NetworkEntry entry : networkEntries) {
            processNetworkEntry(entry, knownStations, knownNodes);
        }
        LOGGER.info("Network built");
    }


    /**
     * Parses the network CSV file and creates NetworkEntry objects from it.
     *
     * @return this CSVNetworkParser
     */
    public CSVNetworkParser parseNetworkCSV() {
        CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(';')
                .build();

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(this.networkCSV))
                .withCSVParser(csvParser)
                .build()) {

            List<String[]> allRows = reader.readAll();

            for (String[] row : allRows) {
                NetworkEntry entry = findNetworkEntry(row);
                networkEntries.add(entry);
            }

        } catch (CsvException | IOException e) {
            LOGGER.severe("Error parsing network CSV");
            throw new RuntimeException(e);
        }

        LOGGER.info("Network CSV parsed");

        return this;
    }

    /**
     * Parses the schedule CSV file and creates ScheduleEntry objects from it.
     *
     * @return this CSVNetworkParser
     */
    public CSVNetworkParser parseScheduleCSV() {
        CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(';')
                .build();

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(this.scheduleCSV))
                .withCSVParser(csvParser)
                .build()) {

            List<String[]> allRows = reader.readAll();

            for (String[] row : allRows) {
                findScheduleEntry(row);
            }

        } catch (IOException | CsvException e) {
            LOGGER.severe("Error parsing schedule CSV");
            throw new RuntimeException(e);
        }

        LOGGER.info("Schedule CSV parsed");

        return this;
    }

    /**
     * Parses both the network and schedule CSV files.
     *
     * @return this CSVNetworkParser
     */
    public CSVNetworkParser parseAll() {
        // Benchmark time
        long start = System.currentTimeMillis();
        parseNetworkCSV();
        long networkParsingTime = System.currentTimeMillis();
        parseScheduleCSV();
        long scheduleParsingTime = System.currentTimeMillis();
        String message = String
                .format("Parsing done in %d ms (network: %d ms, schedule: %d ms)",
                        scheduleParsingTime - start,
                        networkParsingTime - start,
                        scheduleParsingTime - networkParsingTime);
        LOGGER.info(message);

        long networkBuild = System.currentTimeMillis();
        buildNetwork();
        long networkBuildTime = System.currentTimeMillis();
        LOGGER.info(String.format("Network built in %d ms", networkBuildTime - networkBuild));
        return this;
    }

    public List<NetworkEntry> getNetworkEntries() {
        return networkEntries;
    }

    public List<ScheduleEntry> getScheduleEntries() {
        return scheduleEntries;
    }

    public Network getNetwork() {
        return network;
    }
}
