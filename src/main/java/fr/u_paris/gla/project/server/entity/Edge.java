package fr.u_paris.gla.project.server.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Objects;

/**
 * The Edge table in the database
 */
@Entity
@Table(name = "edge")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Edge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // The identifier of the branch inside a line, for example branch 0 or branch 1
    @Column(name = "branch_id")
    private int branchId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "graph_id")
    private Graph graph;

    // The from and to nodes are the two nodes connected by the edge
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "node_from_id")
    private Node from;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "node_to_id")
    private Node to;

    // Distance is in kilometers, float is enough because it's a small number
    private float distance;

    // Travel time between the two nodes
    @Column(name = "travel_time", columnDefinition = "TIME")
    private LocalTime travelTime;

    public Edge(int branchId, float distance, LocalTime travelTime) {
        this.branchId = branchId;
        this.distance = distance;
        this.travelTime = travelTime;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "id=" + id +
                ", branchId=" + branchId +
                ", distance=" + distance +
                ", travelTime=" + travelTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return branchId == edge.branchId && Objects.equals(graph, edge.graph) && Objects.equals(from, edge.from) && Objects.equals(to, edge.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(branchId, graph, from, to);
    }
}
