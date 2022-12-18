package org.example.dijkstra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.search.Scorer;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DijkstraFinder<T> {
    private final org.example.search.Graph<T> graph;
    private final Scorer<T> nextNodeScorer;

    DijkstraGraph<T> g;
    T from;

    public List<T> findRoute(T from, T to) {
        if (this.from != from) {
            List<DijkstraEdge<T>> edges = graph.getConnections().entrySet().stream()
                    .flatMap(e -> e.getValue().stream()
                            .map(t -> new DijkstraEdge<T>(e.getKey(), t, nextNodeScorer.computeCost(e.getKey(), t))))
                    .toList();
            g = new DijkstraGraph<>(edges);
            g.dijkstra(from);
            this.from = from;
        }
        return g.getPath(to);
    }
}

