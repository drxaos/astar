package org.example.dijkstra;

import org.example.search.Graph;

import java.util.Map;
import java.util.Set;

public class DijkstraTest {
    public static void main(String[] args) {
        DijkstraFinder<String> finder = new DijkstraFinder<>(
                new Graph<>(Map.of(
                        "A", Set.of("B", "C", "D"),
                        "B", Set.of("A", "C", "E"),
                        "C", Set.of("A", "B"),
                        "D", Set.of("A", "E", "F"),
                        "E", Set.of("B", "D"),
                        "F", Set.of("D")
                )),
                (from, to) -> 1
        );

        System.out.println(finder.findRoute("A", "F"));
        System.out.println(finder.findRoute("C", "E"));
        System.out.println(finder.findRoute("F", "C"));
    }
}
