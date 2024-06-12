package fr.u_paris.gla.project.server.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import fr.u_paris.gla.project.core.shortest_path_finder.INode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * The request for the pathfinder API to get the shortest path between 2 stations
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PathFinderResponse {
    @JsonProperty("listNodes")
    private List<INode> listNodes;

    @JsonProperty("totalTime")
    private int totalTime;
}
