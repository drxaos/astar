package org.example.grid;

public record Point(int x, int y) {
    public Point add(Point p) {
        return new Point(x + p.x, y + p.y);
    }
}
