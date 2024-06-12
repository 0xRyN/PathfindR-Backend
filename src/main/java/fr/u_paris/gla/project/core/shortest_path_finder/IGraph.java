package fr.u_paris.gla.project.core.shortest_path_finder;

import java.util.Set;

public interface IGraph {
	
	/**
	 * Get all the nodes that belong to this graph
	 * @return a Collection containing all such nodes
	 */
	public Set<INode> getAllNodes();

}
