package org.example.dijkstra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * One vertex of the graph, complete with mappings to neighbouring vertices
 */
public class DijkstraVertex<T> implements Comparable<DijkstraVertex<T>> {
    public final T name;
    public double dist = Double.POSITIVE_INFINITY;
    public DijkstraVertex<T> previous = null;
    public final Map<DijkstraVertex<T>, Double> neighbours = new HashMap<>();

    public DijkstraVertex(T name) {
        this.name = name;
    }

    public int compareTo(DijkstraVertex other) {
        if (dist == other.dist)
            return Integer.compare(name.hashCode(), other.name.hashCode());

        return Double.compare(dist, other.dist);
    }

    @Override
    public String toString() {
        return "(" + name + ", " + dist + ")";
    }

    public List<T> getPath() {
        if (this == this.previous) {
            return List.of(this.name);
        } else if (this.previous == null) {
            return List.of();
        } else {
            ArrayList<T> path = new ArrayList<>(this.previous.getPath());
            path.add(this.name);
            return path;
        }
    }
}
