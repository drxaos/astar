package org.example.explorer;

import org.example.astar.AStarFinder;
import org.example.grid.GridScorer;
import org.example.grid.Point;
import org.example.search.Graph;

import java.util.*;

public class Explorer {
    Set<Point> walls = new HashSet<>();
    Set<Point> path = new HashSet<>();
    LinkedHashSet<Point> waiting = new LinkedHashSet<>();
    Map<Point, Set<Point>> map = new HashMap<>();
    GridScorer gridScorer = new GridScorer();

    Point explore = null;
    int lastWaitingSize = 0;
    boolean replaced = false;

    Maze maze;

    public Explorer(Maze maze) {
        this.maze = maze;
        this.path.add(maze.getCurrent());
    }

    static final List<Point> LOOKUP = List.of(
            new Point(-1, 0),
            new Point(0, -1),
            new Point(0, 1),
            new Point(1, 0)
    );

    public void step() {
        Point current = maze.getCurrent();

        final HashSet<Point> neighbors = new HashSet<>();
        for (Point lookup : LOOKUP) {
            Point neighbor = current.add(lookup);
            boolean wall = maze.isWall(lookup);
            if (wall) {
                walls.add(neighbor);
            } else {
                neighbors.add(neighbor);

                if (!path.contains(neighbor)) { // new path
                    path.add(neighbor);

                    final HashSet<Point> neighbors2 = new HashSet<>();
                    for (Point lookup2 : LOOKUP) {
                        Point neighbor2 = neighbor.add(lookup2);
                        waiting.add(neighbor2);
                        if (!walls.contains(neighbor2)) {
                            neighbors2.add(neighbor2);
                        }
                    }
                    map.put(neighbor, neighbors2);
                }
            }
        }
        map.put(current, neighbors);
        waiting.removeAll(walls);
        waiting.removeAll(path);
        if (lastWaitingSize != waiting.size()) {
            lastWaitingSize = waiting.size();
            replaced = false;
        }
        waiting.forEach(p -> maze.markPlan(p));

        // nearest waiting
        Point nearest = waiting.stream().min(Comparator.comparing(p -> gridScorer.computeCost(current, p))).orElse(null);
        if (explore == null || !waiting.contains(explore)) {
            explore = nearest;
        }

        if (explore == null) {
            // end
            System.out.println("end");
        } else {

            AStarFinder<Point> finder = new AStarFinder<>(new Graph<>(map), gridScorer, gridScorer);
            List<Point> route = finder.findRoute(current, explore);
            AStarFinder<Point> finder2 = new AStarFinder<>(new Graph<>(map), gridScorer, gridScorer);
            List<Point> route2 = finder.findRoute(current, nearest);
            if (route.size() >= 2) {
                Point step = route.get(1);
                maze.move(step.x() - current.x(), step.y() - current.y());

                Point step2 = route2.get(1);
                if (step.equals(step2)) {
                    replaced = true;
                    explore = nearest;
                }
            } else {
                explore = null;
            }
        }
    }
}
