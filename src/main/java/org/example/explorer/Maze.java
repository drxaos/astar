package org.example.explorer;

import org.example.grid.PngLoader;
import org.example.grid.Point;

import java.util.List;

public class Maze {
    PngLoader loader;
    Point current;

    public Maze(PngLoader loader) {
        this.loader = loader;
        this.current = loader.getStart();
    }

    public boolean isWall(Point lookup) {
        Point point = current.add(lookup);
        try {
            boolean wall = loader.isWall(point.x(), point.y());
            if (!wall) {
                loader.markPath(List.of(point));
            }
            return wall;
        } catch (ArrayIndexOutOfBoundsException e) {
            return true;
        }
    }

    public Point getCurrent() {
        return current;
    }

    public void markPlan(Point point) {
        try {
            boolean wall = loader.isWall(point.x(), point.y());
            if (!wall) {
                loader.markPlan(List.of(point));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    public void move(int dx, int dy) {
        if (dx == -1 && dy == 0
                || dx == 1 && dy == 0
                || dx == 0 && dy == -1
                || dx == 0 && dy == 1
        ) {
            loader.markPath(List.of(current));
            current = current.add(new Point(dx, dy));
            loader.markPoint(List.of(current));
        } else {
            throw new RuntimeException("wrong move: " + dx + "," + dy);
        }
    }
}
