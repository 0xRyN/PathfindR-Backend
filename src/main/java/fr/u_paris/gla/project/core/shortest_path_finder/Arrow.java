package fr.u_paris.gla.project.core.shortest_path_finder;

public class Arrow implements IArrow {
	
	private final Node source;
	private final Node target;
	private final int weight;
	
	public Arrow(Node source, Node target, int weight) {
		this.source = source;
		this.target = target;
		this.weight = weight;
	}
	
	@Override
	public INode getSource() {
		return source;
	}

	@Override
	public INode getTarget() {
		return target;
	}

	@Override
	public int getCost() {
		return weight;
	}

}
