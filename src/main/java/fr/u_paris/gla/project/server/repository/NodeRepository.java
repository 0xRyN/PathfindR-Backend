package fr.u_paris.gla.project.server.repository;

import fr.u_paris.gla.project.server.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NodeRepository extends JpaRepository<Node,String> {

    List<Node> findByStation_Id(int stationId);
    List<Node> findByGraphId(int graphId);
    List<Node> findByGraphIdAndStationId(int graphId, int stationId);

}
