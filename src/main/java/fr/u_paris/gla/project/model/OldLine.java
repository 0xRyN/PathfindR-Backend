package fr.u_paris.gla.project.model;

public class OldLine {
    private String name;
    private Integer direction;

    public OldLine(String name) {
        this(name, -1);
    }

    public OldLine(String name, Integer direction) {
        this.name = name;
        this.direction = direction;
    }

    public String getName() {
        return this.name;
    }

    public Integer getDirection() {
        return this.direction;
    }

    public String toString() {
        return String.format("%s Direction %s", name, direction);
    }
}