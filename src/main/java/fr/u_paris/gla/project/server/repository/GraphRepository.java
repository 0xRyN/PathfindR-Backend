package fr.u_paris.gla.project.server.repository;

import fr.u_paris.gla.project.server.entity.Graph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GraphRepository  extends JpaRepository<Graph,Integer> {

}
