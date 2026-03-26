package spaceinvaders;
import java.awt.*;
import java.util.Objects;
import javax.swing.*;

public class DrawingBoard extends JPanel {

    private int x = 25;
    private int y = 25;
    private Image curImage;

    public void paintComponent (Graphics g) {
        super.paintComponent(g);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        Image image1 = new ImageIcon(Objects.requireNonNull(getClass().getResource("/alien/alien1.png"))).getImage();
        Image image2 = new ImageIcon(Objects.requireNonNull(getClass().getResource("/alien/alien2.png"))).getImage();
        
        if (curImage == null) curImage = image1;
        else if (curImage.equals(image1)) curImage = image2;
        else curImage = image1;

        x += 5;
        g.drawImage(curImage, x, y, 50, 50, this);
    }
}
