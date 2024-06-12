package fr.u_paris.gla.project.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * The Node table in the database
 */
@Entity
@Table(name = "node")
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "outgoingArrows", "incomingArrows"})
public class Node {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "graph_id")
    private Graph graph;

    // The identifier of the line, for example line "A" or line "B"
    @Column(name = "line_id")
    private String lineId;

    // The station that this node represents.
    // While one node represents one station, one station can be represented by multiple nodes
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "station_id")
    private Station station;

    // The set of outgoing arrows that are directly reachable from this node
    @OneToMany(mappedBy = "from",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL})
    private Set<Edge> outgoingArrows = new HashSet<>();

    // All the arrows that have the current node as a target. This attribute is needed in order to extract a shortest path in the dijkstra algorithm
    @OneToMany(mappedBy = "to",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL})
    private Set<Edge> incomingArrows = new HashSet<>();


    public Node(String lineId) {
        this.lineId = lineId;
    }

    /**
     * Add an outgoing arrow to the node.
     *
     * @param edge the edge to add as an outgoing arrow
     */
    public void addOutgoingArrow(Edge edge) {
        this.outgoingArrows.add(edge);
    }

    /**
     * Add an incoming arrow to the node.
     *
     * @param edge the edge to add as an incoming arrow
     */
    public void addIncomingArrow(Edge edge) {
        this.incomingArrows.add(edge);
    }

    /**
     * Returns a unique identifier for the node.
     * The unique identifier is a concatenation of the line identifier, branch identifier and station name.
     *
     * @return the unique identifier of the node
     */
    public String getUniqueIdentifier() {
        return this.lineId  + "-" + this.station.getName();
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Node node = (Node) obj;
        return lineId.equals(node.lineId) && station.equals(node.station);
    }

    @Override
    public int hashCode() {
        return lineId.hashCode() + station.hashCode();
    }
    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", lineId='" + lineId + '\'' +
                '}';
    }
}
