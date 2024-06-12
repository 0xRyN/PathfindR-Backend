package fr.u_paris.gla.project.core.shortest_path_finder;

import java.util.Collection;

public interface INode {
	
	/**
	 * Get all the arrows that start from this node.
	 * @return a collection containing all outgoing arrows.
	 */
	public Collection<IArrow> getOutgoingArrows();
	
	/**
	 * Get all the arrows that point to this node.
	 * @return a collection containing all incoming arrows.
	 */
	public Collection<IArrow> getIncomingArrows();

}
