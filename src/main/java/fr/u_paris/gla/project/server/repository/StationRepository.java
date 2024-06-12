package fr.u_paris.gla.project.server.repository;

import fr.u_paris.gla.project.server.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends JpaRepository<Station, Integer> {
    List<Station> findByNameAndLatitudeAndLongitude(String name, double latitude, double longitude);

}
