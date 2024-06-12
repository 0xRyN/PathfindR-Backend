package fr.u_paris.gla.project.server.controller;

import fr.u_paris.gla.project.server.entity.Node;
import fr.u_paris.gla.project.server.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * REST controller for managing nodes.
 *
 * @author Tran Anh Duy NGUYEN
 * @version 1.0
 */
@RestController
@RequestMapping("/nodes")
public class NodeController {
    private NodeService nodeService;

    @Autowired
    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping
    public List<Node> getAllNodes() {
        return nodeService.findAllNodes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Node> getNodeById(@PathVariable String id) {
        return nodeService.findNodeById(id)
                .map(node -> ResponseEntity.ok().body(node))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Node createNode(@RequestBody Node node) {
        return nodeService.saveNode(node);
    }
}
