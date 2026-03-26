package spaceinvaders;

import java.awt.*;
import java.util.Objects;
import java.util.Random;
import javax.swing.*;

public class DrawingBoard extends JPanel {

    private int x = 25;
    private int y = 25;
    private Image curImage;

    public void paintComponent (Graphics g) {
//        g.setColor(Color.orange);
//        g.fillRect(20, 50, 100, 100);


        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        Image image1 = new ImageIcon(Objects.requireNonNull(getClass().getResource("/alien/alien1.png"))).getImage();
        Image image2 = new ImageIcon(Objects.requireNonNull(getClass().getResource("/alien/alien2.png"))).getImage();

        if (curImage == null) curImage = image1;
        else if (curImage.equals(image1)) curImage = image2;
        else curImage = image1;

        x += 5;
        g.drawImage(curImage, x, y, 50, 50, this);

//
//        Random rando = new Random();
//        int red = rando.nextInt(256);
//        int green = rando.nextInt(256);
//        int blue = rando.nextInt(256);
//        Color randomColor = new Color(red, green, blue);
//        g.setColor(randomColor);
//        g.fillOval(100, 100, 300, 300);

//        Graphics2D g2D = (Graphics2D) g;
//        Color startColor = new Color(red, green, blue);
//        red = rando.nextInt(256);
//        green = rando.nextInt(256);
//        blue = rando.nextInt(256);
//        Color endColor = new Color(red, green, blue);
//        GradientPaint gradient = new GradientPaint(150, 150, startColor, 200, 200, endColor);
//        g2D.setPaint(gradient);
//        g2D.fillOval(100, 100, 250, 250);
    }
}
