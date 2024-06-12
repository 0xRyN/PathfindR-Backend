package fr.u_paris.gla.project.core.shortest_path_finder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a node in a graph.
 *
 * @version 1.0
 *
 * @see Graph
 * @author Tran Anh Duy NGUYEN
 */
public class Node implements INode {
    private static int nextId = 0;
    private int id;
    private String name;
    
    private Set<Arrow> incomingArrows;
    private Set<Arrow> outgoingArrows;

    public Node(String name) {
        id = nextId++;
        this.name = name;
        this.incomingArrows = new HashSet<Arrow>();
        this.outgoingArrows = new HashSet<Arrow>();
    }

    public void addOutgoing(Node target, int weight) {
        Arrow arrow = new Arrow(this, target, weight);
        this.outgoingArrows.add(arrow);
        target.incomingArrows.add(arrow);
    }
    
    public void addIncoming(Node source, int weight) {
    	Arrow arrow = new Arrow(source, this, weight);
    	this.incomingArrows.add(arrow);
    	source.outgoingArrows.add(arrow);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOutgoingArrows(Set<Arrow> outgoing) {
        this.outgoingArrows = outgoing;
    }
    
    public void setIncomingArrows(Set<Arrow> incoming) {
        this.incomingArrows = incoming;
    }

	@Override
	public Collection<IArrow> getOutgoingArrows() {
		return Collections.unmodifiableCollection(this.outgoingArrows);
	}

	@Override
	public Collection<IArrow> getIncomingArrows() {
		return Collections.unmodifiableCollection(this.incomingArrows);
	}
}
