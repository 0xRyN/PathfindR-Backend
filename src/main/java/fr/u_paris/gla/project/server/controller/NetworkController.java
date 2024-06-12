package fr.u_paris.gla.project.server.controller;

import fr.u_paris.gla.project.model.Edge;
import fr.u_paris.gla.project.model.Node;
import fr.u_paris.gla.project.parser.CSVNetworkParser;

import fr.u_paris.gla.project.server.entity.Network;
import fr.u_paris.gla.project.server.entity.Station;
import fr.u_paris.gla.project.server.service.*;
import fr.u_paris.gla.project.utils.GPS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalTime;
import java.util.List;

@EnableWebMvc
@RestController
@RequestMapping("/networks")
public class NetworkController {
    private NetworkService networkService;
    private GraphService graphService;
    private StationService stationService;
    private NodeService nodeService;
    private EdgeService edgeService;

    @Value("${upload.dir}")
    private String uploadDir;

    // The average walking speed is 1.42 m/s, or 5.1 km/h
    private final double averageWalkingSpeed = 5.1;

    // The maximum walking distance is 500m
    private final double maxWalkingDistance = 0.5;

    @Autowired
    public NetworkController(NetworkService networkService, GraphService graphService,
                             StationService stationService, NodeService nodeService,
                             EdgeService edgeService) {
        this.networkService = networkService;
        this.graphService = graphService;
        this.stationService = stationService;
        this.nodeService = nodeService;
        this.edgeService = edgeService;
    }

