package org.example.datsanta;

import org.example.datsanta.part2.Presenting;
import org.example.search.Scorer;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Encapsulates an implementation of KMeans clustering algorithm.
 *
 * @author Ali Dehghani
 */
public class KMeans {

    private KMeans() {
        throw new IllegalAccessError("You shouldn't call this constructor");
    }

    /**
     * Will be used to generate random numbers.
     */
    private static final Random random = new Random();

    /**
     * Performs the K-Means clustering algorithm on the given dataset.
     *
     * @param records       The dataset.
     * @param bags
     * @param k             Number of Clusters.
     * @param distance      To calculate the distance between two items.
     * @param childScorer
     * @param maxIterations Upper bound for the number of iterations.
     * @return K clusters along with their features.
     */
    public static Map<Centroid, List<Child>> fit(
            List<Child> records,
            List<List<Presenting>> bags,
            int k,
            Scorer<Child> distance,
            ChildScorer directDistance,
            CircleLineScorer circleLineScorer,
            int maxIterations,
            List<SnowArea> snowAreas
    ) {

        final Child zero = new Child(0, 0);
        final ChildScorer childScorer = new ChildScorer();
        records = records.stream().sorted(Comparator.comparing(c -> {
            return -childScorer.computeCost(c, zero);
        })).toList();

        List<Centroid> centroids = new ArrayList<>();
        if (snowAreas != null) {
            if (snowAreas.size() > k) {
                List<Child> finalRecords = records;
                snowAreas = snowAreas.stream().filter(sn -> {
                            return finalRecords.stream().anyMatch(r -> {
                                return directDistance.computeCost(r, new Child(sn.x(), sn.y())) < sn.r();
                            });
                        }).sorted(Comparator.comparing(sn -> -directDistance.computeCost(new Child(0, 0), new Child(sn.x(), sn.y()))))
                        .limit(k).toList();
            }
            centroids.addAll(snowAreas.stream().map(s -> new Centroid(new HashMap<>(Map.of("x", (double) s.x(), "y", (double) s.y())))).toList());
        }
        centroids.addAll(randomCentroids(records, k - centroids.size()));
        Map<Centroid, List<Child>> clusters = new HashMap<>();
        Map<Centroid, List<Child>> lastState = new HashMap<>();

        // iterate for a pre-defined number of times
        for (int i = 0; i < maxIterations; i++) {
            boolean isLastIteration = i == maxIterations - 1;

            //            final List<Centroid> sortedCentroids = centroids.stream().sorted(Comparator.comparing(c -> {
            //                return -childScorer.computeCost(c.getChild(), zero);
            //            })).toList();
            //            for (int j = 0; j < sortedCentroids.size(); j++) {
            //                sortedCentroids.get(j).setCount(bags.get(j).size());
            //            }
            //
            //            final List<Centroid> sortedCentroidsReverse = centroids.stream().sorted(Comparator.comparing(c -> {
            //                return childScorer.computeCost(c.getChild(), zero);
            //            })).toList();

            // in each iteration we should find the nearest centroid for each record
            for (Child record : records) {

                Centroid centroid;
                if (i < maxIterations / 2) {
                    centroid = nearestCentroid(Map.of(), record, centroids, distance, directDistance, snowAreas);
                } else {
                    centroid = nearestCentroid(clusters, record, centroids, circleLineScorer, directDistance, snowAreas);
                }

                assignToCluster(clusters, record, centroid);
            }

            // if the assignment does not change, then the algorithm terminates
            boolean shouldTerminate = isLastIteration || clusters.equals(lastState);
            lastState = clusters;

//            DsTest.clusters.clear();
//            lastState.forEach((c, l) -> DsTest.clusters.put(c, new ArrayList<>(l)));


            if (shouldTerminate) {
                break;
            }

            // at the end of each iteration we should relocate the centroids
            centroids = relocateCentroids(clusters);
            clusters = new HashMap<>();
        }

        if (bags != null) {
            final List<Centroid> sortedCentroids = centroids.stream().sorted(Comparator.comparing(c -> {
                return -childScorer.computeCost(c.getChild(), zero);
            })).toList();
            final List<Child> furtherCluster = lastState.get(sortedCentroids.get(0));
            final int targetCount = bags.get(0).size();
            sortedCentroids.get(0).setCount(targetCount);
            if (furtherCluster.size() < targetCount) {
                final ArrayList<Child> processingRecords = new ArrayList<>(records);
                processingRecords.removeAll(furtherCluster);
                while (furtherCluster.size() < targetCount) {
                    Child record = nearestRecord(sortedCentroids.get(0), furtherCluster, processingRecords, circleLineScorer);
                    processingRecords.remove(record);
                    assignToCluster(lastState, record, sortedCentroids.get(0));
                }
            } else if (furtherCluster.size() > targetCount) {
                while (furtherCluster.size() > targetCount) {
                    Child record = nearestRecord(new Child(0, 0), furtherCluster, directDistance);
                    furtherCluster.remove(record);
                }
            }
        }

//        DsTest.clusters.clear();
//        lastState.forEach((c, l) -> DsTest.clusters.put(c, new ArrayList<>(l)));

        //
        //        clusters = new HashMap<>();
        //
        //        for (int i = 0; i < maxIterations; i++) {
        //            boolean isLastIteration = i == maxIterations - 1;
        //
        //            final List<Centroid> sortedCentroids = centroids.stream().sorted(Comparator.comparing(c -> {
        //                return -childScorer.computeCost(c.getChild(), zero);
        //            })).toList();
        ////            for (int j = 0; j < sortedCentroids.size(); j++) {
        ////                sortedCentroids.get(j).setCount(bags.get(j).size());
        ////            }
        //            sortedCentroids.get(0).setCount(bags.get(0).size());
        //
        ////            final List<Centroid> sortedCentroidsReverse = centroids.stream().sorted(Comparator.comparing(c -> {
        ////                return childScorer.computeCost(c.getChild(), zero);
        ////            })).toList();
        //
        //            // in each iteration we should find the nearest centroid for each record
        //            final ArrayList<Child> processingRecords = new ArrayList<>(records);
        //            for (Centroid centroid : sortedCentroids) {
        //                for (int j = 0; j < centroid.getCount(); j++) {
        //                    Child record = nearestRecord(clusters, centroid, processingRecords, distance);
        //                    processingRecords.remove(record);
        //                    assignToCluster(clusters, record, centroid);
        //                }
        //            }
        //
        //            // if the assignment does not change, then the algorithm terminates
        //            boolean shouldTerminate = isLastIteration || clusters.equals(lastState);
        //            lastState = clusters;
        //            if (shouldTerminate) {
        //                break;
        //            }
        //
        //            // at the end of each iteration we should relocate the centroids
        //            centroids = relocateCentroids(clusters);
        //            clusters = new HashMap<>();
        //        }
//        Map<Centroid, List<Child>> result = relocateCentroidsFinal(lastState);
//        return result;
        return lastState;
    }

