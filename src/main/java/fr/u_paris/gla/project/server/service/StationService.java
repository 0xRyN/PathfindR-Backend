package fr.u_paris.gla.project.server.service;

import fr.u_paris.gla.project.server.entity.Station;
import fr.u_paris.gla.project.server.repository.StationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {
    private StationRepository stationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public StationService(StationRepository stationRepository, EntityManager entityManager) {
        this.stationRepository = stationRepository;
        this.entityManager = entityManager;
    }

    public List<Station> findAllStations() {
        return stationRepository.findAll();
    }

    public Optional<Station> findStationById(Integer id) {
        return stationRepository.findById(id);
    }

    public List<Station> findStationByNameAndCoordinates(String name, double latitude, double longitude) {
        return stationRepository.findByNameAndLatitudeAndLongitude(name, latitude, longitude);
    }

    public Station saveStation(Station station) {
        return stationRepository.save(station);
    }

    @Transactional
    public void resetPrimaryKey() {
        entityManager.createNativeQuery("ALTER TABLE station ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }

    @Transactional
    public void deleteAllStationsAndResetPrimaryKey() {
        stationRepository.deleteAll();
        resetPrimaryKey();
    }
}
