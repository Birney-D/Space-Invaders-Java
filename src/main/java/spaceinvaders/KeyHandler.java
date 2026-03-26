package spaceinvaders;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean leftPress, rightPress, firePress, bombPress;

    @Override
    public void keyTyped(KeyEvent e) {

        if (e.getKeyChar() == 27) {
            System.exit(0);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_LEFT) leftPress = true;
        if (code == KeyEvent.VK_RIGHT) rightPress = true;
        if (code == KeyEvent.VK_SPACE) firePress = true;
        if (code == KeyEvent.VK_SHIFT) bombPress = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_LEFT) leftPress = false;
        if (code == KeyEvent.VK_RIGHT) rightPress = false;
        if (code == KeyEvent.VK_SPACE) firePress = false;
        if (code == KeyEvent.VK_SHIFT) bombPress = false;
    }
}
