package fr.u_paris.gla.project.utils;

public record GPSCoordinates(double latitude, double longitude) {
    public String toString() {
        return String.format("(%f, %f)", latitude, longitude);
    }
}
