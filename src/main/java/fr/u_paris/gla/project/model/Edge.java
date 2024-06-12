package fr.u_paris.gla.project.model;

import java.time.LocalTime;

import fr.u_paris.gla.project.core.shortest_path_finder.IArrow;
import fr.u_paris.gla.project.core.shortest_path_finder.INode;

public class Edge implements IArrow {

    // The from and to nodes are the two nodes connected by the edge
    private final Node from;
    private final Node to;

    // Distance is in kilometers, float is enough because it's a small number
    private final float distance;
    
    // Branch identifier
    private final int branchId;

    // Travel time between the two nodes
    private final LocalTime travelTime;

    /**
     * Creates a new edge between the two given nodes.
     *
     * @param from       the node from which the edge starts
     * @param to         the node to which the edge goes
     * @param distance   the distance between the two nodes
     * @param travelTime the travel time between the two nodes
     */
    public Edge(Node from, Node to, int branchId, float distance, LocalTime travelTime) {
        this.from = from;
        this.to = to;
        this.branchId = branchId;
        this.distance = distance;
        this.travelTime = travelTime;
    }

    public Node getFrom() {
        return this.from;
    }

    public Node getTo() {
        return this.to;
    }

    /**
     * Returns the travel time between the two nodes, in LocalTime format.
     *
     * @return the travel time between the two nodes in LocalTime format
     */
    public LocalTime getTravelTime() {
        return this.travelTime;
    }


    /**
     * Returns the distance between the two nodes, in kilometers.
     *
     * @return the distance between the two nodes in kilometers
     */
    public float getDistance() {
        return this.distance;
    }
    
    public int getBranchId() {
    	return this.branchId;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s, %.2f km, in %s", from, to, distance, travelTime);
    }

	@Override
	public INode getSource() {
		return from;
	}

	@Override
	public INode getTarget() {
		return to;
	}

	@Override
	public int getCost() {
		// The cost is equal to the number of seconds
		return ((travelTime.getHour() * 60 * 60)
				+ (travelTime.getMinute() * 60)
				+ (travelTime.getSecond()));

	}
}
