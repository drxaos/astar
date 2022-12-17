package org.example.explorer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DrawWindow extends JPanel {

    JFrame frame;
    BufferedImage img;

    public DrawWindow() {
        frame = new JFrame("TEST");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.getContentPane().add(main.panel);
        frame.getContentPane().add(this);
        frame.setVisible(true);
    }

    public void setImg(BufferedImage img) {
        this.img = img;
        this.setPreferredSize(new Dimension(img.getWidth() * 15, img.getHeight() * 15));
        frame.pack();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            g.drawImage(img, 0, 0, img.getWidth() * 15, img.getHeight() * 15, null);
        }
    }
}