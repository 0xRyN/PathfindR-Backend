package fr.u_paris.gla.project.server.controller;

import fr.u_paris.gla.project.server.entity.Node;
import fr.u_paris.gla.project.server.entity.Station;
import fr.u_paris.gla.project.server.service.NodeService;
import fr.u_paris.gla.project.server.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing stations.
 *
 * @author Tran Anh Duy NGUYEN
 * @version 1.0
 */
@RestController
@RequestMapping("/stations")
public class StationController {
    private StationService stationService;
    private NodeService nodeService;

    @Autowired
    public StationController(StationService stationService, NodeService nodeService) {
        this.stationService = stationService;
        this.nodeService = nodeService;
    }

    @GetMapping("/{stationId}/lines")
    public List<String> getLines(@PathVariable() String stationId) {
        Station station = stationService.findStationById(Integer.parseInt(stationId)).get();
        System.out.println("station finded"+station.getName());
       List<Node> nodes = nodeService.findNodeByStationId(station.getId());
       List<String>  list = new ArrayList<>();
       for (int i = 0;i<nodes.size(); i++) {
           list.add(nodes.get(i).getLineId());
       }
       return list;
    }

    @GetMapping
    public List<Station> getAllStations() {
        return stationService.findAllStations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> getStationById(@PathVariable Integer id) {
        return stationService.findStationById(id)
                .map(station -> ResponseEntity.ok().body(station))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Station createStation(@RequestBody Station station) {
        return stationService.saveStation(station);
    }
}
