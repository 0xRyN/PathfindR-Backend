package fr.u_paris.gla.project.core.shortest_path_finder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a graph.
 *
 * @version 1.0
 *
 * @see Node
 * @author Tran Anh Duy NGUYEN
 */
public class Graph implements IGraph {
    private Set<Node> nodes = new HashSet<>();

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    // getters and setters
    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }

	@Override
	public Set<INode> getAllNodes() {
		return Collections.unmodifiableSet(nodes);
	}
}
