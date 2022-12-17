package org.example.grid;

import org.example.search.Scorer;

public class GridScorer implements Scorer<Point> {
    @Override
    public double computeCost(Point from, Point to) {
        if (from.x() == to.x()) {
            return Math.abs(from.y() - to.y());
        }
        if (from.y() == to.y()) {
            return Math.abs(from.x() - to.x());
        }
        return Math.sqrt(Math.pow(to.x() - from.x(), 2) + Math.pow(to.y() - from.y(), 2));
    }
}
