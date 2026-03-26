package spaceinvaders;
import java.awt.*;


public class Alien extends Entity {

    public Shot weapon;
    public String type;
    public String audioFile1, audioFile2;
    
    public int imageCounter;
    public int dropSpeed;
    public int bounty;
    public int strength;
    public int shotSpeed;

    public static boolean goingRight = true;


    public Alien(Game g, int type, int x, int y) {
        this.gui = g;
        this.xCoordinate = x;
        this.yCoordinate = y;

        switch (type) {

            case 1 -> {
                this.type = "bottom";
                loadImages("/alien/bottomAlien1.png", "/alien/bottomAlien2.png");
                audioFile1 = "/alien/invader1.wav";
                this.bounty = 10;
                this.strength = 1;
            }
            case 2 -> {
                this.type = "mid";
                loadImages("/alien/midAlien1.png", "/alien/midAlien2.png");
                audioFile1 = "/alien/invader2.wav";
                this.bounty = 20;
                this.strength = 2;
            }
            case 3 -> {
                this.type = "top";
                loadImages("/alien/topAlien1.png", "/alien/topAlien2.png");
                audioFile1 = "/alien/invader3.wav";
                this.bounty = 30;
                this.strength = 3;
            }
            case 4 -> {
                this.type = "ufoL";
                loadImages("/alien/ufo.png", "/alien/ufo.png");
                audioFile1 = "/alien/ufo_highpitch.wav";
                this.bounty = 300;
                this.strength = 3;
            }
            case 5 -> {
                this.type = "ufoR";
                loadImages("/alien/ufo.png", "/alien/ufo.png");
                audioFile1 = "/alien/ufo_highpitch.wav";
                this.bounty = 300;
                this.strength = 3;
            }
        }

        this.audioFile2 = "/alien/invaderkilled.wav";
        this.curImage = image1;
        this.setInitialValues();
    }

    public void setInitialValues() {
        super.setInitialValues();

        // (30 x 30)
        if (this.type.equals("ufoL") || this.type.equals("ufoR")) {
            this.width = gui.tileSize;
            this.height = 20;
            this.speed = 5;
            this.dropSpeed = 0;

        } else {
            this.width = this.height = (gui.tileSize - 20);
            this.speed = 1;
            this.dropSpeed = 15;
        }

        this.imageCounter = 1;
        this.shotSpeed = 15;
    }

    public void fireWeapon(int type) {
        if (type < 1 || type > 3) System.err.println("Not a Valid type");

        // (1 == laser, 2 == slow missile, 3 == fast missile)
        Rectangle box = new Rectangle();
        box.setBounds(xCoordinate, yCoordinate, width, height);

        double xCenter = box.getCenterX() - 3;
        double yCenter = box.getCenterY() - 3;
        if (type > 1) xCenter -= 5;

        weapon = new Shot(gui, "alien", type, (int) xCenter, (int) yCenter, gui.keyMaker);

        // allows us to increase shot speed!
        if (type != 3) weapon.speed = shotSpeed;
        else weapon.speed = shotSpeed + 10;

        gui.shotList.add(weapon);
    }

    public void draw(Graphics2D g2d) {
        super.draw(g2d);

        if (isDoneExploding) this.isDead = true;
        imageCounter++;
    }

    public void update() {
        if (wentOffScreen()) this.isDead = true;
//        if (!type.equals("ufo")) checkBorder();
        animate();
        xCoordinate += speed;
    }

    private void checkBorder() {
        boolean hitRightSide = goingRight && (xCoordinate > gui.screenWidth - width - 4);
        boolean hitLeftSide = !goingRight && (xCoordinate < 8);

        if (hitRightSide) {
            for (Alien enemy : gui.badGuys) {
                if (!enemy.type.equals("ufoL") && !enemy.type.equals("ufoR") && !enemy.isExploding) {
                    enemy.aboutFace();
                }
            }
            goingRight = false;

        } else if (hitLeftSide) {
            for (Alien enemy : gui.badGuys) {
                if (!enemy.type.equals("ufoL") && !enemy.type.equals("ufoR") && !enemy.isExploding) {
                    enemy.aboutFace();
                }
            }
            goingRight = true;
        }

        if (yCoordinate + height > gui.screenHeight) {
            gui.gameOver = true;
        }
    }

    public boolean wentOffScreen() {
        return (xCoordinate < -gui.tileSize || xCoordinate > gui.screenWidth);
    }

    private void animate() {
        if (imageCounter % 15 == 0) {
            if (curImage.equals(image1)) curImage = image2;
            else curImage = image1;

        }
    }

    public void aboutFace() {
        yCoordinate += dropSpeed;
        speed = -speed;
    }

    public void goBoom() {
        super.goBoom();
        if (!type.equals("ufoL") && !type.equals("ufoR")) gui.reduceBadGuys();
        SoundManager.playSound(audioFile2);
    }

    public boolean isHit(Entity other) {
        Rectangle me = new Rectangle();
        Rectangle you = new Rectangle();

        if ((other instanceof Shot shot) &&
                  (shot.owner.equals("player")) &&
                        (shot.type == 2)) {

            // Create huge blast area!!!
            int bigWidth = shot.width * 4;
            int bigHeight = shot.height * 3;
            int bigX = shot.xCoordinate - width;
            int bigY = shot.yCoordinate - height;

            me.setBounds(xCoordinate, yCoordinate, width, height);
            you.setBounds(bigX, bigY, bigWidth, bigHeight);

        } else {

            // Easiest to Hit
            switch (this.type) {
                case "bottom" -> {
                    return super.isHit(other);
                }

                // Harder to Hit
                case "mid", "ufoL", "ufoR" -> {
                    me.setBounds(xCoordinate, yCoordinate, width, height);
                    you.setBounds(other.xCoordinate + (other.width / 4),
                            other.yCoordinate, (other.width / 2), other.height);
                }

                // Hardest to Hit
                case "top" -> {
                    me.setBounds(xCoordinate + (width / 4), yCoordinate, (width / 2), height);
                    you.setBounds(other.xCoordinate + (other.width / 4),
                            other.yCoordinate, (other.width / 2), other.height);
                }
            }
        }

        return me.intersects(you);
    }
}
