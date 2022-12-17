package org.example.dijkstra;

import java.util.*;

class DijkstraGraph<T> {
    private final Map<T, DijkstraVertex<T>> graph; // mapping of vertex names to Vertex objects, built from a set of Edges

    /**
     * Builds a graph from a set of edges
     */
    public DijkstraGraph(List<DijkstraEdge<T>> edges) {
        graph = new HashMap<>(edges.size());

        //one pass to find all vertices
        for (DijkstraEdge<T> e : edges) {
            if (!graph.containsKey(e.v1)) graph.put(e.v1, new DijkstraVertex<T>(e.v1));
            if (!graph.containsKey(e.v2)) graph.put(e.v2, new DijkstraVertex<T>(e.v2));
        }

        //another pass to set neighbouring vertices
        for (DijkstraEdge<T> e : edges) {
            graph.get(e.v1).neighbours.put(graph.get(e.v2), e.dist);
            //graph.get(e.v2).neighbours.put(graph.get(e.v1), e.dist); // also do this for an undirected graph
        }
    }

    /**
     * Runs dijkstra using a specified source vertex
     */
    public void dijkstra(T startName) {
        if (!graph.containsKey(startName)) {
            System.err.printf("Graph doesn't contain start vertex \"%s\"\n", startName);
            return;
        }
        final DijkstraVertex<T> source = graph.get(startName);
        NavigableSet<DijkstraVertex<T>> q = new TreeSet<>();

        // set-up vertices
        for (DijkstraVertex<T> v : graph.values()) {
            v.previous = v == source ? source : null;
            v.dist = v == source ? 0 : Integer.MAX_VALUE;
            q.add(v);
        }

        dijkstra(q);
    }

    /**
     * Implementation of dijkstra's algorithm using a binary heap.
     */
    private void dijkstra(final NavigableSet<DijkstraVertex<T>> q) {
        DijkstraVertex<T> u, v;
        while (!q.isEmpty()) {

            u = q.pollFirst(); // vertex with shortest distance (first iteration will return source)
            if (u.dist == Integer.MAX_VALUE)
                break; // we can ignore u (and any other remaining vertices) since they are unreachable

            //look at distances to each neighbour
            for (Map.Entry<DijkstraVertex<T>, Double> a : u.neighbours.entrySet()) {
                v = a.getKey(); //the neighbour in this iteration

                final double alternateDist = u.dist + a.getValue();
                if (alternateDist < v.dist) { // shorter path to neighbour found
                    q.remove(v);
                    v.dist = alternateDist;
                    v.previous = u;
                    q.add(v);
                }
            }
        }
    }

    public List<T> getPath(T to) {
        return graph.get(to).getPath();
    }
}
