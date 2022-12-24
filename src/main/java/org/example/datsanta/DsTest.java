package org.example.datsanta;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.astar.AStarFinder;
import org.example.explorer.DrawWindow;
import org.example.search.Graph;
import org.example.search.Scorer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class DsTest {
    public static void main(String[] args) throws Exception {
//        MapGenerator generator = new MapGenerator();
//        loader.load(generator.generate());
        String apiKey = "90a75999-2d77-41d9-aa22-26a85571da53";
        String mapId = "faf7ef78-41b3-4a36-8423-688a61929c08";

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", apiKey);
        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        final ResponseEntity<String> exchange = new RestTemplate()
                .exchange(
                        new URI("https://datsanta.dats.team/json/map/" + mapId + ".json"),
                        HttpMethod.GET,
                        entity,
                        String.class
                );
        String mapJson = exchange.getBody();
        Files.write(Paths.get("" + mapId + "_map.json"), mapJson.getBytes());


        JsonLoader loader = new JsonLoader();
        //loader.load("src/main/java/org/example/datsanta/faf7ef78-41b3-4a36-8423-688a61929c08.json");
        loader.loadJson(mapJson);
        final Map<Child, Set<Child>> nodes = loader.toNodes();
        Child zero = new Child(0, 0);

        long startTime = System.currentTimeMillis();

        final List<List<Gift>> bags = Collector.collectGifts(loader.getDsMap());
        List<List<Integer>> resultBags = bags.stream().map(l -> l.stream().map(Gift::id).toList()).toList();

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
            int targetSize = bags.get(0).size();
            int k = processingPoints.size() / targetSize;
            final Map<Centroid, List<Child>> clustersIteration = KMeans.fit(processingPoints, bags, k, childScorer, 100);
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
//            clustersQueue.add(Map.entry(KMeans.average(furtherCluster.getKey(), furtherCluster.getValue()), furtherCluster.getValue()));
            clustersQueue.add(furtherCluster);
            processingPoints.removeAll(furtherCluster.getValue());
            bags.remove(0);
        }
        //        });

        // fix clusters
        Scorer<Child> fixClustersScorer = childScorer;
        if (true)
            for (Map.Entry<Centroid, List<Child>> c1 : clustersQueue) {
                for (Map.Entry<Centroid, List<Child>> c2 : clustersQueue) {
                    if (c1 == c2) {
                        continue;
                    }

                    TreeMap<Double, Child> c1Candidates = new TreeMap<>();
                    for (Child p : c1.getValue()) {
                        double toC1 = fixClustersScorer.computeCost(c1.getKey().getChild(), p);
                        double toC2 = fixClustersScorer.computeCost(c2.getKey().getChild(), p);
                        if (toC1 > toC2) {
                            c1Candidates.put(-toC1, p);
                        }
                    }

                    TreeMap<Double, Child> c2Candidates = new TreeMap<>();
                    for (Child p : c2.getValue()) {
                        double toC1 = fixClustersScorer.computeCost(c1.getKey().getChild(), p);
                        double toC2 = fixClustersScorer.computeCost(c2.getKey().getChild(), p);
                        if (toC2 > toC1) {
                            c2Candidates.put(-toC2, p);
                        }
                    }

                    while (!c1Candidates.isEmpty() && !c2Candidates.isEmpty()) {
                        Map.Entry<Double, Child> pToC2 = c1Candidates.firstEntry();
                        c1Candidates.remove(pToC2.getKey());
                        Map.Entry<Double, Child> pToC1 = c2Candidates.firstEntry();
                        c2Candidates.remove(pToC1.getKey());

                        c1.getValue().remove(pToC2.getValue());
                        c2.getValue().remove(pToC1.getValue());
                        c1.getValue().add(pToC1.getValue());
                        c2.getValue().add(pToC2.getValue());

                        System.out.println("fixed");
                    }

                }
            }

        clustersQueue.forEach(c -> clusters.put(c.getKey(), new ArrayList<>(c.getValue())));

        long kmeanTime = System.currentTimeMillis();
        System.out.println("kmeanTime: " + (kmeanTime - bagsTime));


        final ConcurrentSkipListSet<Child> notMarked = new ConcurrentSkipListSet<>(loader.getDsMap().children());
        final List<Child> resultPath = new ArrayList<>();

        ExecutorService geneticExecutor = Executors.newFixedThreadPool(12);
        ExecutorService executor = Executors.newFixedThreadPool(12);
        List<Future<List<Child>>> tasks = new ArrayList<>();
        if (true) {

            while (!clustersQueue.isEmpty()) {

                Map.Entry<Centroid, List<Child>> cluster50entry = clustersQueue.remove(clustersQueue.size() - 1);
                List<Child> cluster50 = cluster50entry.getValue();

                cluster50.add(0, zero);

                AtomicInteger clusterIndex = new AtomicInteger();
                notMarked.remove(zero);
                tasks.add(executor.submit(() ->
                {
                    int clIndex = clusterIndex.incrementAndGet();
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
                    List<Future<ArrayList<Child>>> geneticTasks = IntStream.range(0, 3)
                            .mapToObj(n -> geneticExecutor.submit(() -> GeneticSearch.runSearch(cluster50, matrix))).toList();

                    List<ArrayList<Child>> geneticResults = new ArrayList<>();
                    for (Future<ArrayList<Child>> geneticTask : geneticTasks) {
                        ArrayList<Child> children = geneticTask.get();
                        geneticResults.add(children);
                    }
                    ArrayList<Child> geneticPath = geneticResults.stream()
                            .min(Comparator.comparing(circleLineScorer::computeCost)).get();

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
                    System.out.println("singleClusterTime" + clIndex + ": " + (endClusterGeneticTime - startClusterGeneticTime));

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

            ArrayList<Child> deduplicated = new ArrayList<>();
            Child prev = new Child(123456, 123456);
            for (Child child : resultPath) {
                if (child.equals(prev)) {
                    continue;
                }
                deduplicated.add(child);
                prev = child;
            }

            double fullLength = childScorer.computeCost(deduplicated);
            System.out.println("fullLength " + fullLength);
            DsResult dsResult = new DsResult(
                    mapId,
                    deduplicated,
                    resultBags
            );

            try {
                String resultJson = new ObjectMapper().writeValueAsString(dsResult);
                Files.write(Paths.get("" + mapId + "_result_" + System.currentTimeMillis() + ".json"), resultJson.getBytes());
            } catch (Exception e) {
                throw new RuntimeException(e);
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
                if (childScorer.computeCost(drawWindow.getMovePoint(), c.getChild()) < 400) {
                    g.setColor(Color.GRAY);
                } else {
                    g.setColor(Color.DARK_GRAY.darker().darker());
                }
                //g.setColor(Color.GRAY);
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
