package fr.u_paris.gla.project.server.service;

import fr.u_paris.gla.project.server.entity.Edge;
import fr.u_paris.gla.project.server.repository.EdgeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EdgeService {
    private EdgeRepository edgeRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public EdgeService(EdgeRepository edgeRepository, EntityManager entityManager) {
        this.edgeRepository = edgeRepository;
        this.entityManager = entityManager;
    }

    public List<Edge> findAllEdges() {
        return edgeRepository.findAll();
    }

    public Optional<Edge> findEdgeById(Integer id) {
        return edgeRepository.findById(id);
    }
    public List<Edge> findEdgeByGraphId(Integer id) {
        return edgeRepository.findByGraphId(id);
    }

    public Edge saveEdge(Edge edge) {
        return edgeRepository.save(edge);
    }

    @Transactional
    public void resetPrimaryKey() {
        entityManager.createNativeQuery("ALTER TABLE edge ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }

    @Transactional
    public void deleteAllEdgesAndResetPrimaryKey() {
        edgeRepository.deleteAll();
        resetPrimaryKey();
    }
}
