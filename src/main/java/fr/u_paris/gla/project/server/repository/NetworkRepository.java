package fr.u_paris.gla.project.server.repository;

import fr.u_paris.gla.project.server.entity.Network;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NetworkRepository extends JpaRepository<Network, Integer> {

}
