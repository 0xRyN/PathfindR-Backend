package fr.u_paris.gla.project.model;

public class Network {

    // The name of the network
    private final String name;

    // The graph representing the network
    private final Graph graph;

    /**
     * Creates a new network with the given name.
     *
     * @param name the name of the network
     */
    public Network(String name) {
        this.name = name;
        this.graph = new Graph();
    }

    public String getName() {
        return this.name;
    }

    public Graph getGraph() {
        return this.graph;
    }

    @Override
    public String toString() {
        return this.graph.toString();
    }
}
