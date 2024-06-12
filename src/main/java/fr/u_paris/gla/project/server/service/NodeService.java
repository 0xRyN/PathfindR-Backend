package fr.u_paris.gla.project.server.service;

import fr.u_paris.gla.project.server.entity.Node;
import fr.u_paris.gla.project.server.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NodeService {
    private NodeRepository nodeRepository;

    @Autowired
    public NodeService(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    public List<Node> findAllNodes() {
        return nodeRepository.findAll();
    }

    public Optional<Node> findNodeById(String id) {
        return nodeRepository.findById(id);
    }

    public List<Node> findNodeByStationId(int stationId) {
        return nodeRepository.findByStation_Id(stationId);
    }

    public List<Node> findNodeByGraphId(int graphId) {
        return nodeRepository.findByGraphId(graphId);
    }

    public List<Node> findNodeByGraphIdAndStationId(int graphId, int stationId) {
        return nodeRepository.findByGraphIdAndStationId(graphId, stationId);
    }

    public Node saveNode(Node node) {
        return nodeRepository.save(node);
    }

    public void deleteAllNodes() {
        nodeRepository.deleteAll();
    }
}
