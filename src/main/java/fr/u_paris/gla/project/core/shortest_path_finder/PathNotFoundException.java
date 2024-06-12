package fr.u_paris.gla.project.core.shortest_path_finder;

public class PathNotFoundException extends Exception {
	
	public PathNotFoundException() {}
	
	public PathNotFoundException(String message) {
		super(message);
	}
	
}
