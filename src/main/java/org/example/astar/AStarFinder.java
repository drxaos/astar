package org.example.astar;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.search.Graph;
import org.example.search.Scorer;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class AStarFinder<T> {
    private final Graph<T> graph;
    private final Scorer<T> nextNodeScorer;
    private final Scorer<T> targetScorer;

    public List<T> findRoute(T from, T to) {
        Map<T, AStarNode<T>> allNodes = new HashMap<>(4096);
        Queue<AStarNode<T>> openSet = new PriorityQueue<>();

        AStarNode<T> start = new AStarNode<>(from, null, 0d, targetScorer.computeCost(from, to));
        allNodes.put(from, start);
        openSet.add(start);

        while (!openSet.isEmpty()) {
            //log.debug("Open Set contains: " + openSet.stream().map(AStarNode::getCurrent).collect(Collectors.toSet()));
            AStarNode<T> next = openSet.poll();
            //log.debug("Looking at node: " + next);
            if (next.getCurrent().equals(to)) {
                //log.debug("Found our destination!");

                List<T> route = new ArrayList<>();
                AStarNode<T> current = next;
                do {
                    route.add(0, current.getCurrent());
                    current = allNodes.get(current.getPrevious());
                } while (current != null);

                //log.debug("Route: " + route);
                return route;
            }

            graph.getConnections(next.getCurrent()).forEach(connection -> {
                double newScore = next.getRouteScore() + nextNodeScorer.computeCost(next.getCurrent(), connection);
                AStarNode<T> nextNode = allNodes.getOrDefault(connection, new AStarNode<>(connection));
                allNodes.put(connection, nextNode);

                if (nextNode.getRouteScore() > newScore) {
                    nextNode.setPrevious(next.getCurrent());
                    nextNode.setRouteScore(newScore);
                    nextNode.setEstimatedScore(newScore + targetScorer.computeCost(connection, to));
                    openSet.add(nextNode);
                    //log.debug("Found a better route to node: " + nextNode);
                }
            });
        }

        throw new IllegalStateException("No route found");
    }

}