    /**
     * Move all cluster centroids to the average of all assigned features.
     *
     * @param clusters The current cluster configuration.
     * @return Collection of new and relocated centroids.
     */
    private static List<Centroid> relocateCentroids(Map<Centroid, List<Child>> clusters) {
        return clusters
                .entrySet()
                .stream()
                .map(e -> average(e.getKey(), e.getValue()))
                .collect(toList());
    }

    private static Map<Centroid, List<Child>> relocateCentroidsFinal(Map<Centroid, List<Child>> clusters) {
        Map<Centroid, List<Child>> result = new HashMap<>();
        clusters.forEach((k, v) -> {
            result.put(average(k, v), v);
        });
        return result;
    }

    /**
     * Moves the given centroid to the average position of all assigned features. If
     * the centroid has no feature in its cluster, then there would be no need for a
     * relocation. Otherwise, for each entry we calculate the average of all records
     * first by summing all the entries and then dividing the final summation value by
     * the number of records.
     *
     * @param centroid The centroid to move.
     * @param records  The assigned features.
     * @return The moved centroid.
     */
    public static Centroid average(Centroid centroid, List<Child> records) {
        // if this cluster is empty, then we shouldn't move the centroid
        if (records == null || records.isEmpty()) {
            return centroid;
        }

        // Since some records don't have all possible attributes, we initialize
        // average coordinates equal to current centroid coordinates
        Map<String, Double> average = centroid.getCoordinates();

        // The average function works correctly if we clear all coordinates corresponding
        // to present record attributes
        records
                .stream()
                .flatMap(e -> e
                        .getFeatures()
                        .keySet()
                        .stream())
                .forEach(k -> average.put(k, 0.0));

        for (Child record : records) {
            record
                    .getFeatures()
                    .forEach((k, v) -> average.compute(k, (k1, currentValue) -> v + currentValue));
        }

        average.forEach((k, v) -> average.put(k, v / records.size()));

        return new Centroid(average);
    }

    /**
     * Assigns a feature vector to the given centroid. If this is the first assignment for this centroid,
     * first we should create the list.
     *
     * @param clusters The current cluster configuration.
     * @param record   The feature vector.
     * @param centroid The centroid.
     */
    private static void assignToCluster(Map<Centroid, List<Child>> clusters, Child record, Centroid centroid) {
        clusters.compute(centroid, (key, list) -> {
            if (list == null) {
                list = new ArrayList<>();
            }

            list.add(record);
            return list;
        });
    }

