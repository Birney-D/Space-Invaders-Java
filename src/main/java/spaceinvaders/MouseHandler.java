package spaceinvaders;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class MouseHandler implements MouseListener {
    public Game gui;
    public Barricade bar;

    public MouseHandler(Game g, Barricade b) {
        this.bar = b;
        this.gui = g;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        System.out.println(x + ", " + y);

//        int originX = bar.xCoordinate;
//        int offsetX = x - originX;
//        int originY = bar.yCoordinate;
//        int offsetY = y - originY;
//
//        int[] trans = new int[bar.width * bar.height];
//        int transparent = new Color(0,0,0,0).getRGB();
//
//        Arrays.fill(trans, transparent);
//        bar.image1.setRGB(offsetX, offsetY, 5, 5, trans, 0, 0);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
