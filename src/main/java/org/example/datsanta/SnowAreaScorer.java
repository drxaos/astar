package org.example.datsanta;

import org.example.search.Scorer;

public class SnowAreaScorer implements Scorer<SnowArea> {
    @Override
    public double computeCost(SnowArea from, SnowArea to) {
        if (from.x() == to.x()) {
            return Math.abs(from.y() - to.y());
        }
        if (from.y() == to.y()) {
            return Math.abs(from.x() - to.x());
        }
        return Math.sqrt(Math.pow(to.x() - from.x(), 2) + Math.pow(to.y() - from.y(), 2));
    }
}
