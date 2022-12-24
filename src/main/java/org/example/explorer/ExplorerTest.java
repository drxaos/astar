package org.example.explorer;

import org.example.datsanta.DrawWindow;
import org.example.grid.PngLoader;

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
            drawWindow.setImg(loader.getImg());
            Thread.sleep(15);
        }
    }
}
