package spaceinvaders;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


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
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
