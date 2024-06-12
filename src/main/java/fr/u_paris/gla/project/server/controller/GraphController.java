package fr.u_paris.gla.project.server.controller;

import fr.u_paris.gla.project.server.entity.Graph;
import fr.u_paris.gla.project.server.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing graphs.
 *
 * @author Tran Anh Duy NGUYEN
 * @version 1.0
 */
@RestController
@RequestMapping("/graphs")
public class GraphController {
    private GraphService graphService;

    @Autowired
    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping
    public List<Graph> getAllGraphs() {
        return graphService.findAllGraphs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Graph> getGraphById(@PathVariable Integer id) {
        return graphService.findGraphById(id)
                .map(graph -> ResponseEntity.ok().body(graph))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Graph createGraph(@RequestBody Graph graph) {
        return graphService.saveGraph(graph);
    }
}
