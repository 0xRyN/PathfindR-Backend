package fr.u_paris.gla.project.parser;

import fr.u_paris.gla.project.model.OldLine;
import fr.u_paris.gla.project.model.OldStation;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public record ScheduleEntry(OldLine line, List<Integer> forks, OldStation station, LocalTime time) {
    public String toString() {
        String forksString = forks.stream().map(Object::toString).collect(Collectors.joining(" -> "));
        return String.format("%s, Bifurcations %s, Départ de %s à %s heures", line, forksString, station, time);
    }
}