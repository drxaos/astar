package org.example.datsanta;

import org.example.grid.Point;
import org.example.search.Scorer;

public class ChildScorer implements Scorer<Child> {
    @Override
    public double computeCost(Child from, Child to) {
        if (from.x() == to.x()) {
            return Math.abs(from.y() - to.y());
        }
        if (from.y() == to.y()) {
            return Math.abs(from.x() - to.x()) * 1.5;
        }
        return Math.sqrt(Math.pow((to.x() - from.x()) * 1.5, 2) + Math.pow(to.y() - from.y(), 2));
    }
}
