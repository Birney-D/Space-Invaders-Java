package spaceinvaders;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class  Gui {

    private JFrame frame;
    private JLabel label;

    private int xPos = 25;
    private int yPos = 25;

    public void go() {
        // Creating instances of everything:
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Canvas doodle = new Canvas();

        // Adding these widgets to the frame:

        frame.getContentPane().add(BorderLayout.CENTER, doodle);

        frame.setSize(600, 600);
        frame.setVisible(true);

        for (int i = 0; i < 100; i++) {
            xPos++;
            yPos++;
            doodle.repaint();

            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Could use these instead of lambdas and pass the listener into the addActionListener().
//    private class LabelListener implements ActionListener {
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            label.setText("Ouch!");
//        }
//    }
//
//    private class ColorListener implements ActionListener {
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            frame.repaint();
//        }
//    }

    private class Canvas extends JPanel {

        private Image curImg = null;

        public void paintComponent(Graphics g) {

            g.fillRect(0, 0, this.getWidth(), this.getHeight());

            Image image1 = new ImageIcon(Objects.requireNonNull(getClass().getResource("/alien/alien1.png"))).getImage();
            Image image2 = new ImageIcon(Objects.requireNonNull(getClass().getResource("/alien/alien2.png"))).getImage();

            if (curImg == null || curImg.equals(image2)) curImg = image1;
            else curImg = image2;

            g.drawImage(curImg, xPos, yPos, 50, 50,  this);

        }
    }
}
