package spaceinvaders;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;


public class Entity {
    public Game gui;
    public int xCoordinate;
    public int yCoordinate;

    public int width;
    public int height;

    public double speed;
    public boolean isDead, isExploding, isDoneExploding;

    public Image image1, image2;
    public Image curImage;
    public Image[] explosionAnimation;

    public void loadImages(String filePath1, String filePath2) {
        image1 = new ImageIcon(Objects.requireNonNull(getClass().getResource(filePath1))).getImage();
        image2 = new ImageIcon(Objects.requireNonNull(getClass().getResource(filePath2))).getImage();

        explosionAnimation = new Image[6];
        for (int i = 0; i < 6; i++) {
            explosionAnimation[i] = new ImageIcon(Objects.requireNonNull(getClass()
                    .getResource("/weapons/explosion" + (i + 1) + ".png"))).getImage();
        }
    }

    public void setInitialValues() {
        this.isDead = false;
                this.isExploding = false;
                        this.isDoneExploding = false;
    }

    public void draw(Graphics2D g2d) {
        g2d.drawImage(curImage, xCoordinate, yCoordinate, width, height, null);

        // Explosion animation:
        if (this.isExploding) runExplosionAnimation();
    }

    public void runExplosionAnimation() {
        if (curImage.equals(explosionAnimation[0])) curImage = explosionAnimation[1];
        else if (curImage.equals(explosionAnimation[1])) curImage = explosionAnimation[2];
        else if (curImage.equals(explosionAnimation[2])) curImage = explosionAnimation[3];
        else if (curImage.equals(explosionAnimation[3])) curImage = explosionAnimation[4];
        else if (curImage.equals(explosionAnimation[4])) curImage = explosionAnimation[5];
        else if (curImage.equals(explosionAnimation[5])) {
            this.isExploding = false;
            this.isDoneExploding = true;
        }
    }

    public void goBoom() {
        this.curImage = explosionAnimation[0];
        this.isExploding = true;
    }

    public boolean isHit(Entity other) {
        Rectangle me = new Rectangle();
        Rectangle you = new Rectangle();

        me.setBounds(xCoordinate, yCoordinate, width, height);
        you.setBounds(other.xCoordinate, other.yCoordinate, other.width, other.height);

        return me.intersects(you);
    }

    public void makeSound(String fileDir) {
        try {
            AudioInputStream soundSource = AudioSystem.getAudioInputStream(Objects
                    .requireNonNull(getClass().getResourceAsStream(fileDir)));
            Clip soundClip = AudioSystem.getClip();
            soundClip.open(soundSource);

            // wait for clip to stop playing, then close it
            soundClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    // Clip has finished playing, now close it
                    soundClip.close();
                }
            });

            soundClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

