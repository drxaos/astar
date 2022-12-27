package org.example.datsanta;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.astar.AStarFinder;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DsTest {
    public static ThreadFactory tf = new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
            final Thread thread = new Thread(r);
            thread.setUncaughtExceptionHandler((t, e) -> e.printStackTrace());
            return thread;
        }
    };

    static String apiKey = "90a75999-2d77-41d9-aa22-26a85571da53";
    static String mapId = "faf7ef78-41b3-4a36-8423-688a61929c08";
    static JsonLoader loader = new JsonLoader();
    static Child zero = new Child(0, 0);
    static AStarFinder<Child> finder;
    static final Map<Centroid, List<Child>> clusters = new ConcurrentHashMap<>();
    static final ArrayList<Map.Entry<Centroid, List<Child>>> clustersQueue = new ArrayList<>();
    static final ConcurrentSkipListSet<Child> notMarked = new ConcurrentSkipListSet<>();
    static final List<Child> resultPath = new ArrayList<>();
    static final List<List<Child>> resultParts = new ArrayList<>();
    static final List<List<Child>> drawParts = new ArrayList<>();
    static final List<AtomicBoolean> drawPartsSelfIntersecting = new ArrayList<>();
    static ExecutorService geneticExecutor = Executors.newFixedThreadPool(12 * GeneticRequest.workers.size(), tf);
    static ExecutorService bestPathExecutor = Executors.newFixedThreadPool(50, tf);
    static final ChildScorer childScorer = new ChildScorer();
    static AtomicInteger activeBestPath = new AtomicInteger(0);
    static AtomicInteger activeGenetic = new AtomicInteger(0);
    static Map<Child, Set<Child>> nodes;

    public static void main(String[] args) throws Exception {
//        MapGenerator generator = new MapGenerator();
//        loader.load(generator.generate());

        Input.run();

//        HttpHeaders headers = new HttpHeaders();
//        headers.add("X-API-Key", apiKey);
//        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
//        HttpEntity<String> entity = new HttpEntity<>(null, headers);
//        final ResponseEntity<String> exchange = new RestTemplate()
//                .exchange(
//                        new URI("https://datsanta.dats.team/json/map/" + mapId + ".json"),
//                        HttpMethod.GET,
//                        entity,
//                        String.class
//                );
//        String mapJson = exchange.getBody();
//        Files.write(Paths.get("" + mapId + "_map.json"), mapJson.getBytes());


        loader.load("src/main/java/org/example/datsanta/faf7ef78-41b3-4a36-8423-688a61929c08.json");
        //loader.loadJson(mapJson);
        nodes = loader.toNodes();

        long startTime = System.currentTimeMillis();

        final List<List<Gift>> bags = Collector.collectGiftsV3(loader.getDsMap());
        bags.sort(Comparator.comparing(List::size, Comparator.reverseOrder()));

        List<List<Integer>> resultBags = bags.stream().map(l -> l.stream().map(Gift::id).toList()).toList();
        System.out.println("bags " + resultBags.size() + ": " + resultBags.stream().map(List::size).toList());

        long bagsTime = System.currentTimeMillis();
        System.out.println("bagsTime: " + (bagsTime - startTime));


        final CircleLineScorer circleLineScorer = new CircleLineScorer(loader.getDsMap().snowAreas());
        final Set<Child> allChildrenSet = new HashSet<>(loader.dsMap.children());
        finder = new AStarFinder<>(
                new Graph<>(nodes),
                circleLineScorer,
                childScorer
        ) {
            @Override
            public List<Child> findRoute(Child from, Child to) {
                List<Child> route = super.findRoute(from, to);
                for (int i = 1; i < route.size() - 1; i++) {
                    Child r = route.get(i);
                    if (allChildrenSet.contains(r)) {
                        if (r.y() + 1 >= 10000) {
                            route.set(i, new Child(r.x(), r.y() - 1));
                        } else {
                            route.set(i, new Child(r.x(), r.y() + 1));
                        }
                    }
                }
                return route;
            }
        };


        Scorer<Child> kMeanScorer = childScorer;
//        Scorer<Child> kMeanScorer = circleLineScorer;
//        Scorer<Child> kMeanScorer = new ChildScorer() {
//            @Override
//            public double computeCost(Child from, Child to) {
//                double angleFromZero = Math.atan2(from.y(), from.x());
//                while (angleFromZero < 0) {
//                    angleFromZero += 2 * Math.PI;
//                }
//                RotatingVector rotatingVector = new RotatingVector(to.x() - from.x(), to.y() - from.y());
//                rotatingVector.rotateCoordinates(angleFromZero);
//                //rotatingVector.x *= 1.5;
//                rotatingVector.restoreCoordinates();
//                return super.computeCost(from, new Child((int) (from.x() + rotatingVector.x), (int) (from.y() + rotatingVector.y)));
//            }
//        };


//        final ForkJoinPool forkJoinPool1 = new ForkJoinPool(12);
//        final ForkJoinTask<?> task1 = forkJoinPool1.submit(() -> {
        final ArrayList<Child> processingPoints = new ArrayList<>(loader.dsMap.children());
        while (!processingPoints.isEmpty()) {
            int targetSize = bags.get(0).size();
            int k = processingPoints.size() / targetSize;
//            final Map<Centroid, List<Child>> clustersIteration = KMeans.fit(processingPoints, bags, k, kMeanScorer, childScorer, 100, loader.getDsMap().snowAreas());
            final Map<Centroid, List<Child>> clustersIteration = KMeans.fit(processingPoints,
                    bags,
                    k,
                    kMeanScorer,
                    childScorer,
                    circleLineScorer,
                    100,
                    loader.getDsMap().snowAreas());

            clusters.clear();
            clustersIteration.forEach((c, l) -> clusters.put(c, new ArrayList<>(l)));


            final Map.Entry<Centroid, List<Child>> furtherCluster = clustersIteration.entrySet()
                    .stream()
                    .max(Comparator.comparing((Map.Entry<Centroid, List<Child>> t) -> childScorer.computeCost(
                            t.getKey().getChild(),
                            new Child(0, 0)
                    )))
                    .get();
            if (furtherCluster.getValue().size() != targetSize) {
                System.out.println("wrong cluster size");
                throw new RuntimeException("wrong cluster size");
            }
//            clustersQueue.add(Map.entry(KMeans.average(furtherCluster.getKey(), furtherCluster.getValue()), furtherCluster.getValue()));
            clustersQueue.add(furtherCluster);

            clusters.clear();
            clustersQueue.forEach(c -> clusters.put(c.getKey(), new ArrayList<>(c.getValue())));


            processingPoints.removeAll(furtherCluster.getValue());
            bags.remove(0);
        }

        // fix clusters
        Scorer<Child> kMeanFixScorer = kMeanScorer;
        if (true)
            for (Map.Entry<Centroid, List<Child>> c1 : clustersQueue) {
                for (Map.Entry<Centroid, List<Child>> c2 : clustersQueue) {
                    if (c1 == c2) {
                        continue;
                    }

                    TreeMap<Double, Child> c1Candidates = new TreeMap<>();
                    for (Child p : c1.getValue()) {
                        double toC1 = kMeanFixScorer.computeCost(c1.getKey().getChild(), p);
                        double toC2 = kMeanFixScorer.computeCost(c2.getKey().getChild(), p);
                        if (toC1 > toC2) {
                            c1Candidates.put(-toC1, p);
                        }
                    }

                    TreeMap<Double, Child> c2Candidates = new TreeMap<>();
                    for (Child p : c2.getValue()) {
                        double toC1 = kMeanFixScorer.computeCost(c1.getKey().getChild(), p);
                        double toC2 = kMeanFixScorer.computeCost(c2.getKey().getChild(), p);
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

                        //System.out.println("fixed");
                    }

                }
            }

        clusters.clear();
        clustersQueue.forEach(c -> clusters.put(c.getKey(), new ArrayList<>(c.getValue())));
        System.out.println("clusters fixed");
//        });

        long kmeanTime = System.currentTimeMillis();
        System.out.println("kmeanTime: " + (kmeanTime - bagsTime));

//        AtomicInteger cqc = new AtomicInteger(0);
//        clustersQueue.parallelStream().forEach(e -> {
//            int i1 = cqc.incrementAndGet();
//            System.out.println("start matrix " + i1);
//            int[][] matrix;
//            matrix = new int[e.getValue().size()][e.getValue().size()];
//            for (int i = 0; i < e.getValue().size(); i++) {
//                for (int j = i + 1; j < e.getValue().size(); j++) {
//                    final List<Child> route = finder.findRoute(e.getValue().get(i), e.getValue().get(j));
//                    double cost = circleLineScorer.computeCost(route);
//                    matrix[i][j] = (int) cost;
//                    matrix[j][i] = (int) cost;
//                }
//            }
//            e.getKey().setMatrix(matrix);
//            System.out.println("end matrix " + i1);
//        });


        notMarked.addAll(loader.getDsMap().children());

        for (int i = 0; i < resultBags.size(); i++) {
            resultParts.add(new ArrayList<>());
            drawParts.add(new ArrayList<>());
            drawPartsSelfIntersecting.add(new AtomicBoolean(false));
        }

//        if (false) { // full genetic
//            ExecutorService fullSearchExecutor = Executors.newFixedThreadPool(20, tf);
//
//            ArrayList<Child> fullSearch = new ArrayList<>(loader.getDsMap().children());
//            fullSearch.add(0, zero);
//            int[][] matrix = new int[fullSearch.size()][fullSearch.size()];
//            List<Future<?>> tasks = new ArrayList<>();
//            for (int i = 0; i < fullSearch.size(); i++) {
//                int finalI = i;
//                tasks.add(fullSearchExecutor.submit(() -> {
//                    System.out.println(finalI);
//                    for (int j = finalI + 1; j < fullSearch.size(); j++) {
//                        final List<Child> route = finder.findRoute(fullSearch.get(finalI), fullSearch.get(j));
//                        double cost = circleLineScorer.computeCost(route);
//                        matrix[finalI][j] = (int) cost;
//                        matrix[j][finalI] = (int) cost;
//                    }
//                }));
//            }
//            tasks.forEach(t -> {
//                try {
//                    t.get();
//                } catch (InterruptedException | ExecutionException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            ArrayList<Child> fullPath = GeneticRequest.runSearch(new ArrayList<>(nodes.keySet()), matrix, 5000, 200, 1000, 0.2f, 40);
//            resultPath.addAll(fullPath);
//        }


        var geneticScorer = circleLineScorer;

        List<Future<List<Child>>> tasks = new ArrayList<>();
        if (true) {
            AtomicInteger clusterIndex = new AtomicInteger();

            List<Callable<List<Child>>> runnables = new ArrayList<>();
            while (!clustersQueue.isEmpty()) {

                Map.Entry<Centroid, List<Child>> cluster50entry = clustersQueue.remove(clustersQueue.size() - 1);
                List<Child> cluster50 = cluster50entry.getValue();

                cluster50.add(0, zero);

                notMarked.remove(zero);
                int clIndex = clusterIndex.getAndIncrement();
                runnables.add(() -> {
                    List<Child> result = bestPath(
                            geneticScorer,
                            clIndex,
                            cluster50entry.getKey(),
                            cluster50entry.getValue()
                    );
                    boolean a = false;
                    if (a && drawPartsSelfIntersecting.get(clIndex).get()) {
                        for (int i = 0; i < 5; i++) {
                            int finalI = i;
                            bestPathExecutor.submit(() -> {
                                System.out.println("recalc " + clIndex + " #" + finalI);
                                try {
                                    bestPath(
                                            geneticScorer,
                                            clIndex,
                                            cluster50entry.getKey(),
                                            null
                                    );
                                    System.out.println("done recalc " + clIndex);
                                    synchronized (resultParts) {
                                        saveResult(false, mapId, startTime, resultBags, childScorer, circleLineScorer, kmeanTime, resultPath, resultParts);
                                    }
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                    return result;
                });
            }
            // перемешиваем, чтобы размазать нагрузку по времени
            List<Callable<List<Child>>> copy = new ArrayList<>(runnables);
            while (!copy.isEmpty()) {
                if (!copy.isEmpty()) {
                    tasks.add(bestPathExecutor.submit(copy.remove(copy.size() - 1)));
                }
//                if (!copy.isEmpty()) {
//                    tasks.add(bestPathExecutor.submit(copy.remove(0)));
//                }
            }
        }

        Executors.newSingleThreadExecutor(tf).submit(() -> {
            if (tasks.isEmpty()) {
                System.out.println("no tasks");
                return;
            }

            tasks.forEach(t -> {
                try {
                    List<Child> path = t.get();
                    resultPath.addAll(path);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });

            synchronized (resultParts) {
                saveResult(true, mapId, startTime, resultBags, childScorer, circleLineScorer, kmeanTime, resultPath, resultParts);
            }
        });


        DrawWindow drawWindow = new DrawWindow();
        final BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

        Executors.newSingleThreadExecutor(tf).submit(() -> {
            while (true) {
                Child dblClick = drawWindow.getDblClick();
                if (dblClick != null) {
                    clusters.forEach((c, ps) -> {
                        if (childScorer.computeCost(dblClick, c.getChild()) < 400) {
                            bestPathExecutor.submit(() -> {
                                try {
                                    System.out.println("redo " + c.getClIndex());
                                    bestPath(
                                            geneticScorer,
                                            c.getClIndex(),
                                            c,
                                            null
                                    );
                                    System.out.println("done " + c.getClIndex());
                                    synchronized (resultParts) {
                                        saveResult(false, mapId, startTime, resultBags, childScorer, circleLineScorer, kmeanTime, resultPath, resultParts);
                                    }
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    });
                }
                Thread.sleep(150);
            }
        });


        Executors.newSingleThreadExecutor(tf).submit(() -> {
            Thread.sleep(60000);
            while (true) {
                boolean a = false;
                if (a || ((ThreadPoolExecutor) bestPathExecutor).getActiveCount() < 10) {
                    clusters.forEach((c, ps) -> {
                        bestPathExecutor.submit(() -> {
                            try {
                                System.out.println("redo " + c.getClIndex());
                                bestPath(
                                        geneticScorer,
                                        c.getClIndex(),
                                        c,
                                        null
                                );
                                System.out.println("done " + c.getClIndex());
                                synchronized (resultParts) {
                                    saveResult(false, mapId, startTime, resultBags, childScorer, circleLineScorer, kmeanTime, resultPath, resultParts);
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        });
                    });
                }
                Thread.sleep(150);
            }
        });


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
                if (false) {
                    g.setColor(Color.GRAY);
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
                for (int j = 0; j < drawParts.size(); j++) {
                    List<Child> resultPart = drawParts.get(j);
                    if (drawPartsSelfIntersecting.get(j).get()) {
                        g.setColor(Color.RED.brighter());
                    } else {
                        g.setColor(Color.ORANGE.darker());
                    }
                    for (int i = 0; i < resultPart.size() - 1; i++) {
                        final Child from = resultPart.get(i);
                        final Child to = resultPart.get(i + 1);
                        g.drawLine(
                                (int) (from.x() / scale - shiftX),
                                (int) (from.y() / scale - shiftY),
                                (int) (to.x() / scale - shiftX),
                                (int) (to.y() / scale - shiftY));
                    }
                }
            }
            clusters.forEach((c, ps) -> {
                if (childScorer.computeCost(drawWindow.getMovePoint(), c.getChild()) < 400) {
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
            {
                try {
                    Child movePoint = drawWindow.getMovePoint();
                    g.setColor(Color.WHITE);
                    g.drawLine(
                            (int) (movePoint.x() / scale - shiftX) - 2,
                            (int) (movePoint.y() / scale - shiftY) - 2,
                            (int) (movePoint.x() / scale - shiftX) + 2,
                            (int) (movePoint.y() / scale - shiftY) + 2
                    );
                    g.drawLine(
                            (int) (movePoint.x() / scale - shiftX) + 2,
                            (int) (movePoint.y() / scale - shiftY) - 2,
                            (int) (movePoint.x() / scale - shiftX) - 2,
                            (int) (movePoint.y() / scale - shiftY) + 2
                    );
                } catch (Exception ignore) {
                }
            }
            drawWindow.setImg(img);
            Thread.sleep(60);
        }
    }

    private static void saveResult(boolean first,
                                   String mapId,
                                   long startTime,
                                   List<List<Integer>> resultBags,
                                   ChildScorer childScorer,
                                   CircleLineScorer circleLineScorer,
                                   long kmeanTime,
                                   List<Child> resultPath,
                                   List<List<Child>> resultParts) {

        boolean notDone = resultParts.stream().anyMatch(List::isEmpty);
        if (notDone) {
            return;
        }

        ArrayList<Child> partsResultPath = new ArrayList<>();
        resultParts.forEach(partsResultPath::addAll);
        if (first && !partsResultPath.equals(resultPath)) {
            System.out.println("wrong parts path");
            System.out.println(resultPath);
            System.out.println(partsResultPath);
            //throw new RuntimeException("wat");
        }

        long geneticTime = System.currentTimeMillis();
        System.out.println("geneticTime: " + (geneticTime - kmeanTime));

        System.out.println("fullTime: " + (geneticTime - startTime));

        ArrayList<Child> deduplicated = new ArrayList<>();
        Child prev = new Child(0, 0);
        for (Child child : partsResultPath) {
            if (child.equals(prev)) {
                continue;
            }
            deduplicated.add(child);
            prev = child;
        }

        HashSet<Child> childrenSet = new HashSet<>(loader.getDsMap().children());
        while (!childrenSet.contains(deduplicated.get(deduplicated.size() - 1))) {
            deduplicated.remove(deduplicated.size() - 1);
        }

        double fullLength = childScorer.computeCost(deduplicated);
        System.out.println("fullLength " + fullLength);
        double fullCost = circleLineScorer.computeCost(deduplicated);
        System.out.println("fullCost " + fullCost);

        DsResult dsResult = new DsResult(
                mapId,
                deduplicated,
                resultBags
        );

        try {
            String resultJson = new ObjectMapper().writeValueAsString(dsResult);
            Files.write(Paths.get("" + mapId + "_result_" + System.currentTimeMillis() + "_len_" + (int) fullLength + "_cost_" + (int) fullCost + ".json"), resultJson.getBytes());
            System.out.println("done");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public record AllParams(
            Child zero,
            AStarFinder<Child> finder,
            Map<Centroid, List<Child>> clusters,
            ConcurrentSkipListSet<Child> notMarked,
            List<List<Child>> resultParts,
            CircleLineScorer geneticScorer,
            ExecutorService geneticExecutor,
            int clIndex,
            Map.Entry<Centroid, List<Child>> cluster50entry,
            List<Child> cluster50
    ) {
    }

    private static List<Child> bestPath(
            CircleLineScorer geneticScorer,
            int clIndex,
            Centroid centroid,
            List<Child> cluster1
    ) throws InterruptedException, ExecutionException {
        int active = activeBestPath.incrementAndGet();
        System.out.println("active bestPath: " + active);

        boolean redo = false;
        if (cluster1 != null) {
            centroid.setOriginalCluster(cluster1);
            cluster1 = new ArrayList<>(cluster1);
        } else {
            cluster1 = new ArrayList<>(centroid.getOriginalCluster());
            redo = true;
        }

        centroid.setClIndex(clIndex);
        long startClusterGeneticTime = System.currentTimeMillis();

        final List<Child> clusterMarked = new ArrayList<>();
        clusterMarked.add(zero);

        int[][] matrix;
        if (centroid.getMatrix() == null) {
            matrix = new int[cluster1.size()][cluster1.size()];
            for (int i = 0; i < cluster1.size(); i++) {
                for (int j = i + 1; j < cluster1.size(); j++) {
                    final List<Child> route = finder.findRoute(cluster1.get(i), cluster1.get(j));
                    double cost = geneticScorer.computeCost(route);
                    matrix[i][j] = (int) cost;
                    matrix[j][i] = (int) cost;
                }
            }
            centroid.setMatrix(matrix);
        } else {
            matrix = centroid.getMatrix();
        }
        List<Child> finalCluster50 = new ArrayList<>(cluster1);
        List<Future<ArrayList<Child>>> geneticTasks = new ArrayList<>();
        if (finalCluster50.size() <= 10) {
            geneticTasks.add(geneticExecutor.submit(() -> GeneticRequest.runSearch(finalCluster50, matrix, 2000, 50, 500, 0.2f, 20)));
            // geneticTasks.add(geneticExecutor.submit(() -> GeneticRequest.runSearch(finalCluster50, matrix, 1000, 50, 700, 0.3f, 20)));
        } else if (finalCluster50.size() <= 20) {
//            geneticTasks.add(geneticExecutor.submit(() -> GeneticRequest.runSearch(finalCluster50, matrix, 3000, 300, 3000, 0.3f, 30)));
//            geneticTasks.add(geneticExecutor.submit(() -> GeneticRequest.runSearch(finalCluster50, matrix, 2000, 100, 1000, 0.1f, 30)));
            geneticTasks.add(geneticExecutor.submit(() -> GeneticRequest.runSearch(finalCluster50, matrix, 4000, 100, 1000, 0.25f, 50)));
        } else {
//        geneticTasks.add(geneticExecutor.submit(() -> GeneticRequest.runSearch(finalCluster50, matrix, 3000, 300, 3000, 0.3f, 30)));
            geneticTasks.add(geneticExecutor.submit(() -> GeneticRequest.runSearch(finalCluster50, matrix, 5000, 200, 1000, 0.2f, 40)));

        }
        if (redo) {
//            geneticTasks.add(geneticExecutor.submit(() -> GeneticRequest.runSearch(finalCluster50, matrix, 4000, 500, 1000, 0.5f, 100)));
            geneticTasks.add(geneticExecutor.submit(() -> GeneticRequest.runSearch(finalCluster50, matrix, 10000, 300, 1000, 0.2f, 250)));
            geneticTasks.add(geneticExecutor.submit(() -> GeneticRequest.runSearch(finalCluster50, matrix, 10000, 300, 1000, 0.4f, 250)));
//            geneticTasks.add(geneticExecutor.submit(() -> GeneticRequest.runSearch(finalCluster50, matrix, 10000, 1000, 10000, 0.1f, 400)));
//            geneticTasks.add(geneticExecutor.submit(() -> GeneticRequest.runSearch(finalCluster50, matrix, 10000, 1000, 10000, 0.5f, 400)));
            //  geneticTasks.add(geneticExecutor.submit(() -> GeneticRequest.runSearch(finalCluster50, matrix, 20000, 2000, 20000, 0.3f, 500)));
        }
        geneticTasks.add(geneticExecutor.submit(() -> {
            Child[] array = finalCluster50.toArray(new Child[0]);
            sortPointsClockwise(array, centroid.getChild());
            ArrayList<Child> result = new ArrayList<>(Arrays.asList(array));
            while (!result.get(0).equals(zero)) {
                Collections.rotate(result, 1);
            }
            result.add(zero);
            return result;
        }));
        for (int kk = 2; kk < (redo ? 5 : 3); kk++) {
            int finalKk = kk;
            geneticTasks.add(geneticExecutor.submit(() -> {
                ArrayList<Child> result = new ArrayList<>();
                try {
                    ArrayList<Child> cluster = new ArrayList<>(finalCluster50);
                    cluster.remove(zero);
                    Map<Centroid, List<Child>> subClusters = KMeans.fit(cluster,
                            null,
                            finalKk,
                            childScorer,
                            childScorer,
                            geneticScorer,
                            100,
                            loader.getDsMap().snowAreas());
                    if (subClusters.values().stream().anyMatch(sc -> sc.size() < 4)) {
                        return new ArrayList<>(finalCluster50);
                    }
                    List<List<Child>> entries = new ArrayList<>(subClusters.values().stream().toList());
                    for (int i = 0; i < entries.size(); i++) {
                        List<Child> e = entries.get(i);
                        ArrayList<Child> subCluster = new ArrayList<>(e);
                        if (i == 0) {
                            subCluster.add(0, zero);
                            subCluster.add(0, entries.get(i + 1).get(0));
                        } else if (i == entries.size() - 1) {
                            subCluster.add(0, entries.get(i - 1).get(0));
                            subCluster.add(0, zero);
                        } else {
                            subCluster.add(0, entries.get(i - 1).get(0));
                            subCluster.add(0, entries.get(i + 1).get(0));
                        }

                        int[][] subMatrix = new int[subCluster.size()][subCluster.size()];
                        for (int i1 = 0; i1 < subCluster.size(); i1++) {
                            for (int j1 = 0; j1 < subCluster.size(); j1++) {
                                if (i1 == j1) {
                                    continue;
                                }
                                final List<Child> route = finder.findRoute(subCluster.get(i1), subCluster.get(j1));
                                double cost = geneticScorer.computeCost(route);
                                subMatrix[i1][j1] = (int) cost;
                                subMatrix[j1][i1] = (int) cost;

                                if ((i1 == 1 && j1 == 0) || (i1 == 0 && j1 != 1) || (i1 != 0 && j1 == 1)) {
                                    cost = 1000000000;
                                    subMatrix[i1][j1] = (int) cost;
                                }
                            }
                        }
                        ArrayList<Child> subResult = GeneticRequest.runSearch(subCluster, subMatrix, 5000, 200, 2000, 0.25f, 70);
                        if (i == 0) {
                            while (subResult.remove(entries.get(i + 1).get(0))) ;
                            while (subResult.remove(zero)) ;
                        } else if (i == entries.size() - 1) {
                            while (subResult.remove(entries.get(i - 1).get(0))) ;
                            while (subResult.remove(zero)) ;
                        } else {
                            while (subResult.remove(entries.get(i - 1).get(0))) ;
                            while (subResult.remove(entries.get(i + 1).get(0))) ;
                        }
                        result.addAll(subResult);
                    }


                    while (!nodes.containsKey(result.get(0))) {
                        result.remove(0);
                    }
                    while (!nodes.containsKey(result.get(result.size() - 1))) {
                        result.remove(result.size() - 1);
                    }

                    List<Child> prep = finder.findRoute(zero, result.get(0));
                    List<Child> app = finder.findRoute(result.get(result.size() - 1), zero);

                    ArrayList<Child> result2 = new ArrayList<>();
                    result2.addAll(prep);
                    result2.addAll(result);
                    result2.addAll(app);

                    if (!result2.get(0).equals(zero) || !result2.get(result2.size() - 1).equals(zero) || result2.size() < 5 || finalCluster50.stream().anyMatch(cp -> !result2.contains(cp))) {
                        return new ArrayList<>(finalCluster50);
                    }

                    return result2;
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ArrayList<>(finalCluster50);
                }
            }));
        }

        List<ArrayList<Child>> geneticResults = new ArrayList<>();
        for (Future<ArrayList<Child>> geneticTask : geneticTasks) {
            ArrayList<Child> children = geneticTask.get();
            geneticResults.add(children);
        }
        if (redo) {
            for (int i = 0; i < geneticResults.size(); i++) {
                System.out.println("gcost: " + i + " / " + geneticScorer.computeCost(geneticResults.get(i)));
            }
        }
        ArrayList<Child> geneticPath = geneticResults.stream()
                .min(Comparator.comparing(geneticScorer::computeCost)).get();

        Child current = geneticPath.remove(0);
        while (!cluster1.isEmpty()) {
            //Child finalCurrent = current;
//                    final List<Child> nearest10 = cluster50.parallelStream().sorted(Comparator.comparing(p -> {
//                        return childScorer.computeCost(finalCurrent, p);
//                    })).limit(10).toList();
//                    final Child nearest = nearest10.parallelStream().min(Comparator.comparing(p -> {
//                        final List<Child> route = finder.findRoute(finalCurrent, p);
//                        return geneticScorer.computeCost(route);
//                    })).orElse(null);
            Child nearest = geneticPath.remove(0);
            while (!nodes.containsKey(nearest)) {
                nearest = geneticPath.remove(0);
            }
            final List<Child> route = finder.findRoute(current, nearest);
            notMarked.remove(nearest);
            cluster1.remove(nearest);
            clusterMarked.addAll(route);
            current = nearest;
            //System.out.println("next " + marked.size());
        }

        ArrayList<Child> deduplicated = new ArrayList<>();
        Child prev = new Child(-1, -1);
        for (Child child : clusterMarked) {
            if (child.equals(prev)) {
                continue;
            }
            deduplicated.add(child);
            prev = child;
        }

        synchronized (resultParts) {

            List<Child> resultPart = resultParts.get(clIndex);
            if (!resultPart.isEmpty()) {
                double oldCost = geneticScorer.computeCost(resultPart);
                double newCost = geneticScorer.computeCost(deduplicated);
                System.out.println("old cost: " + oldCost);
                if (newCost > oldCost) {
                    System.out.println("not found better solution");
                    return deduplicated;
                }
            }

            clusters.put(centroid, deduplicated);

            long endClusterGeneticTime = System.currentTimeMillis();
            System.out.println("singleClusterTime" + clIndex + ": " + (endClusterGeneticTime - startClusterGeneticTime));

            resultParts.get(clIndex).clear();
            resultParts.get(clIndex).addAll(deduplicated);

            {
                drawParts.get(clIndex).clear();
                HashSet<Child> originalSet = new HashSet<>(centroid.getOriginalCluster());
                originalSet.remove(zero);
                ArrayList<Child> drawPart = new ArrayList<>(deduplicated);
                while (!originalSet.contains(drawPart.get(0))) {
                    drawPart.remove(0);
                }
                while (!originalSet.contains(drawPart.get(drawPart.size() - 1))) {
                    drawPart.remove(drawPart.size() - 1);
                }
                drawParts.get(clIndex).addAll(drawPart);


                Set<Child> drawPointsSet = new HashSet<>(drawPart);
                //ArrayList<ChildDouble> intersectionPoints = new ArrayList<>();
                AtomicBoolean si = new AtomicBoolean(false);
                loop:
                for (int i = 0; i < drawPart.size() - 1; i++) {
                    final Child from1 = drawPart.get(i);
                    final Child to1 = drawPart.get(i + 1);
                    for (int j = i + 1; j < drawPart.size() - 1; j++) {
                        final Child from2 = drawPart.get(j);
                        final Child to2 = drawPart.get(j + 1);
                        if (from1.equals(from2) && to1.equals(to2)) {
                            continue;
                        }
                        if (from1.equals(to2) && to1.equals(from2)) {
                            continue;
                        }
                        ChildDouble intersection = PointOfIntersection.pointOfIntersection(
                                ChildDouble.from(from1),
                                ChildDouble.from(to1),
                                ChildDouble.from(from2),
                                ChildDouble.from(to2));
                        if (intersection != null && !drawPointsSet.contains(intersection.toChild())) {
                            //intersectionPoints.add(intersection);
                            si.set(true);
                            break loop;
                        }
                    }
                }
                centroid.setSelfIntersecting(si.get());
                drawPartsSelfIntersecting.get(clIndex).set(si.get());
            }

            active = activeBestPath.decrementAndGet();
            System.out.println("active bestPath: " + active);

            return deduplicated;
        }
    }


    static void sortPointsClockwise(Child[] points, Child center) {
        boolean changed;
        do {
            changed = false;
            for (int i = 0; i < points.length - 1; i++) {
                if (comparePoint(points[i + 1], points[i], center)) {
                    Child temp = points[i];
                    points[i] = points[i + 1];
                    points[i + 1] = temp;
                    changed = true;
                }
            }
        } while (changed);
    }

    // http://stackoverflow.com/questions/6989100/sort-points-in-clockwise-order
    static boolean comparePoint(Child a, Child b, Child center) {

        if (a.x() - center.x() >= 0 && b.x() - center.x() < 0) {
            return true;
        }
        if (a.x() - center.x() < 0 && b.x() - center.x() >= 0) {
            return false;
        }
        if (a.x() - center.x() == 0 && b.x() - center.x() == 0) {
            if (a.y() - center.y() >= 0 || b.y() - center.y() >= 0) {
                return a.y() > b.y();
            }
            return b.y() > a.y();
        }

        // compute the cross product of vectors (center -> a) x (center -> b)
        double det = (a.x() - center.x()) * (b.y() - center.y()) -
                (b.x() - center.x()) * (a.y() - center.y());
        if (det < 0) {
            return true;
        }
        if (det > 0) {
            return false;
        }

        // points a and b are on the same line from the center
        // check which point is closer to the center
        double d1 = (a.x() - center.x()) * (a.x() - center.x()) +
                (a.y() - center.y()) * (a.y() - center.y());
        double d2 = (b.x() - center.x()) * (b.x() - center.x()) +
                (b.y() - center.y()) * (b.y() - center.y());
        return d1 > d2;
    }
}
