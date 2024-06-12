package fr.u_paris.gla.project.parser;

import fr.u_paris.gla.project.model.OldLine;
import fr.u_paris.gla.project.model.OldStation;

import java.time.LocalTime;

// Float is used instead of Double because the distance is in kilometers (small numbers)
public record NetworkEntry(OldLine line, OldStation startStation, OldStation endStation,
                           LocalTime travelTime, Float distance) {
    public String toString() {
        return String.format("%s %s -> %s [%s minutes, %s km]", line, startStation, endStation, travelTime, distance);
    }
}

