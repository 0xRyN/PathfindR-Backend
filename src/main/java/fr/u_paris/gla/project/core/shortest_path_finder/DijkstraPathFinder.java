package fr.u_paris.gla.project.core.shortest_path_finder;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * A utility class for a Dijkstra shortest path algorithm
 * that is applicable to an oriented graph that uses integer
 * values for the arrow weights.
 * 
 * @author Veronique Hayaert
 */
public class DijkstraPathFinder {
	
	/** Hidden constructor */
	private DijkstraPathFinder() {}
	
	public static List<INode> computeShortestPath (IGraph graph, INode source, INode target) 
			throws NodeNotFoundException, PathNotFoundException {
		
		if ((graph == null) || (source == null) || (target == null)) 
			throw new IllegalArgumentException();
		
		// At the start, all nodes are unvisited
		Set<INode> unvisited = new HashSet<INode>();
		unvisited.addAll(graph.getAllNodes());
		HashMap<INode, Integer> visited = new HashMap<INode,Integer>();
		
		if ((!unvisited.contains(source)) || (!unvisited.contains(target))) 
			throw new NodeNotFoundException("The graph does not contain either the source or the target node (or both)");
		
		// Priority queue that contains the distance from the source node to
		// the discovered, but not yet visited nodes
		PriorityQueue<Map.Entry<INode, Integer>> priorityQueue 
			= new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));
		
		priorityQueue.offer(Map.entry(source, 0));
		
		
		// Process each node in the priority queue
		while (! priorityQueue.isEmpty()) {
			
			// Select the node with the smallest known distance from the
			// source node as the "current node"
			Map.Entry<INode, Integer> current = priorityQueue.poll();
			
			// If the current node is not the target node, the algorithm continues
			if (current.getKey() != target) {
				
				// Get arrows that go "out" from the current node
				Collection<IArrow> arrows = current.getKey().getOutgoingArrows();
				arrows.forEach((arrow) -> {
					
					//Consider each unvisited neighbor
					if (unvisited.contains(arrow.getTarget())) {
						
						// Calculate the distance to this neighbor
						int dist = current.getValue() + arrow.getCost();
						
						// Compare the newly calculated distance to the one currently
						// assigned to this neighbor if it exists
						Optional<Entry<INode, Integer>> opt = 
							priorityQueue.stream()
								.filter(entry -> entry.getKey() == arrow.getTarget())
								.findFirst();
						
						// Assign the distance to the smaller one of the two
						if (opt.isPresent()) {
							int optValue = opt.get().getValue();
							
							if(dist < optValue) {
								priorityQueue.removeIf(entry -> entry.getKey() == arrow.getTarget());
								priorityQueue.offer(Map.entry(arrow.getTarget(), dist));
							}
						} else {
							priorityQueue.offer(Map.entry(arrow.getTarget(), dist));
						}
					}
					
				});
			
				// Remove the current node from the unvisited set
				// when all of its neighbors have been considered
				unvisited.remove(current.getKey());
				
				// Add the current node to the "visited" set
				visited.putIfAbsent(current.getKey(), current.getValue());
			
			// When the target node has been found, we can stop the search
			// and extract the shortest path to the target
			} else {
				visited.putIfAbsent(current.getKey(), current.getValue());
				
				// Extract the shortest path to the target node by picking its neighbor
				// with the shortest distance, and repeating until the source node is reached. 
				return extractShortestPath(visited,source,target);
						
			}
		}

        return null;
    }
	
	
	private static List<INode> extractShortestPath(Map<INode,Integer> visited, INode source, INode target) 
			throws PathNotFoundException {
		
		INode current = target;
		LinkedList<INode> path = new LinkedList<INode>();
		path.add(target);
		
		while(current != source) {
			
			Collection<IArrow> incoming = current.getIncomingArrows();
			
			// Find the neighbor with the shortest distance
			Optional<INode> minNeighbor =
				incoming.stream()
						.filter((arrow) -> visited.get(arrow.getSource()) != null)
						.map((arrow) -> Map.entry(
							( arrow.getSource() ),
							( visited.get(arrow.getSource()) + arrow.getCost() )) )
						.min((e1,e2) -> Integer.compare(e1.getValue(), e2.getValue()))
						.map(entry -> entry.getKey());
			
			// The neighbor with the shortest distance is added to the path,
			// and becomes the new current node
			if (minNeighbor.isPresent()) {
				current = minNeighbor.get();
				path.addFirst(current);
			} else {
				throw new PathNotFoundException();
			}
			
		}
		
		return Collections.unmodifiableList(path);
	}
	
	/**
	 * Returns the cost associated to the path taken as argument if the path exists, 
	 * and throws an exception otherwise.
	 * 
	 * @param path
	 * @return the cost of the path
	 * @throws PathNotFoundException
	 */
	public static int getPathCost(List<INode> path) throws PathNotFoundException {
		
		if (path == null) throw new IllegalArgumentException("Error : The path is null");
		if (path.isEmpty()) throw new IllegalArgumentException("Error : The path is empty");
		
		int pathLength = path.size();
		int pathCost = 0;
		
		for (int i=0; i<pathLength-1; i++) {
			
			int ind = i;
			Collection<IArrow> outgoing = path.get(i).getOutgoingArrows();
			
			if (outgoing == null) throw new PathNotFoundException();
			if (outgoing.isEmpty()) throw new PathNotFoundException();
			
			Optional<IArrow> targetArrow = outgoing.stream()
					.filter(arrow -> (arrow.getTarget() == path.get(ind+1)))
					.findFirst();
			
			if (targetArrow.isPresent()) pathCost += targetArrow.get().getCost();
			else throw new PathNotFoundException();
			
		}
		
		return pathCost;
	}
	
	public static int computeShortestPathCost(IGraph graph, INode source, INode target) 
			throws NodeNotFoundException, PathNotFoundException {
		
		List<INode> path = DijkstraPathFinder.computeShortestPath(graph, source, target);
		return DijkstraPathFinder.getPathCost(path);
	}
	
}
