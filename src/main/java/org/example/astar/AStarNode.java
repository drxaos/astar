package org.example.astar;

import lombok.Data;

@Data
class AStarNode<T> implements Comparable<AStarNode<?>> {
    private final T current;
    private T previous;
    private double routeScore;
    private double estimatedScore;

    AStarNode(T current) {
        this(current, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    AStarNode(T current, T previous, double routeScore, double estimatedScore) {
        this.current = current;
        this.previous = previous;
        this.routeScore = routeScore;
        this.estimatedScore = estimatedScore;
    }

    @Override
    public int compareTo(AStarNode other) {
        return Double.compare(this.estimatedScore, other.estimatedScore);
    }
}