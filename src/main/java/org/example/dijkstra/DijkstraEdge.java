package org.example.dijkstra;

/**
 * One edge of the graph (only used by Graph constructor)
 */
public class DijkstraEdge<T> {
    public final T v1, v2;
    public final double dist;

    public DijkstraEdge(T v1, T v2, double dist) {
        this.v1 = v1;
        this.v2 = v2;
        this.dist = dist;
    }
}
