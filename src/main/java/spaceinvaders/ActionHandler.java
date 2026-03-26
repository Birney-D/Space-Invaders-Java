package spaceinvaders;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionHandler implements ActionListener {

    public Game gui;
    public ActionHandler(Game gui) {
        this.gui = gui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        gui.gameOver = false;
        gui.iLost = false;
        gui.clearScene();
        gui.startButton.setVisible(false);

        gui.gameThread = new Thread(gui);
        gui.gameThread.start();
    }
}
