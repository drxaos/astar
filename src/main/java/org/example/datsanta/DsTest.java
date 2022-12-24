package org.example.datsanta;

import org.example.astar.AStarFinder;
import org.example.dijkstra.DijkstraFinder;
import org.example.explorer.DrawWindow;
import org.example.search.Graph;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class DsTest {
    public static void main(String[] args) throws Exception {
        JsonLoader loader = new JsonLoader();
        loader.load("src/main/java/org/example/datsanta/faf7ef78-41b3-4a36-8423-688a61929c08.json");
        final Map<Child, Set<Child>> nodes = loader.toNodes();

        final List<List<Gift>> bags = Collector.collectGifts(loader.getDsMap());

        final ChildScorer childScorer = new ChildScorer();
        final CircleLineScorer circleLineScorer = new CircleLineScorer(loader.getDsMap().snowAreas());
        AStarFinder<Child> finder = new AStarFinder<>(
                new Graph<>(nodes),
                circleLineScorer,
                childScorer
        );
        DijkstraFinder<Child> finderDijkstra = new DijkstraFinder<>(
                new Graph<>(nodes),
                circleLineScorer
        );

        final Map<Centroid, List<Child>> clusters = new HashMap<>();
        final Map<Centroid, List<Child>> debugClusters = new HashMap<>();
        final Map<Centroid, List<Child>> debugFurtherClusters = new HashMap<>();
        final ArrayList<List<Child>> clustersQueue = new ArrayList<>();
        //        final ForkJoinPool forkJoinPool1 = new ForkJoinPool(12);
        //        final ForkJoinTask<?> task1 = forkJoinPool1.submit(() -> {
        final ArrayList<Child> processingPoints = new ArrayList<>(loader.dsMap.children());
        while (!processingPoints.isEmpty()) {
            final Map<Centroid, List<Child>> clustersIteration = KMeans.fit(processingPoints, bags, bags.size() * 3 / 2, childScorer, 50);
            debugClusters.clear();
            debugClusters.putAll(clustersIteration);
            final Map.Entry<Centroid, List<Child>> furtherCluster = clustersIteration.entrySet()
                    .stream()
                    .max(Comparator.comparing((Map.Entry<Centroid, List<Child>> t) -> childScorer.computeCost(
                            t.getKey().getChild(),
                            new Child(0, 0)
                    )))
                    .get();
            debugFurtherClusters.clear();
            debugFurtherClusters.put(furtherCluster.getKey(), furtherCluster.getValue());
            clustersQueue.add(furtherCluster.getValue());
            processingPoints.removeAll(furtherCluster.getValue());
            bags.remove(0);
            clusters.put(furtherCluster.getKey(), furtherCluster.getValue());
        }
        //        });

        final TreeSet<Child> notMarked = new TreeSet<>(loader.getDsMap().children());
        final List<Child> marked = new ArrayList<>();

        final ForkJoinPool forkJoinPool = new ForkJoinPool(12);
        final ForkJoinTask<?> task = forkJoinPool.submit(() -> {
            Child current = new Child(0, 0);
            while (!notMarked.isEmpty()) {
                Child finalCurrent1 = current;

                final var bagsFinal = bags;
                final var clustersFinal = clusters;
                final List<Child> cluster50 = clustersQueue.remove(clustersQueue.size() - 1);

                notMarked.remove(current);
                marked.add(current);

                while (!cluster50.isEmpty()) {
                    Child finalCurrent = current;
                    final List<Child> nearest10 = cluster50.parallelStream().sorted(Comparator.comparing(p -> {
                        return childScorer.computeCost(finalCurrent, p);
                    })).limit(10).toList();
                    final Child nearest = nearest10.parallelStream().min(Comparator.comparing(p -> {
                        final List<Child> route = finder.findRoute(finalCurrent, p);
                        return circleLineScorer.computeCost(route);
                    })).orElse(null);
                    final List<Child> route = finder.findRoute(finalCurrent, nearest);
                    notMarked.remove(nearest);
                    cluster50.remove(nearest);
                    marked.addAll(route);
                    current = nearest;
                    System.out.println("next " + marked.size());
                }

                final List<Child> route = finder.findRoute(current, new Child(0, 0));
                marked.addAll(route);
                current = new Child(0, 0);
            }
        });

        DrawWindow drawWindow = new DrawWindow();
        final BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        while (true) {
            Graphics2D g = (Graphics2D) img.getGraphics();
            g.setBackground(new Color(0, 0, 0, 0));
            g.clearRect(0, 0, img.getWidth(), img.getHeight());

            double scale = drawWindow.getScale();
            double shiftX = drawWindow.getCenter().x / scale;
            double shiftY = drawWindow.getCenter().y / scale;

            loader.dsMap.snowAreas().forEach(snowArea -> {
                Shape theCircle = new Ellipse2D.Double(
                        (snowArea.x() - snowArea.r()) / scale - shiftX,
                        (snowArea.y() - snowArea.r()) / scale - shiftY,
                        (snowArea.r() * 2) / scale,
                        (snowArea.r() * 2) / scale
                );
                g.setColor(Color.BLUE);
                g.draw(theCircle);
            });

            clusters.forEach((c, ps) -> {
                g.setColor(Color.DARK_GRAY);
                ps.forEach(p -> {
                    g.drawLine(
                            (int) (c.getChild().x() / scale - shiftX),
                            (int) (c.getChild().y() / scale - shiftY),
                            (int) (p.x() / scale - shiftX),
                            (int) (p.y() / scale - shiftY));
                });
            });
            //            debugClusters.forEach((c, ps) -> {
            //                Graphics2D g = (Graphics2D) img.getGraphics();
            //                g.setColor(Color.MAGENTA);
            //                ps.forEach(p -> {
            //                    g.drawLine(c.getChild().x() / 10, c.getChild().y() / 10, p.x() / 10, p.y() / 10);
            //                });
            //            });
            //            debugFurtherClusters.forEach((c, ps) -> {
            //                Graphics2D g = (Graphics2D) img.getGraphics();
            //                g.setColor(Color.PINK);
            //                ps.forEach(p -> {
            //                    g.drawLine(c.getChild().x() / 10, c.getChild().y() / 10, p.x() / 10, p.y() / 10);
            //                });
            //            });

            {
                g.setColor(Color.YELLOW);
                for (int i = 0; i < marked.size() - 1; i++) {
                    final Child from = marked.get(i);
                    final Child to = marked.get(i + 1);
                    g.drawLine(
                            (int) (from.x() / scale - shiftX),
                            (int) (from.y() / scale - shiftY),
                            (int) (to.x() / scale - shiftX),
                            (int) (to.y() / scale - shiftY));
                }
            }
            nodes.keySet().forEach(n -> {
                try {
                    img.setRGB(
                            (int) (n.x() / scale - shiftX),
                            (int) (n.y() / scale - shiftY),
                            0x00FF00);
                } catch (Exception ignore) {
                }
            });
            loader.getDsMap().children().forEach(child -> {
                try {
                    img.setRGB(
                            (int) (child.x() / scale - shiftX),
                            (int) (child.y() / scale - shiftY),
                            0xFF0000);
                } catch (Exception ignore) {
                }
            });

            drawWindow.setImg(img);
            Thread.sleep(15);
        }
    }
}
