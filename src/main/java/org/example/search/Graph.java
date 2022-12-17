package org.example.search;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class Graph<T> {
    private final Map<T, Set<T>> connections;

    public Set<T> getConnections(T node) {
        return connections.getOrDefault(node, Set.of());
    }
}