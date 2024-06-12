package fr.u_paris.gla.project.server.service;

import fr.u_paris.gla.project.server.entity.Network;
import fr.u_paris.gla.project.server.repository.NetworkRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NetworkService {
    private NetworkRepository networkRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public NetworkService(NetworkRepository networkRepository, EntityManager entityManager) {
        this.networkRepository = networkRepository;
        this.entityManager = entityManager;
    }

    public List<Network> findAllNetworks() {
        return networkRepository.findAll();
    }

    public Optional<Network> findNetworkById(Integer id) {
        return networkRepository.findById(id);
    }

    public Network saveNetwork(Network network) {
        return networkRepository.save(network);
    }

    @Transactional
    public void resetPrimaryKey() {
        entityManager.createNativeQuery("ALTER TABLE network ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }

    @Transactional
    public void deleteAllNetworksAndResetPrimaryKey() {
        networkRepository.deleteAll();
        resetPrimaryKey();
    }
}
