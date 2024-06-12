package fr.u_paris.gla.project.core.shortest_path_finder;

public interface IArrow {
	
	/**
	 * Get the node this arrow starts from
	 * @return the source node
	 */
	public INode getSource();
	
	/**
	 * Get the node this arrow is pointing towards 
	 * @return the target node
	 */
	public INode getTarget();
	
	/**
	 * Get the cost of using this node to go to the target node
	 * @return the cost
	 */
	public int getCost();
	
}
