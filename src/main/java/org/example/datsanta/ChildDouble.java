package org.example.datsanta;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;
import java.util.Objects;

public record ChildDouble(
        double x,
        double y
) implements Comparable<ChildDouble> {
    public static ChildDouble from(Child c) {
        return new ChildDouble(c.x(), c.y());
    }

    public Child toChild() {
        return new Child((int) x, (int) y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChildDouble child = (ChildDouble) o;
        return x == child.x && y == child.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int compareTo(ChildDouble other) {
        int result;
        result = Double.compare(x, other.x);
        if (result == 0) {
            result = Double.compare(y, other.y);
        }
        return result;
    }
}