    @GetMapping
    public List<Network> getAllNetworks() {
        return networkService.findAllNetworks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Network> getNetworkById(@PathVariable Integer id) {
        return networkService.findNetworkById(id)
                .map(network -> ResponseEntity.ok().body(network))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Network> createNetwork(@RequestBody Network networkRequest) {
        final String networkCSV = uploadDir + "/" + "network.csv";
        final String scheduleCSV = uploadDir + "/" + "schedule.csv";

        try {
            CSVNetworkParser parser = new CSVNetworkParser(networkCSV, scheduleCSV);
            parser.parseAll();

            List<Node> nodesModel = parser.getNetwork().getGraph().getNodes().stream().toList();
            List<Edge> edgesModel = parser.getNetwork().getGraph().getEdges().stream().toList();

            // delete the current network and also all of its components,
            // as the current version of application can only have 1 network at a time
            deleteCurrentNetwork();

            /*
             * Création du graph
             *
             */
            fr.u_paris.gla.project.server.entity.Graph graphEntity = new fr.u_paris.gla.project.server.entity.Graph();
            graphEntity = graphService.saveGraph(graphEntity);

            /*
             * Création du network
             *
             */
            Network networkEntity = new Network();
            networkEntity.setName(networkRequest.getName());
            networkEntity.setGraph(graphEntity);
            networkService.saveNetwork(networkEntity);

            /*
             * Ajout des nodes et des stations
             *
             */
            addNodesAndStations(nodesModel, graphEntity);

            /*
             * Ajout des edges
             *
             */
            addEdges(edgesModel, graphEntity);

            /*
             * Ajout des arêtes pour les nœuds dans la même station ou dans les stations proches (dans un rayon de 500 mètres maximum).
             *
             */
            addEdgesForNodesInSameStationOrInNearbyStations(nodesModel, graphEntity);


            return ResponseEntity.ok().body(networkEntity);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops! Something went wrong on our end. Our team has been notified and we are working to fix it. Please try again later.");
        }
    }

    public LocalTime calculateWalkingTimeBetweenNodesInAStation(Node nodeFrom, Node nodeTo) {
        // constant for the travel time to avoid looping in Dijkstra's algorithm (minimum is 5 seconds)
        // we need to define this because 2 nodes in the same station can have the same coordinates,
        // which lead to a 0 time of walking and can cause loop in Dijkstra's algorithm
        LocalTime loopAvoidanceTravelTime = LocalTime.of(0, 0, 5);
        double distance = GPS.distance(nodeFrom.getStation().getCoordinates().latitude(),
                nodeFrom.getStation().getCoordinates().longitude(),
                nodeTo.getStation().getCoordinates().latitude(),
                nodeTo.getStation().getCoordinates().longitude());

        // calculate the travel time based on the distance
        double travelTimeInSeconds = distance / averageWalkingSpeed * 3600;

        LocalTime calculatedTravelTime = LocalTime.ofSecondOfDay((long) travelTimeInSeconds);

        return calculatedTravelTime.isAfter(loopAvoidanceTravelTime) ?
                calculatedTravelTime : loopAvoidanceTravelTime;
    }

    public LocalTime calculateWalkingTimeFromDistance(double distance) {

        // calculate the travel time based on the distance, convert it to seconds
        double travelTimeInSeconds = distance / averageWalkingSpeed * 3600;

        return LocalTime.ofSecondOfDay((long) travelTimeInSeconds);
    }

    /**
     * A method used to convert the node models to node entities and save the nodes within their respective stations to the database.
     *
     */
    private void addNodesAndStations(List<Node> nodesModel, fr.u_paris.gla.project.server.entity.Graph graphEntity) {
        for (Node n : nodesModel) {
            fr.u_paris.gla.project.server.entity.Node nodeEntity = new fr.u_paris.gla.project.server.entity.Node();
            nodeEntity.setGraph(graphEntity);
            nodeEntity.setId(n.getUniqueIdentifier());
            nodeEntity.setLineId(n.getLineId());

            List<Station> stationsList = stationService.findStationByNameAndCoordinates(n.getStation().getName(),
                    n.getStation().getCoordinates().latitude(),
                    n.getStation().getCoordinates().longitude());
            Station station = new Station();
            if (stationsList.isEmpty()) {
                station.setName(n.getStation().getName());
                station.setLatitude(n.getStation().getCoordinates().latitude());
                station.setLongitude(n.getStation().getCoordinates().longitude());
                stationService.saveStation(station);
            } else {
                station = stationsList.get(0);
            }
            nodeEntity.setStation(station);
            nodeService.saveNode(nodeEntity);
        }
    }

    /**
     * A method used to convert the edges models to edges entities and save the edges to the database.
     *
     */
    private void addEdges(List<Edge> edgesModel, fr.u_paris.gla.project.server.entity.Graph graphEntity) {
        for (Edge e : edgesModel) {
            fr.u_paris.gla.project.server.entity.Edge edgeEntity =
                    new fr.u_paris.gla.project.server.entity.Edge();

            Node nodeFrom = e.getFrom();
            fr.u_paris.gla.project.server.entity.Node nodeFromEntity =
                    nodeService.findNodeById(nodeFrom.getUniqueIdentifier()).get();
            edgeEntity.setFrom(nodeFromEntity);

            Node nodeTo = e.getTo();
            fr.u_paris.gla.project.server.entity.Node nodeToEntity =
                    nodeService.findNodeById(nodeTo.getUniqueIdentifier()).get();
            edgeEntity.setTo(nodeToEntity);

            edgeEntity.setGraph(graphEntity);
            edgeEntity.setBranchId(e.getBranchId());
            edgeEntity.setDistance(e.getDistance());
            edgeEntity.setTravelTime(e.getTravelTime());

            nodeFromEntity.addOutgoingArrow(edgeEntity);
            nodeToEntity.addIncomingArrow(edgeEntity);

            nodeService.saveNode(nodeFromEntity);
            nodeService.saveNode(nodeToEntity);
        }
    }

    /**
     * A method used to save the edges between nodes in the same station or in nearby stations.
     *
     */
    private void addEdgesForNodesInSameStationOrInNearbyStations(List<Node> nodesModel, fr.u_paris.gla.project.server.entity.Graph graphEntity) {

        for (int i = 0; i < nodesModel.size(); i++) {
            Node nodeFrom = nodesModel.get(i);
            fr.u_paris.gla.project.server.entity.Node nodeFromEntity =
                    nodeService.findNodeById(nodeFrom.getUniqueIdentifier()).get();
            for (int j = i + 1; j < nodesModel.size(); j++) {
                Node nodeTo = nodesModel.get(j);
                fr.u_paris.gla.project.server.entity.Node nodeToEntity =
                        nodeService.findNodeById(nodeTo.getUniqueIdentifier()).get();

                // if 2 nodes are in the same station
                if (nodeFrom.getStation().equals(nodeTo.getStation())) {
                    // if two nodes are in the same station, then there will be an arrow connecting one node to the other
                    // to represent movement within the station
                    fr.u_paris.gla.project.server.entity.Edge edgeEntity1 =
                            new fr.u_paris.gla.project.server.entity.Edge();
                    fr.u_paris.gla.project.server.entity.Edge edgeEntity2 =
                            new fr.u_paris.gla.project.server.entity.Edge();

                    edgeEntity1.setFrom(nodeFromEntity);
                    edgeEntity2.setTo(nodeFromEntity);

                    edgeEntity1.setTo(nodeToEntity);
                    edgeEntity2.setFrom(nodeToEntity);

                    edgeEntity1.setGraph(graphEntity);
                    edgeEntity1.setDistance(0);

                    edgeEntity2.setGraph(graphEntity);
                    edgeEntity2.setDistance(0);

                    LocalTime walkingTimeBetweenNodesInSameStation =
                            calculateWalkingTimeBetweenNodesInAStation(nodeFrom, nodeTo);

                    edgeEntity1.setTravelTime(walkingTimeBetweenNodesInSameStation);
                    edgeEntity2.setTravelTime(walkingTimeBetweenNodesInSameStation);

                    nodeFromEntity.addOutgoingArrow(edgeEntity1);
                    nodeFromEntity.addIncomingArrow(edgeEntity2);
                    nodeToEntity.addIncomingArrow(edgeEntity1);
                    nodeToEntity.addOutgoingArrow(edgeEntity2);

                    nodeService.saveNode(nodeFromEntity);
                    nodeService.saveNode(nodeToEntity);
                } else {
                    // calculate the distance between 2 nodes
                    double distance = GPS.distance(nodeFrom.getStation().getCoordinates().latitude(),
                            nodeFrom.getStation().getCoordinates().longitude(),
                            nodeTo.getStation().getCoordinates().latitude(),
                            nodeTo.getStation().getCoordinates().longitude());

                    // if 2 nodes are in different stations, but they are close
                    if (distance <= maxWalkingDistance) {
                        fr.u_paris.gla.project.server.entity.Edge edgeEntity1 =
                                new fr.u_paris.gla.project.server.entity.Edge();
                        fr.u_paris.gla.project.server.entity.Edge edgeEntity2 =
                                new fr.u_paris.gla.project.server.entity.Edge();

                        edgeEntity1.setFrom(nodeFromEntity);
                        edgeEntity2.setTo(nodeFromEntity);

                        edgeEntity1.setTo(nodeToEntity);
                        edgeEntity2.setFrom(nodeToEntity);

                        edgeEntity1.setGraph(graphEntity);
                        edgeEntity1.setDistance(0);

                        edgeEntity2.setGraph(graphEntity);
                        edgeEntity2.setDistance(0);

                        LocalTime walkingTime =
                                calculateWalkingTimeFromDistance(distance);

                        edgeEntity1.setTravelTime(walkingTime);
                        edgeEntity2.setTravelTime(walkingTime);

                        nodeFromEntity.addOutgoingArrow(edgeEntity1);
                        nodeFromEntity.addIncomingArrow(edgeEntity2);
                        nodeToEntity.addIncomingArrow(edgeEntity1);
                        nodeToEntity.addOutgoingArrow(edgeEntity2);

                        nodeService.saveNode(nodeFromEntity);
                        nodeService.saveNode(nodeToEntity);
                    }
                }
            }
        }
    }

    /**
     * A method used to delete the current network along with all of its components
     * In future versions, when the application supports multiple networks (e.g., Paris, Lyon),
     * this method may become obsolete.
     *
     */
    private void deleteCurrentNetwork() {
        if (!networkService.findAllNetworks().isEmpty()) {
            stationService.deleteAllStationsAndResetPrimaryKey();
            nodeService.deleteAllNodes();
            edgeService.deleteAllEdgesAndResetPrimaryKey();
            graphService.deleteAllGraphsAndResetPrimaryKey();
            networkService.deleteAllNetworksAndResetPrimaryKey();
        }
    }
}
