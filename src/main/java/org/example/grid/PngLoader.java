package org.example.grid;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PngLoader {
    BufferedImage img = null;
    static final int R = 0xFF0000;
    static final int G = 0xFF00;
    static final int B = 0xFF;
    static final int W = 0xFFFFFF;

    static final List<Point> LOOKUP = List.of(
            new Point(-1, 0),
            new Point(0, -1),
            new Point(0, 1),
            new Point(1, 0)
    );

    public void load(String path) {
        try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(String path) {
        try {
            ImageIO.write(img, "png", new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isWall(int x, int y) {
        int pixel = img.getRGB(x, y);
        return (pixel & W) == 0;
    }

    public Map<Point, Set<Point>> toNodes() {
        final Map<Point, Set<Point>> result = new HashMap<>();

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int pixel = img.getRGB(x, y);
                if ((pixel & W) == 0) {
                    continue;
                }
                Point current = new Point(x, y);
                HashSet<Point> neighbors = new HashSet<>();
                for (Point lookup : LOOKUP) {
                    Point neighbor = current.add(lookup);
                    int neighborPixel = img.getRGB(neighbor.x(), neighbor.y());
                    if ((neighborPixel & W) == 0) {
                        continue;
                    }
                    neighbors.add(neighbor);
                }
                result.put(current, neighbors);
            }
        }

        return result;
    }

    public Point getStart() {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int pixel = img.getRGB(x, y);
                if ((pixel & W) == B) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    public Point getFinish() {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int pixel = img.getRGB(x, y);
                if ((pixel & W) == R) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    public void markPath(List<Point> points) {
        for (Point point : points) {
            img.setRGB(point.x(), point.y(), 0xFF00FFFF);
        }
    }

    public void markPoint(List<Point> points) {
        for (Point point : points) {
            img.setRGB(point.x(), point.y(), 0xFFFF00FF);
        }
    }

    public void markPlan(List<Point> points) {
        for (Point point : points) {
            img.setRGB(point.x(), point.y(), 0xFFFFFF00);
        }
    }

    public BufferedImage getImg() {
        return img;
    }
}