    /**
     * With the help of the given distance calculator, iterates through centroids and finds the
     * nearest one to the given record.
     *
     * @param clusters
     * @param record         The feature vector to find a centroid for.
     * @param centroids      Collection of all centroids.
     * @param distance       To calculate the distance between two items.
     * @param directDistance
     * @param snowAreas
     * @return The nearest centroid to the given feature vector.
     */
    private static Centroid nearestCentroid(Map<Centroid, List<Child>> clusters, Child record, List<Centroid> centroids, Scorer<Child> distance, ChildScorer directDistance, List<SnowArea> snowAreas) {
        double minimumDistance = Double.MAX_VALUE;
        Centroid nearest = null;

        for (Centroid centroid : centroids) {
            //if (clusters.getOrDefault(centroid, List.of()).size() > centroid.getCount() - 1) {continue;}

            double currentDistanceToCentroid = distance.computeCost(record, centroid.getChild());

            List<Child> points = clusters.get(centroid);

            SnowArea recordInsnowArea = snowAreas.stream().filter(sn ->
                    directDistance.computeCost(new Child(sn.x(), sn.y()), record) <= sn.r()
            ).findFirst().orElse(null);

            double currentDistanceToPoint = points != null ? points.stream().mapToDouble(p -> {
                        if (recordInsnowArea != null) {
                            if (directDistance.computeCost(new Child(recordInsnowArea.x(), recordInsnowArea.y()), record) <= recordInsnowArea.r()) {
                                return 0;
                            }
                        }
                        return (distance.computeCost(record, p)+currentDistanceToCentroid)/3;
                    })
                    .min().orElse(currentDistanceToCentroid) : currentDistanceToCentroid;

            if (currentDistanceToPoint < minimumDistance) {
                minimumDistance = currentDistanceToPoint;
                nearest = centroid;
            }
        }

        return nearest;
    }

    private static Child nearestRecord(Centroid centroid, List<Child> clusterPoints, List<Child> records, Scorer<Child> distance) {
        double minimumDistance = Double.MAX_VALUE;
        Child nearest = null;

        for (Child record : records) {
            //if (clusters.getOrDefault(centroid, List.of()).size() > centroid.getCount() - 1) {continue;}

            double currentDistanceToCentroid = distance.computeCost(record, centroid.getChild());

//            double currentDistanceToPoint = clusterPoints != null ? clusterPoints.stream().mapToDouble(p -> {
//                        return distance.computeCost(record, p);
//                    })
//                    .min().orElse(currentDistanceToCentroid) : currentDistanceToCentroid;


            if (currentDistanceToCentroid < minimumDistance) {
                minimumDistance = currentDistanceToCentroid;
                nearest = record;
            }
        }

        return nearest;
    }

    private static Child nearestRecord(Child child, List<Child> records, Scorer<Child> distance) {
        double minimumDistance = Double.MAX_VALUE;
        Child nearest = null;

        for (Child record : records) {
            //if (clusters.getOrDefault(centroid, List.of()).size() > centroid.getCount() - 1) {continue;}

            double currentDistance = distance.computeCost(record, child);

            if (currentDistance < minimumDistance) {
                minimumDistance = currentDistance;
                nearest = record;
            }
        }

        return nearest;
    }

    /**
     * Generates k random centroids. Before kicking-off the centroid generation process,
     * first we calculate the possible value range for each attribute. Then when
     * we're going to generate the centroids, we generate random coordinates in
     * the [min, max] range for each attribute.
     *
     * @param records The dataset which helps to calculate the [min, max] range for
     *                each attribute.
     * @param k       Number of clusters.
     * @return Collections of randomly generated centroids.
     */
    private static List<Centroid> randomCentroids(List<Child> records, int k) {
        List<Centroid> centroids = new ArrayList<>();
        Map<String, Double> maxs = new HashMap<>();
        Map<String, Double> mins = new HashMap<>();

        for (Child record : records) {
            record
                    .getFeatures()
                    .forEach((key, value) -> {
                        // compares the value with the current max and choose the bigger value between them
                        maxs.compute(
                                key,
                                (k1, max) -> max == null || value > max
                                        ? value
                                        : max
                        );

                        // compare the value with the current min and choose the smaller value between them
                        mins.compute(
                                key,
                                (k1, min) -> min == null || value < min
                                        ? value
                                        : min
                        );
                    });
        }

        Set<String> attributes = records
                .stream()
                .flatMap(e -> e
                        .getFeatures()
                        .keySet()
                        .stream())
                .collect(toSet());
        for (int i = 0; i < k; i++) {
            Map<String, Double> coordinates = new HashMap<>();
            for (String attribute : attributes) {
                double max = maxs.get(attribute);
                double min = mins.get(attribute);
                coordinates.put(attribute, random.nextDouble() * (max - min) + min);
            }

            centroids.add(new Centroid(coordinates));
        }

        return centroids;
    }
}
