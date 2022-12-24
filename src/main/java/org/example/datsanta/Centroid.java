package org.example.datsanta;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Encapsulates all coordinates for a particular cluster centroid.
 */
public class Centroid {

    /**
     * The centroid coordinates.
     */
    private final Map<String, Double> coordinates;
    int count;
    @Getter
    @Setter
    Integer clIndex;
    @Getter
    @Setter
    int[][] matrix;
    @Getter
    @Setter
    List<Child> originalCluster;
    @Getter
    @Setter
    boolean selfIntersecting;

    public Centroid(Map<String, Double> coordinates) {
        this.coordinates = coordinates;
    }

    public Map<String, Double> getCoordinates() {
        return coordinates;
    }

    public Child getChild() {
        return new Child((int) (double) coordinates.get("x"), (int) (double) coordinates.get("y"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Centroid centroid = (Centroid) o;
        return Objects.equals(getCoordinates(), centroid.getCoordinates());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCoordinates());
    }

    @Override
    public String toString() {
        return "Centroid " + coordinates;
    }

    public void setCount(int size) {
        this.count = size;
    }

    public int getCount() {
        return count;
    }
}
