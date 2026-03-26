package spaceinvaders;

import java.awt.*;

public class Shot extends Entity {

    public String owner, audioFile;

    public int type;

    public KeyHandler keyMaker;

    public Shot(Game g, String owner, int type, int xPos, int yPos, KeyHandler key) {
        // Include a ship/alien desig to load different images.
        this.gui = g;
        this.owner = owner.toLowerCase();
        this.audioFile = "/alien/invaderkilled.wav";
        this.type = type;
        this.xCoordinate = xPos;
        this.yCoordinate = yPos;
        this.keyMaker = key;

        int ownerID = 0;
        if (owner.equals("alien")) ownerID = 1;
        else if (owner.equals("player")) ownerID = 2;

        if (ownerID != 1 && ownerID != 2) System.err.println("invalid owner");
        loadImages("/weapons/laser" + ownerID + ".png", "/weapons/missile" + ownerID + ".png");

        // 1 == laser, 2 == missile, 3 = fast missile
        if (this.type == 1) this.curImage = image1;
        else this.curImage = image2;

        setInitialValues();
    }

    public void setInitialValues() {
        super.setInitialValues();

        // laser
        if (type == 1) {
            this.width = 5;
            this.height = 30;

        } else if (owner.equals("player")) {
            // missiles
            this.width = gui.tileSize / 3;
            this.height = gui.tileSize / 2 + 10;
        } else {
            // enemy missiles
            this.width = gui.tileSize / 4;
            this.height = gui.tileSize / 2 + 10;
        }

        if (owner.equals("player")) {
            if (type == 2) this.speed = 15;
            else this.speed = 20;
        }
    }

    public void draw(Graphics2D g2d) {
        super.draw(g2d);

        if (isDoneExploding) this.isDead = true;
    }

    public void update() {
        if (wentOffScreen()) {
            this.isDead = true;

            if (owner.equals("player")) {
                gui.killStreak = 0;
            }
        }

        if (owner.equals("alien")) {
            this.yCoordinate += speed;
        } else this.yCoordinate -= speed;
    }

    public boolean wentOffScreen() {
        return yCoordinate <= -height ||
                yCoordinate >= gui.screenHeight;
    }

    public boolean isHit(Entity other) {
        if (owner.equals("player") && type == 2 && !keyMaker.bombPress) {
            Rectangle me = new Rectangle();
            Rectangle you = new Rectangle();

            int bigWidth = width * 4;
            int bigHeight = height * 3;
            int bigX = xCoordinate - width;
            int bigY = yCoordinate - height;

            me.setBounds(bigX, bigY, bigWidth, bigHeight);
            you.setBounds(other.xCoordinate, other.yCoordinate, other.width, other.height);

            return me.intersects(you);

        } else return super.isHit(other);
    }
}
