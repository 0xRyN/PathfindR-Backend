package fr.u_paris.gla.project.server.controller;

import fr.u_paris.gla.project.server.entity.Edge;
import fr.u_paris.gla.project.server.service.EdgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing edges.
 *
 * @author Tran Anh Duy NGUYEN
 * @version 1.0
 */
@RestController
@RequestMapping("/edges")
public class EdgeController {
    private EdgeService edgeService;

    @Autowired
    public EdgeController(EdgeService edgeService) {
        this.edgeService = edgeService;
    }

    @GetMapping
    public List<Edge> getAllEdges() {
        return edgeService.findAllEdges();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Edge> getEdgeById(@PathVariable Integer id) {
        return edgeService.findEdgeById(id)
                .map(edge -> ResponseEntity.ok().body(edge))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Edge createEdge(@RequestBody Edge edge) {
        return edgeService.saveEdge(edge);
    }
}
