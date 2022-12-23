package org.example.datsanta;

import org.example.astar.AStarFinder;
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

        final Map<Centroid, List<Child>> clusters = new HashMap<>();
        final Map<Centroid, List<Child>> debugClusters = new HashMap<>();
        final Map<Centroid, List<Child>> debugFurtherClusters = new HashMap<>();
        final ArrayList<List<Child>> clustersQueue = new ArrayList<>();
        //        final ForkJoinPool forkJoinPool1 = new ForkJoinPool(12);
        //        final ForkJoinTask<?> task1 = forkJoinPool1.submit(() -> {
        final ArrayList<Child> processingPoints = new ArrayList<>(loader.dsMap.children());
        while (!processingPoints.isEmpty()) {
            final Map<Centroid, List<Child>> clustersIteration = KMeans.fit(processingPoints, bags, bags.size() * 2, childScorer, 50);
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
                final List<Child> cluster50 = clustersQueue.remove(0);

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
        while (true) {
            final BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
            loader.dsMap.snowAreas().forEach(snowArea -> {
                Graphics2D g = (Graphics2D) img.getGraphics();
                Shape theCircle = new Ellipse2D.Double(
                        (snowArea.x() - snowArea.r()) / 10,
                        (snowArea.y() - snowArea.r()) / 10,
                        (snowArea.r() * 2) / 10,
                        (snowArea.r() * 2) / 10
                );
                g.setColor(Color.BLUE);
                g.draw(theCircle);
            });

            clusters.forEach((c, ps) -> {
                Graphics2D g = (Graphics2D) img.getGraphics();
                g.setColor(Color.DARK_GRAY);
                ps.forEach(p -> {
                    g.drawLine(c.getChild().x() / 10, c.getChild().y() / 10, p.x() / 10, p.y() / 10);
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
                Graphics2D g = (Graphics2D) img.getGraphics();
                g.setColor(Color.YELLOW);
                for (int i = 0; i < marked.size() - 1; i++) {
                    final Child from = marked.get(i);
                    final Child to = marked.get(i + 1);
                    g.drawLine(from.x() / 10, from.y() / 10, to.x() / 10, to.y() / 10);
                }
            }
            nodes.keySet().forEach(n -> {
                img.setRGB(n.x() / 10, n.y() / 10, 0x00FF00);
            });
            loader.getDsMap().children().forEach(child -> {
                img.setRGB(child.x() / 10, child.y() / 10, 0xFF0000);
            });

            drawWindow.setImg(img);
            Thread.sleep(150);
        }
    }
}
