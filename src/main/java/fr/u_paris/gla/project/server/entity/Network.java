package fr.u_paris.gla.project.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Network table in the database
 */
@Entity
@Table(name = "network")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Network {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // The name of the network
    @Column(name = "name")
    private String name;

    // The graph representing the network
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "graph_id")
    private Graph graph;

    public Network(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Network{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
