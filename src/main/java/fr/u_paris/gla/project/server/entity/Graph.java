package fr.u_paris.gla.project.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * The Graph table in the database
 */
@Entity
@Table(name = "graph")
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "nodes", "edges"})
public class Graph {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // The set of nodes in the graph
    @OneToMany(mappedBy = "graph",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL})
    private Set<Node> nodes = new HashSet<>();

    // The set of edges in the graph
    @OneToMany(mappedBy = "graph",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL})
    private Set<Edge> edges = new HashSet<>();

    /**
     * Adds a node to the graph.
     *
     * @param node the node to add to the graph
     */
    public void addNode(Node node) {
        this.nodes.add(node);
    }


    /**
     * Adds an edge to the graph. Also adds the two nodes to the graph if they are not already in it.
     *
     * @param from       the node from which the edge starts
     * @param to         the node to which the edge goes
     * @param branchId     the branch identifier
     * @param distance   the distance between the two nodes
     * @param travelTime the travel time between the two nodes
     */
    public void addEdge(Node from, Node to, int branchId, float distance, LocalTime travelTime) {
        Edge edge = new Edge(branchId, distance, travelTime);
        edge.setFrom(from);
        edge.setTo(to);

        this.edges.add(edge);
        from.addOutgoingArrow(edge);
        to.addIncomingArrow(edge);

        // Add the nodes to the graph if they are not already in it
        this.nodes.add(from);
        this.nodes.add(to);
    }

    @Override
    public String toString() {
        return "Graph{" +
                "id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graph graph = (Graph) o;
        return id == graph.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
