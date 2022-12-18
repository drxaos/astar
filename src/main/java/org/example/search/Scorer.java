package org.example.search;

import java.util.List;
import java.util.stream.IntStream;

public interface Scorer<T> {
    double computeCost(T from, T to);

    default double computeCost(List<T> route) {
        return IntStream.range(0, route.size() - 1)
                .mapToDouble(i -> computeCost(route.get(i), route.get(i + 1)))
                .sum();
    }
}