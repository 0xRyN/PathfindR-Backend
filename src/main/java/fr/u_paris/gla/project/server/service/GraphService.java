package fr.u_paris.gla.project.server.service;

import fr.u_paris.gla.project.server.entity.Graph;
import fr.u_paris.gla.project.server.repository.GraphRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GraphService {
    private GraphRepository graphRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public GraphService(GraphRepository graphRepository, EntityManager entityManager) {
        this.graphRepository = graphRepository;
        this.entityManager = entityManager;
    }

    public List<Graph> findAllGraphs() {
        return graphRepository.findAll();
    }

    public Optional<Graph> findGraphById(Integer id) {
        return graphRepository.findById(id);
    }

    public Graph saveGraph(Graph graph) {
        return graphRepository.save(graph);
    }

    @Transactional
    public void resetPrimaryKey() {
        entityManager.createNativeQuery("ALTER TABLE graph ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }

    @Transactional
    public void deleteAllGraphsAndResetPrimaryKey() {
        graphRepository.deleteAll();
        resetPrimaryKey();
    }

}
