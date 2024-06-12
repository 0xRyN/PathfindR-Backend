package fr.u_paris.gla.project.core;

import fr.u_paris.gla.project.core.shortest_path_finder.Node;
import fr.u_paris.gla.project.core.shortest_path_finder.NodeNotFoundException;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import fr.u_paris.gla.project.core.shortest_path_finder.Graph;
import fr.u_paris.gla.project.core.shortest_path_finder.INode;

/**
 * 
 * Provides utility methods for setting up test graphs for testing the Dijkstra's shortest path algorithm.
 * 
 * @version 1.0
 *
 * @see Node
 * @see Graph
 * @author: Zineddine CHIKHAOUI.
 */
class ShortestPathDijkstraFinderTestHelper {
	
	/**
	 * Returns the node of graph g that has the name nodeName if such a node exists,
	 * and throws a NodeNotFoundException otherwise.
	 * 
	 * @param g
	 * @param nodeName
	 * @return the node of graph g with the name nodeName
	 * @throws NodeNotFoundException
	 */
	public static Node findNodeFromName(Graph g, String nodeName) throws NodeNotFoundException {
		Node n = g.getNodes().stream()
				.filter(node -> node.getName().equals(nodeName))
				.findFirst()
				.orElse(null);
		
		if (n == null) throw new NodeNotFoundException();
		return n;
	}
	
	/**
	 * Create a list containing the nodes of the graph g whose respective names
	 * are stored in the list nodeNames. Throws NodeNotFoundException if at least
	 * one of the given node names does not correspond to any existing node in 
	 * the graph g.
	 * 
	 * @param g a graph
	 * @param nodeNames a list of node names
	 * @return the corresponding list of nodes
	 * @throws NodeNotFoundException 
	 */
	public static List<INode> buildPath(Graph g, List<String> nodeNames) throws NodeNotFoundException {
		
		LinkedList<INode> path = new LinkedList<>();
		for (String nodeName : nodeNames) {
			Node n = findNodeFromName(g, nodeName);
			path.addLast(n);
		}
		
		return Collections.unmodifiableList(path);
	}

    /**
     * Creates a simple graph that consists of a linear path from a source node to a destination node through a middle node.
     * The graph is used to test the basic functionality of the shortest path calculation.
     * 
     * @return A simple {@link Graph} instance for testing.
     */
    public static Graph createSimpleGraph() {
        Node source = new Node("Source");
        Node middle = new Node("Middle");
        Node destination = new Node("Destination");
    
        Graph graph = new Graph();
        graph.addNode(source);
        graph.addNode(middle);
        graph.addNode(destination);
    
        source.addOutgoing(middle, 10);
        middle.addOutgoing(destination, 15);

        return graph;
    }

    /**
     * Creates a graph with two nodes that are disconnected from each other.
     * This setup is used to test the algorithm's behavior in scenarios where no path exists between nodes.
     *
     * @return A {@link Graph} instance representing a disconnected graph.
     */
    public static Graph createDisconnectedGraph() {
        Node source = new Node("Source");
        Node isolated = new Node("Isolated");

        Graph graph = new Graph();
        graph.addNode(source);
        graph.addNode(isolated);

        return graph;
    }

    /**
     * Creates a graph with varying path weights between the start node and the end node.
     * This setup is used to test the algorithm's ability to choose the path with the lowest total weight.
     *
     * @return A {@link Graph} instance with paths of varying weights.
     */
    public static Graph createGraphWithVaryingWeightPaths() {
        Node start = new Node("Start");
        Node middle1 = new Node("Middle1");
        Node middle2 = new Node("Middle2");
        Node end = new Node("End");

        Graph graph = new Graph();
        graph.addNode(start);
        graph.addNode(middle1);
        graph.addNode(middle2);
        graph.addNode(end);

        start.addOutgoing(middle1, 5);
        start.addOutgoing(middle2, 10);
        end.addIncoming(middle1, 10);
        end.addIncoming(middle2, 5);

        return graph;
    }

    /**
     * Creates a graph with multiple equivalent shortest paths from the start node to the end node.
     * This setup is used to test the algorithm's handling of scenarios where multiple shortest paths exist.
     *
     * @return A {@link Graph} instance with multiple equivalent shortest paths.
     */
    public static Graph createGraphWithMultipleEquivalentPaths() {
        Node start = new Node("Start");
        Node middle1 = new Node("Middle1");
        Node middle2 = new Node("Middle2");
        Node end = new Node("End");

        Graph graph = new Graph();
        graph.addNode(start);
        graph.addNode(middle1);
        graph.addNode(middle2);
        graph.addNode(end);

        start.addOutgoing(middle1, 10);
        middle1.addOutgoing(end, 5);
        start.addOutgoing(middle2, 10);
        middle2.addOutgoing(end, 5);

        return graph;
    }

    /**
     * Creates a graph with loops and dead ends to test the algorithm's robustness in complex scenarios.
     * This setup tests the algorithm's ability to navigate through graphs with cycles and no-path situations.
     *
     * @return A {@link Graph} instance with loops and dead ends.
     */
    public static Graph createGraphWithLoopsAndDeadEnds() {
        Node start = new Node("Start");
        Node loop = new Node("Loop");
        Node deadEnd = new Node("DeadEnd");
        Node end = new Node("End");

        Graph graph = new Graph();
        graph.addNode(start);
        graph.addNode(loop);
        graph.addNode(deadEnd);
        graph.addNode(end);

        start.addOutgoing(loop, 5);
        // Creates a loop
        loop.addOutgoing(start, 5);
        // Dead end
        start.addOutgoing(deadEnd, 20);
        loop.addOutgoing(end, 10);

        return graph;
    }
    
    public static Graph createTwoCycleGraph() {
    	Node n1 = new Node("n1");
    	Node n2 = new Node("n2");
    	Node n3 = new Node("n3");
    	Node n4 = new Node("n4");
    	
    	n1.addOutgoing(n2, 2);
    	n2.addOutgoing(n3, 1);
    	n3.addOutgoing(n4, 7);
    	n4.addOutgoing(n1, 6);
    	n1.addOutgoing(n3, 4);
    	n3.addOutgoing(n2, 3);
    	n2.addOutgoing(n4, 1);
    	
    	Graph graph = new Graph();
    	graph.addNode(n1);
    	graph.addNode(n2);
    	graph.addNode(n3);
    	graph.addNode(n4);
    	
    	return graph;
    	
    }

    public static Graph createGraphWithFiveNodes() {
        Node start = new Node("Start");
        Node two = new Node("2");
        Node three = new Node("3");
        Node four = new Node("4");
        Node end = new Node("End");

        Graph graph = new Graph();
        
        HashSet<Node> nodes = new HashSet<Node>();
        nodes.add(start);
        nodes.add(start);
        nodes.add(two);
        nodes.add(three);
        nodes.add(four);
        nodes.add(end);
        graph.setNodes(nodes);

        start.addOutgoing(two,2);
        two.addOutgoing(start,2);
        two.addOutgoing(three, 3);
        three.addOutgoing(two, 3);
        two.addOutgoing(four, 4);
        four.addOutgoing(two, 4);

        three.addOutgoing(end, 2);
        end.addOutgoing(three, 2);
        four.addOutgoing(end, 2);
        end.addOutgoing(four, 2);

        return graph;
    }

    
}