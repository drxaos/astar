package org.example.explorer;

import org.example.datsanta.DrawWindow;
import org.example.grid.PngLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ExplorerTest {
    public static void main(String[] args) throws InterruptedException {
        PngLoader loader = new PngLoader();
        loader.load("src/main/resources/1.png");
        Maze maze = new Maze(loader);
        Explorer explorer = new Explorer(maze);
        DrawWindow drawWindow = new DrawWindow();
        while (true) {
            explorer.step();
            System.out.println(maze.getCurrent());

            final BufferedImage img = new BufferedImage(loader.getImg().getWidth() * 10, loader.getImg().getHeight() * 10, BufferedImage.TYPE_INT_RGB);
            Graphics g = img.getGraphics();
            g.drawImage(loader.getImg(), 0, 0, loader.getImg().getWidth() * 10, loader.getImg().getHeight() * 10, null);

            drawWindow.setImg(img);
            Thread.sleep(15);
        }
    }
}
