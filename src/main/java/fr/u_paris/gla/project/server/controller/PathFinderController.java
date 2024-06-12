package fr.u_paris.gla.project.server.controller;

import fr.u_paris.gla.project.core.shortest_path_finder.DijkstraPathFinder;
import fr.u_paris.gla.project.core.shortest_path_finder.INode;
import fr.u_paris.gla.project.core.shortest_path_finder.PathNotFoundException;
import fr.u_paris.gla.project.server.entity.*;
import fr.u_paris.gla.project.server.service.*;
import fr.u_paris.gla.project.utils.GPSCoordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for path-finding process.
 *
 * @author Tran Anh Duy NGUYEN
 * @version 1.0
 */
@RestController
@RequestMapping("/path-finder")
public class PathFinderController {
    private GraphService graphService;
    private NodeService nodeService;
    private EdgeService edgeService;

    @Autowired
    public PathFinderController(GraphService graphService, NodeService nodeService, EdgeService edgeService) {
        this.graphService = graphService;
        this.nodeService = nodeService;
        this.edgeService = edgeService;
    }

    @PostMapping
    public ResponseEntity<PathFinderResponse> getShortestPath(@RequestBody PathFinderRequest pathFinderRequest) {

        Optional<Graph> graphEntity = graphService.findGraphById(pathFinderRequest.getGraphId());

        if (graphEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The graph that you provided doesn't exist");
        }

        List<INode> listShortestPath = null;
        int shortestPathCost = Integer.MAX_VALUE;

        try {

            List<Node> nodesSourceEntity =
                    nodeService.findNodeByGraphIdAndStationId(graphEntity.get().getId(), pathFinderRequest.getStationFromId());
            List<Node> nodesTargetEntity =
                    nodeService.findNodeByGraphIdAndStationId(graphEntity.get().getId(), pathFinderRequest.getStationToId());

            for (Node nodeSourceEntity : nodesSourceEntity) {
                for (Node nodeTargetEntity : nodesTargetEntity) {
                    Station stationSourceEntity = nodeSourceEntity.getStation();
                    fr.u_paris.gla.project.model.Station stationSourceModel =
                            new fr.u_paris.gla.project.model.Station(stationSourceEntity.getId(), stationSourceEntity.getName(), new GPSCoordinates(stationSourceEntity.getLatitude(), stationSourceEntity.getLongitude()));
                    fr.u_paris.gla.project.model.Node nodeSourceModel =
                            new fr.u_paris.gla.project.model.Node(nodeSourceEntity.getLineId(), stationSourceModel);

                    Station stationTargetEntity = nodeTargetEntity.getStation();
                    fr.u_paris.gla.project.model.Station stationTargetModel =
                            new fr.u_paris.gla.project.model.Station(stationTargetEntity.getId(), stationTargetEntity.getName(), new GPSCoordinates(stationTargetEntity.getLatitude(), stationTargetEntity.getLongitude()));
                    fr.u_paris.gla.project.model.Node nodeTargetModel =
                            new fr.u_paris.gla.project.model.Node(nodeTargetEntity.getLineId(), stationTargetModel);

                    fr.u_paris.gla.project.model.Graph graphModel = new fr.u_paris.gla.project.model.Graph();
                    graphModel.addNode(nodeSourceModel);
                    graphModel.addNode(nodeTargetModel);

                    List<Node> nodesEntity = nodeService.findNodeByGraphId(graphEntity.get().getId());
                    List<Edge> edgesEntity = edgeService.findEdgeByGraphId(graphEntity.get().getId());

                    // add nodes to graph model
                    addNodeToGraphModel(nodesEntity, graphModel);

                    // add edges to graph model
                    addEdgesToGraphModel(edgesEntity, graphModel);

                    List<INode> listPath =
                            DijkstraPathFinder.computeShortestPath(graphModel, nodeSourceModel, nodeTargetModel);
                    if (listPath != null) {
                        int pathCost =
                                DijkstraPathFinder.computeShortestPathCost(graphModel, nodeSourceModel, nodeTargetModel);

                        // updates the shortest path if the current path's distance is shorter, or if the distance is equal
                        // and it requires passing through fewer stations.
                        if (pathCost < shortestPathCost || (pathCost == shortestPathCost
                                && listShortestPath != null && listPath.size() < listShortestPath.size())) {
                            shortestPathCost = pathCost;
                            listShortestPath = listPath;
                        }
                    }
                }
            }

            if (shortestPathCost == -1 || listShortestPath == null) {
                throw new PathNotFoundException("Cannot find the shortest path");
            }

            return ResponseEntity.ok().body(new PathFinderResponse(listShortestPath, shortestPathCost));

        } catch (PathNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops! Something went wrong on our end. Our team has been notified and we are working to fix it. Please try again later.");
        }

    }

    /**
     * A method used to convert the node entities in database to node models
     * and add nodes for the graph model (this graph model will be used in the shortest path finding algorithm).
     *
     */
    private void addNodeToGraphModel(List<Node> nodesEntity, fr.u_paris.gla.project.model.Graph graphModel) {
        for (Node nodeEntity : nodesEntity) {
            Station stationEntity = nodeEntity.getStation();
            fr.u_paris.gla.project.model.Station stationModel =
                    new fr.u_paris.gla.project.model.Station(stationEntity.getId(), stationEntity.getName(), new GPSCoordinates(stationEntity.getLatitude(), stationEntity.getLongitude()));
            fr.u_paris.gla.project.model.Node nodeModel =
                    new fr.u_paris.gla.project.model.Node(nodeEntity.getLineId(), stationModel);
            graphModel.addNode(nodeModel);
        }
    }

    /**
     * A method used to convert the edge entities in database to edge models
     * and add edges for the graph model (this graph model will be used in the shortest path finding algorithm).
     *
     */
    private void addEdgesToGraphModel(List<Edge> edgesEntity, fr.u_paris.gla.project.model.Graph graphModel) {
        for (Edge edgeEntity : edgesEntity) {
            Station stationFromEntity = edgeEntity.getFrom().getStation();
            fr.u_paris.gla.project.model.Station stationFromModel =
                    new fr.u_paris.gla.project.model.Station(stationFromEntity.getId(), stationFromEntity.getName(), new GPSCoordinates(stationFromEntity.getLatitude(), stationFromEntity.getLongitude()));
            fr.u_paris.gla.project.model.Node nodeFromModel = null;
            for (fr.u_paris.gla.project.model.Node node : graphModel.getNodes()) {
                if (Objects.equals(edgeEntity.getFrom().getLineId(), node.getLineId()) && stationFromModel.equals(node.getStation())) {
                    nodeFromModel = node;
                    break;
                }
            }

            Station stationToEntity = edgeEntity.getTo().getStation();
            fr.u_paris.gla.project.model.Station stationToModel =
                    new fr.u_paris.gla.project.model.Station(stationToEntity.getId(), stationToEntity.getName(), new GPSCoordinates(stationToEntity.getLatitude(), stationToEntity.getLongitude()));
            fr.u_paris.gla.project.model.Node nodeToModel = null;
            for (fr.u_paris.gla.project.model.Node node : graphModel.getNodes()) {
                if (Objects.equals(edgeEntity.getTo().getLineId(), node.getLineId()) && stationToModel.equals(node.getStation())) {
                    nodeToModel = node;
                    break;
                }
            }

            graphModel.addEdge(nodeFromModel, nodeToModel, edgeEntity.getBranchId(), edgeEntity.getDistance(), edgeEntity.getTravelTime());
        }
    }


}