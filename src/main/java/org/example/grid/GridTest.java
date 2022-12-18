package org.example.grid;

import org.example.astar.AStarFinder;
import org.example.dijkstra.DijkstraFinder;
import org.example.search.Graph;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GridTest {
    public static void main(String[] args) {
        {
            PngLoader loader = new PngLoader();
            loader.load("src/main/resources/1.png");
            Map<Point, Set<Point>> map = loader.toNodes();
            Point start = loader.getStart();
            Point finish = loader.getFinish();

            AStarFinder<Point> finder = new AStarFinder<>(
                    new Graph<>(map),
                    new GridScorer(),
                    new GridScorer()
            );

            List<Point> route1 = finder.findRoute(start, finish);
            loader.markPath(route1);
            loader.save("src/main/resources/1-astar.png");
            System.out.println(route1);
        }
        {
            PngLoader loader = new PngLoader();
            loader.load("src/main/resources/1.png");
            Map<Point, Set<Point>> map = loader.toNodes();
            Point start = loader.getStart();
            Point finish = loader.getFinish();

            DijkstraFinder<Point> finder = new DijkstraFinder<>(
                    new Graph<>(map),
                    new GridScorer()
            );

            List<Point> route1 = finder.findRoute(start, finish);
            loader.markPath(route1);
            loader.save("src/main/resources/1-dijkstra.png");
            System.out.println(route1);
        }
        {
            PngLoader loader = new PngLoader();
            loader.load("src/main/resources/2.png");
            Map<Point, Set<Point>> map = loader.toNodes();
            Point start = loader.getStart();
            Point finish = loader.getFinish();

            AStarFinder<Point> finder = new AStarFinder<>(
                    new Graph<>(map),
                    new GridScorer(),
                    new GridScorer()
            );

            List<Point> route1 = finder.findRoute(start, finish);
            loader.markPath(route1);
            loader.save("src/main/resources/2-astar.png");
            System.out.println(route1);
        }
        {
            PngLoader loader = new PngLoader();
            loader.load("src/main/resources/2.png");
            Map<Point, Set<Point>> map = loader.toNodes();
            Point start = loader.getStart();
            Point finish = loader.getFinish();

            DijkstraFinder<Point> finder = new DijkstraFinder<>(
                    new Graph<>(map),
                    new GridScorer()
            );

            List<Point> route1 = finder.findRoute(start, finish);
            loader.markPath(route1);
            loader.save("src/main/resources/2-dijkstra.png");
            System.out.println(route1);
        }

    }
}
