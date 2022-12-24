package org.example.datsanta;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public record Child(
        int x,
        int y
) implements Comparable<Child> {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Child child = (Child) o;
        return x == child.x && y == child.y;
    }

    @Override
    public int hashCode() {
        return x << 16 | y;
    }

    public int compareTo(Child other) {
        int result;
        result = Integer.compare(x, other.x);
        if (result == 0) {
            result = Integer.compare(y, other.y);
        }
        return result;
    }

    @JsonIgnore
    public Map<String, Double> getFeatures() {
        return Map.of("x", (double) x, "y", (double) y);
    }
}
