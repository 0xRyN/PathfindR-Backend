package fr.u_paris.gla.project.server.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The request for the pathfinder API to get the shortest path between 2 stations
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PathFinderRequest {
    @JsonProperty("graphId")
    private int graphId;

    @JsonProperty("stationFromId")
    private int stationFromId;

    @JsonProperty("stationToId")
    private int stationToId;
}
