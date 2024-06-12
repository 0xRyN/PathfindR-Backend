package fr.u_paris.gla.project.model;

import fr.u_paris.gla.project.utils.GPSCoordinates;


/**
 * Represents a station in the metro network.
 * A station has a name and GPS coordinates.
 * The name is unique.
 * The GPS coordinates are used to calculate the distance between two stations.
 */
public class Station {

    // The id of the station
    private static int nextId = 0;
    private final int id;

    // The name of the station
    private final String name;

    // The GPS coordinates of the station (latitude and longitude)
    private final GPSCoordinates coordinates;

    /**
     * Creates a new station with the given id, name and coordinates.
     *
     * @param id          the id of the station
     * @param name        the name of the station
     * @param coordinates the GPS coordinates of the station
     */
    public Station(int id, String name, GPSCoordinates coordinates) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
    }

    /**
     * Creates a new station with the given name and coordinates.
     *
     * @param name        the name of the station
     * @param coordinates the GPS coordinates of the station
     */
    public Station(String name, GPSCoordinates coordinates) {
        this.id = nextId++;
        this.name = name;
        this.coordinates = coordinates;
    }

    public String getName() {
        return this.name;
    }

    public GPSCoordinates getCoordinates() {
        return this.coordinates;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Station %s %s", name, coordinates);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Station)) {
            return false;
        }
        Station other = (Station) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
