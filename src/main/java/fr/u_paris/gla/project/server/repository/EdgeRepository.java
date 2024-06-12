package fr.u_paris.gla.project.server.repository;

import fr.u_paris.gla.project.server.entity.Edge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EdgeRepository extends JpaRepository<Edge,Integer> {
    List<Edge> findByGraphId(int graphId);
}
