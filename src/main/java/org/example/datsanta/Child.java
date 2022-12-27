package org.example.datsanta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.example.datsanta.part2.Gender;

import java.util.Map;

public record Child(
        int x,
        int y,
        Gender gender,
        int age
) implements Comparable<Child> {
    public Child(int x, int y, Gender gender, int age) {
        this.x = x;
        this.y = y;
        this.gender = gender;
        this.age = age;
    }

    public Child(int x, int y) {
        this(x, y, null, 0);
    }

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
