package org.example.datsanta;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class DrawWindow extends JPanel {

    JFrame frame;
    BufferedImage img;
    @Getter
    Point center = new Point(0, 0);
    @Getter
    double scale = 10;
    Point windowPressPoint;
    @Getter
    Child movePoint = new Child(0, 0);
    Child dblClick = null;

    public Child getDblClick() {
        Child dblClick1 = dblClick;
        dblClick = null;
        return dblClick1;
    }

    public DrawWindow() {
        frame = new JFrame("TEST");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.getContentPane().add(main.panel);
        frame.getContentPane().add(this);
        frame.setVisible(true);
        this.addMouseWheelListener(new MouseWheelEventListener());
        this.addMouseMotionListener(new MouseDragListener());
        this.addMouseListener(new MousePressListener());
    }

    public void setImg(BufferedImage img) {
        this.img = img;
        this.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
        frame.pack();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
        }
    }

    public class MouseWheelEventListener implements MouseWheelListener {
        public void mouseWheelMoved(MouseWheelEvent e) {
            int units = e.getWheelRotation();
            double newScale = units < 0 ? scale / 1.1 : scale * 1.1;
            int x = e.getX();
            int y = e.getY();
            int width = ((DrawWindow) e.getSource()).getWidth();
            int height = ((DrawWindow) e.getSource()).getHeight();
            //System.out.println("wheel " + units + ", x " + x + ", y" + y);

            double mapX = x * scale + center.x;
            center.x = (int) (mapX - x * newScale);
            double mapY = y * scale + center.y;
            center.y = (int) (mapY - y * newScale);
            scale = newScale;
        }
    }

    public class MousePressListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            windowPressPoint = e.getPoint();
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                dblClick = new Child((int) (e.getPoint().x * scale + center.x), (int) (e.getPoint().y * scale + center.y));
            }
        }
    }

    public class MouseDragListener extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            int dx = e.getX() - windowPressPoint.x;
            int dy = e.getY() - windowPressPoint.y;
            center.x = (int) (center.x - dx * scale);
            center.y = (int) (center.y - dy * scale);
            windowPressPoint = e.getPoint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            movePoint = new Child((int) (e.getPoint().x * scale + center.x), (int) (e.getPoint().y * scale + center.y));
            frame.setTitle("Point " + movePoint.x() + " : " + movePoint.y());
        }
    }
}
