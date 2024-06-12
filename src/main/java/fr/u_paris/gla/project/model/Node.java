package fr.u_paris.gla.project.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.u_paris.gla.project.core.shortest_path_finder.IArrow;
import fr.u_paris.gla.project.core.shortest_path_finder.INode;

@JsonIgnoreProperties({"outgoingArrows", "incomingArrows", "outgoing", "incoming"})
public class Node implements INode {

    // The identifier of the line, for example line "A" or line "B"
    private final String lineId;

    // The station that this node represents.
    // While one node represents one station, one station can be represented by multiple nodes
    private final Station station;

    // Edges that are reachable from this node
    private final Set<Edge> outgoing;
    
    //Edges that go to this node
    private final Set<Edge> incoming;


    /**
     * Creates a new node with the given line identifier, branch identifier and station.
     *
     * @param lineId   the identifier of the line
     * @param station  the station that this node represents
     */
    public Node(String lineId, Station station) {
        this.lineId = lineId;
        this.station = station;
        this.outgoing = new HashSet<>();
        this.incoming = new HashSet<>();
    }

    public void addOutgoing(Edge edge) {
        this.outgoing.add(edge);
    }
    
    public void addIncoming(Edge edge) {
    	this.incoming.add(edge);
    }

    public String getLineId() {
        return this.lineId;
    }

    public Station getStation() {
        return this.station;
    }

    public Set<Edge> getOutgoing() {
        return Collections.unmodifiableSet(outgoing);
    }
    
    public Set<Edge> getIncoming() {
    	return Collections.unmodifiableSet(incoming);
    }

    /**
     * Returns a unique identifier for the node.
     * The unique identifier is a concatenation of the line identifier, branch identifier and station name.
     *
     * @return the unique identifier of the node
     */
    public String getUniqueIdentifier() {
        return this.lineId + "-" + this.station.getName();
    }

    @Override
    public String toString() {
        return String.format("Node %s %s", lineId, station);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Node node = (Node) obj;
        return lineId.equals(node.lineId) && station.equals(node.station);
    }

    @Override
    public int hashCode() {
        return lineId.hashCode() + station.hashCode();
    }

	@Override
	public Collection<IArrow> getOutgoingArrows() {
		return Collections.unmodifiableCollection(this.getOutgoing());
	}

	@Override
	public Collection<IArrow> getIncomingArrows() {
		return Collections.unmodifiableCollection(this.getIncoming());
	}


}
