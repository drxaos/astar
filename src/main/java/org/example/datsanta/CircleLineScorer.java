package org.example.datsanta;

import org.example.search.Scorer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CircleLineScorer implements Scorer<Child> {

    List<SnowArea> snowAreaList;

    Map<Child, Map<Child, Double>> cache = new ConcurrentHashMap<>(4096);

    public CircleLineScorer(List<SnowArea> snowAreaList) {
        this.snowAreaList = snowAreaList;
    }

    public static List<ChildDouble> getCircleLineIntersectionChild(
            Child pointA,
            Child pointB, Child center, double radius
    ) {
        double baX = pointB.x() - pointA.x();
        double baY = pointB.y() - pointA.y();
        double caX = center.x() - pointA.x();
        double caY = center.y() - pointA.y();

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - radius * radius;

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return Collections.emptyList();
        }
        // if disc == 0 ... dealt with later
        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;

        ChildDouble p1 = new ChildDouble((pointA.x() - baX * abScalingFactor1), (pointA.y() - baY * abScalingFactor1));
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return Collections.singletonList(p1);
        }
        ChildDouble p2 = new ChildDouble((pointA.x() - baX * abScalingFactor2), (pointA.y() - baY * abScalingFactor2));
        return Arrays.asList(p1, p2);
    }

    public static List<ChildDouble> getIntersection(
            Child pointA,
            Child pointB,
            Child center,
            double radius
    ) {
        final List<ChildDouble> points = getCircleLineIntersectionChild(
                pointA, pointB, center, radius
        );

        if (points.size() < 2) {
            return List.of();
        }

        List<ChildDouble> result = new ArrayList<>();

        final ChildDouble point1 = points.get(0);
        addIfInside(pointA, pointB, result, point1);

        final ChildDouble point2 = points.get(1);
        addIfInside(pointA, pointB, result, point2);

        return result;
    }

    private static void addIfInside(final Child pointA, final Child pointB, final List<ChildDouble> result, final ChildDouble point1) {
        if (point1.x() >= Math.min(pointA.x(), pointB.x()) && point1.x() <= Math.max(pointA.x(), pointB.x())) {
            if (point1.y() >= Math.min(pointA.y(), pointB.y()) && point1.y() <= Math.max(pointA.y(), pointB.y())) {
                result.add(point1);
            }
        }
    }

    public static double distance(
            ChildDouble pointA,
            ChildDouble pointB
    ) {
        return Math.sqrt(Math.pow(pointA.x() - pointB.x(), 2) + Math.pow(pointA.y() - pointB.y(), 2));
    }

    public static double getInsideCost(
            Child pointA,
            Child pointB,
            Child center,
            double radius
    ) {
        final List<ChildDouble> intersections = getIntersection(pointA, pointB, center, radius);
        if (intersections.size() == 2) {
            final ChildDouble p1 = intersections.get(0);
            final ChildDouble p2 = intersections.get(1);
            return distance(p1, p2) * 6;
        } else if (intersections.isEmpty()) {
            if (distance(ChildDouble.from(center), ChildDouble.from(pointA)) <= radius) {
                return distance(ChildDouble.from(pointA), ChildDouble.from(pointB)) * 6;
            } else {
                return 0;
            }
        } else {
            final ChildDouble p1 = intersections.get(0);
            if (distance(ChildDouble.from(center), ChildDouble.from(pointA)) <= radius) {
                return distance(ChildDouble.from(pointA), p1) * 6;
            } else {
                return distance(ChildDouble.from(pointB), p1) * 6;
            }
        }
    }

    public static double angle(double x, double y) {
        return Math.atan2(y, x);
    }

    public static double getCost(
            Child pointA,
            Child pointB,
            List<SnowArea> circles
    ) {
        final double insideCost = circles.stream().map(c -> getInsideCost(pointA, pointB, new Child(c.x(), c.y()), c.r())).mapToDouble(r -> r).sum();
        final double outsideCost = distance(ChildDouble.from(pointA), ChildDouble.from(pointB));

        double angle = angle(pointB.x() - pointA.x(), pointB.y() - pointA.y());
        double xx = Math.cos(angle) * (1-0.636);
        double yy = Math.sin(angle);
        double outsideCostWind = Math.sqrt(Math.pow(xx, 2) + Math.pow(yy, 2)) * outsideCost;

        return insideCost + outsideCostWind;
    }

    @Override
    public double computeCost(Child from, Child to) {
        final Map<Child, Double> m = cache.computeIfAbsent(from, f -> new ConcurrentHashMap<>(4096));
        final Double cached = m.get(to);
        if (cached != null) {
            return cached;
        }
        final double cost = getCost(from, to, snowAreaList);
        m.put(to, cost);
        return cost;
    }
}
