package fr.u_paris.gla.project.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


/**
 * The Station table in the database
 * Represents a station in the metro network.
 * A station has a name and GPS coordinates.
 * The name is unique.
 * The GPS coordinates are used to calculate the distance between two stations.
 */
@Entity
@Table(name = "station")
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "nodes"})
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // The name of the station
    @Column(name = "name")
    private String name;

    // The GPS coordinates of the station (latitude and longitude)
    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    // The set of nodes in the graph
    @OneToMany(mappedBy = "station",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL})
    private Set<Node> nodes = new HashSet<>();

    public Station(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof fr.u_paris.gla.project.model.Station)) {
            return false;
        }
        Station other = (Station) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
