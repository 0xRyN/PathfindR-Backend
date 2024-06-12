package fr.u_paris.gla.project.model;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import fr.u_paris.gla.project.core.shortest_path_finder.IGraph;
import fr.u_paris.gla.project.core.shortest_path_finder.INode;

public class Graph implements IGraph {

    // The set of nodes in the graph
    private final Set<Node> nodes;

    // The set of edges in the graph
    private final Set<Edge> edges;

    public Graph() {
        this.nodes = new HashSet<>();
        this.edges = new HashSet<>();
    }

    /**
     * Adds an edge to the graph.
     *
     * @param node the node to add to the graph
     */
    public void addNode(Node node) {
        this.nodes.add(node);
    }

    /**
     * Adds an edge to the graph.
     *
     * @param edge the edge to add to the graph
     */
    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }

    /**
     * Adds an edge to the graph. Also adds the two nodes to the graph if they are not already in it.
     *
     * @param from       the node from which the edge starts
     * @param to         the node to which the edge goes
     * @param branchId 	 the branch identifier
     * @param distance   the distance between the two nodes
     * @param travelTime the travel time between the two nodes
     */
    public void addEdge(Node from, Node to, int branchId, float distance, LocalTime travelTime) {
        Edge edge = new Edge(from, to, branchId, distance, travelTime);
        this.edges.add(edge);
        from.addOutgoing(edge);
        to.addIncoming(edge);

        // Add the nodes to the graph if they are not already in it
        this.nodes.add(from);
        this.nodes.add(to);
    }

    public Set<Node> getNodes() {
        return this.nodes;
    }

    public Set<Edge> getEdges() {
        return this.edges;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Graph with %d nodes and %d edges", this.nodes.size(), this.edges.size()));
        for (Edge edge : this.edges) {
            sb.append("\n").append(edge);
        }
        return sb.toString();
    }

	@Override
	public Set<INode> getAllNodes() {
		return Collections.unmodifiableSet(nodes);
	}
}
