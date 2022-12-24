package org.example.datsanta;

import org.example.astar.AStarFinder;
import org.example.dijkstra.DijkstraFinder;
import org.example.explorer.DrawWindow;
import org.example.search.Graph;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;

public class DsTest {
    public static void main(String[] args) throws Exception {
        JsonLoader loader = new JsonLoader();
        loader.load("src/main/java/org/example/datsanta/faf7ef78-41b3-4a36-8423-688a61929c08.json");
        final Map<Child, Set<Child>> nodes = loader.toNodes();

        long startTime = System.currentTimeMillis();

        final List<List<Gift>> bags = Collector.collectGifts(loader.getDsMap());

        long bagsTime = System.currentTimeMillis();
        System.out.println("bagsTime: " + (bagsTime - startTime));


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
        final ArrayList<Map.Entry<Centroid, List<Child>>> clustersQueue = new ArrayList<>();
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
            clustersQueue.add(furtherCluster);
            processingPoints.removeAll(furtherCluster.getValue());
            bags.remove(0);
            clusters.put(furtherCluster.getKey(), new ArrayList<>(furtherCluster.getValue()));
        }
        //        });

        long kmeanTime = System.currentTimeMillis();
        System.out.println("kmeanTime: " + (kmeanTime - bagsTime));


        Child zero = new Child(0, 0);
        final ConcurrentSkipListSet<Child> notMarked = new ConcurrentSkipListSet<>(loader.getDsMap().children());
        final List<Child> resultPath = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(12);
        List<Future<List<Child>>> tasks = new ArrayList<>();
        {

            while (!clustersQueue.isEmpty()) {

                Map.Entry<Centroid, List<Child>> cluster50entry = clustersQueue.remove(clustersQueue.size() - 1);
                List<Child> cluster50 = cluster50entry.getValue();

                cluster50.add(0, zero);

                notMarked.remove(zero);
                tasks.add(executorService.submit(() ->
                {
                    long startClusterGeneticTime = System.currentTimeMillis();

                    final List<Child> clusterMarked = new ArrayList<>();
                    clusterMarked.add(zero);

                    int[][] matrix = new int[cluster50.size()][cluster50.size()];
                    for (int i = 0; i < cluster50.size(); i++) {
                        for (int j = 0; j < cluster50.size(); j++) {
                            final List<Child> route = finder.findRoute(cluster50.get(i), cluster50.get(j));
                            double cost = circleLineScorer.computeCost(route);
                            matrix[i][j] = (int) cost;
                        }
                    }
                    ArrayList<Child> geneticPath = GeneticSearch.runSearch(cluster50, matrix);


                    Child current = geneticPath.remove(0);
                    while (!cluster50.isEmpty()) {
                        //Child finalCurrent = current;
//                    final List<Child> nearest10 = cluster50.parallelStream().sorted(Comparator.comparing(p -> {
//                        return childScorer.computeCost(finalCurrent, p);
//                    })).limit(10).toList();
//                    final Child nearest = nearest10.parallelStream().min(Comparator.comparing(p -> {
//                        final List<Child> route = finder.findRoute(finalCurrent, p);
//                        return circleLineScorer.computeCost(route);
//                    })).orElse(null);
                        Child nearest = geneticPath.remove(0);
                        final List<Child> route = finder.findRoute(current, nearest);
                        notMarked.remove(nearest);
                        cluster50.remove(nearest);
                        clusterMarked.addAll(route);
                        current = nearest;
                        //System.out.println("next " + marked.size());
                    }

//                    final List<Child> route = finder.findRoute(current, new Child(0, 0));
//                    clusterMarked.addAll(route);
//                    current = new Child(0, 0);

                    clusters.put(cluster50entry.getKey(), clusterMarked);

                    long endClusterGeneticTime = System.currentTimeMillis();
                    System.out.println("singleClusterTime: " + (endClusterGeneticTime - startClusterGeneticTime));

                    return clusterMarked;
                }));


            }
        }

        Executors.newSingleThreadExecutor().submit(() -> {
            tasks.forEach(t -> {
                try {
                    List<Child> path = t.get();
                    resultPath.addAll(path);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });

            long geneticTime = System.currentTimeMillis();
            System.out.println("geneticTime: " + (geneticTime - kmeanTime));

            System.out.println("fullTime: " + (geneticTime - startTime));
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
                if (childScorer.computeCost(drawWindow.getMovePoint(), c.getChild()) < 600) {
                    g.setColor(Color.GRAY);
                } else {
                    g.setColor(Color.DARK_GRAY.darker().darker());
                }
                ps.forEach(p -> {
                    if (p.equals(zero)) {
                        return;
                    }
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
                g.setColor(Color.ORANGE.darker().darker().darker().darker());
                for (int i = 0; i < resultPath.size() - 1; i++) {
                    final Child from = resultPath.get(i);
                    final Child to = resultPath.get(i + 1);
//                    if (from.equals(zero) || to.equals(zero)) {
//                        continue;
//                    }
                    g.drawLine(
                            (int) (from.x() / scale - shiftX),
                            (int) (from.y() / scale - shiftY),
                            (int) (to.x() / scale - shiftX),
                            (int) (to.y() / scale - shiftY));
                }
            }
            clusters.forEach((c, ps) -> {
                if (childScorer.computeCost(drawWindow.getMovePoint(), c.getChild()) < 600) {
                    g.setColor(Color.YELLOW);
                } else {
                    return;
                }
                for (int i = 0; i < ps.size() - 1; i++) {
                    final Child from = ps.get(i);
                    final Child to = ps.get(i + 1);
                    g.drawLine(
                            (int) (from.x() / scale - shiftX),
                            (int) (from.y() / scale - shiftY),
                            (int) (to.x() / scale - shiftX),
                            (int) (to.y() / scale - shiftY));
                }
            });
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
                    g.setColor(Color.RED);
                    g.drawOval(
                            (int) (child.x() / scale - shiftX) - 1,
                            (int) (child.y() / scale - shiftY) - 1,
                            3,
                            3
                    );
//                    img.setRGB(
//                            (int) (child.x() / scale - shiftX),
//                            (int) (child.y() / scale - shiftY),
//                            0xFF0000);
                } catch (Exception ignore) {
                }
            });

            drawWindow.setImg(img);
            Thread.sleep(15);
        }
    }
}
