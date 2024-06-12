package fr.u_paris.gla.project.model;

import fr.u_paris.gla.project.utils.GPSCoordinates;

public class OldStation {
    private String name;
    private GPSCoordinates coordinates;

    public OldStation(String name) {
        this(name, new GPSCoordinates(0, 0));
    }

    public OldStation(String name, GPSCoordinates coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }

    public String getName() {
        return this.name;
    }

    public GPSCoordinates getCoordinates() {
        return this.coordinates;
    }

    public String toString() {
        return String.format("%s %s", name, coordinates);
    }

    //TODO: implement equals which returns equal if the name is the same and
    // the coordinates are *almost* the same (i.e. the difference is less than DIST_THRESHOLD)
}