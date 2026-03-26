package spaceinvaders;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Barricade extends Entity {

    public BufferedImage image1, image2;

    public Rectangle[] pointsOfImpact;

    public Barricade(Game g, int x, int y) {
        this.gui = g;
        this.xCoordinate = x;
        this.yCoordinate = y;

        pointsOfImpact = new Rectangle[23];

        String barFile = "/general/barricade.png";
        loadImages(barFile, barFile);
        this.curImage = image1;

        setInitialValues();
    }

    public void setInitialValues() {
        super.setInitialValues();

        // 115 x 55
        this.width = (gui.tileSize * 2) + 15;
        this.height = gui.tileSize + 5;
        this.speed = 0;

        setPOIBounds();
    }

    public void loadImages(String filePath1, String filePath2) {
        try {
            image1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(filePath1)));
            image2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(filePath2)));

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        explosionAnimation = new Image[6];
        for (int i = 0; i < 6; i++) {
            explosionAnimation[i] = new ImageIcon(Objects.requireNonNull(getClass()
                    .getResource("/weapons/explosion" + (i + 1) + ".png"))).getImage();
        }
    }

    public void setPOIBounds() {
        int lowY = yCoordinate + 25;
        int highY = yCoordinate;

        int lowHeight = height - 25; // 30
        int hiHeight = height;

        int blastRadius = 5;

        // outer rectangles: 6 on left & 6 on right
        int i = 0, j = 17;
        for (; i < 6 && j < pointsOfImpact.length; i++, j++) {

            // Instantiate recs:
            Rectangle lftRec = pointsOfImpact[i] = new Rectangle();
            Rectangle rgtRec = pointsOfImpact[j] = new Rectangle();

            // Give em Dimensions:
            lftRec.setBounds(xCoordinate + (blastRadius * i), lowY - (blastRadius * i),
                    blastRadius, lowHeight + (blastRadius * i));

            rgtRec.setBounds(xCoordinate + (blastRadius * j), highY + (blastRadius * i),
                    blastRadius, hiHeight - (blastRadius * i));
        }

        // Center rectangles (11):
        for (int k = 6; k < 17; k++) {
            Rectangle ctrRec = pointsOfImpact[k] = new Rectangle();
            ctrRec.setBounds(xCoordinate + (blastRadius * k), yCoordinate, blastRadius, 35);
        }
    }

    public void decreaseBoundsOf(Rectangle pOI, int depth, boolean top) {

        int correctedY = (int) (pOI.getY() + depth);
        int correctedHeight = (int) (pOI.getHeight() - depth);
        if (!top) correctedY = (int) (pOI.getY());

        pOI.setBounds((int) pOI.getX(), correctedY, (int) pOI.getWidth(), correctedHeight);
    }

    public void blastAHole(ArrayList<Rectangle> targets, int damage, boolean top) {
        int clear = new Color(0, 0, 0 ,0).getRGB();

        double startingX = xCoordinate;
        double startingY = yCoordinate;

        for (Rectangle pOI : targets) {

            // (5) x (10 or 15)
            int blastWidth = (int) pOI.getWidth();
            int blastDepth = (blastWidth * damage) + 5;

            if (blastDepth > pOI.getHeight()) blastDepth = (int) pOI.getHeight(); // (5)

            int[] transparentBlk = new int[blastWidth * blastDepth];
            Arrays.fill(transparentBlk, clear);

            int curX = (int) pOI.getX() - (int) startingX;
            int curY = (int) pOI.getY() - (int) startingY;
            if (!top) curY = (int) pOI.getMaxY() - blastDepth - (int) startingY;


            image1.setRGB(curX, curY, blastWidth, blastDepth, transparentBlk, 0, 0);

            decreaseBoundsOf(pOI, blastDepth, top);
        }
    }

    public ArrayList<Rectangle> getPointsOfImpact(Entity other) {
        Rectangle obj = new Rectangle();
        obj.setBounds(other.xCoordinate + (other.width / 4),
                other.yCoordinate, (other.width / 2), other.height);

        ArrayList<Rectangle> impacts = new ArrayList<>();
        for (Rectangle r : pointsOfImpact) {
            if (r.getHeight() > 0 && r.intersects(obj)) {
                impacts.add(r);
            }
        }

        return impacts;
    }

    public boolean isHit(Entity other) {
        Rectangle obj = new Rectangle();

        obj.setBounds(other.xCoordinate + (other.width / 4),
                other.yCoordinate, (other.width / 2), other.height);

        // Check if collision & check if barricade is dead!
        int deadCount = 0;
        for (Rectangle r : pointsOfImpact) {
            if (r.getHeight() > 0 && r.intersects(obj)) {
                return true;
            } else if (r.getHeight() == 0) deadCount++;
        }

        if (deadCount == pointsOfImpact.length) {
            this.isDead = true;
        }

        return false;
    }
}
