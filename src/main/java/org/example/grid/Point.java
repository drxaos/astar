package org.example.grid;

public record Point(int x, int y) implements Comparable<Point> {
    public Point add(Point p) {
        return new Point(x + p.x, y + p.y);
    }

    public int compareTo(Point other) {
        int result;
        result = Integer.compare(x, other.x);
        if (result == 0) {
            result = Integer.compare(y, other.y);
        }
        return result;
    }
}
