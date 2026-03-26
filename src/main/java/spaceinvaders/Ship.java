package spaceinvaders;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Ship extends Entity {
    public KeyHandler keyMaker;
    public Image image3;

    public Shot weapon;

    public String audioFile1, audioFile2;

    public int shotCount;

    public int missilesRemaining;

    public int livesRemaining;

    public int score;
    public long lastShot;
    public long powerUPTime;

    public int shotDelay;

    public boolean missilesReceived, gotXtraLife, gotRapidFire, killStreakReached;

    public Ship(Game g, KeyHandler k) {
        this.gui = g;
        this.keyMaker = k;
        this.loadImages("/player/ship.png", "/player/shipL.png");

        this.image3 = new ImageIcon(Objects.requireNonNull(getClass()
                          .getResource("/player/shipR.png"))).getImage();

        this.audioFile1 = "/player/shoot.wav";
        this.audioFile2 = "/player/explosion.wav";

        this.curImage = image1;
        this.setInitialValues();
    }

    public void setInitialValues() {
        super.setInitialValues();

        // (55, 45)
        this.width = gui.tileSize + 5;
        this.height = gui.tileSize - 5;

        // (415, 700)
        this.xCoordinate = (gui.screenWidth / 2) - (this.width / 2) - 5;
        this.yCoordinate = (gui.screenHeight - 62);

        this.livesRemaining = 3;
        this.missilesRemaining = 0;
        this.missilesReceived = this.gotXtraLife =
                this.gotRapidFire = killStreakReached = false;
        this.score = 0;
        this.speed = 10;
        this.shotDelay = 700;
        this.shotCount = 0;
        this.powerUPTime = 0;
    }

    public void fireWeapon(int type) {
        if (type != 1 && type != 2) System.err.println("Invalid weapon type");

        Rectangle box = new Rectangle();
        box.setBounds(xCoordinate, yCoordinate, width, height);
        double xCenter = box.getCenterX() - 5;

        weapon = new Shot(gui, "player", type, (int) xCenter, yCoordinate, keyMaker);
        gui.shotList.add(weapon);
        SoundManager.playSound(audioFile1);

        // only equals zero every 15 shots!
        shotCount = (shotCount + 1) % 15;
        gui.shotCounter.setText("SHOTS: " + shotCount);
    }

    public void draw(Graphics2D g2d) {
        super.draw(g2d);

        if (isDoneExploding) livesRemaining--;
        drawLivesRemaining(g2d);
        if (livesRemaining == 0) isDead = true;
    }

    public void drawLivesRemaining(Graphics2D g2d) {
        // Used to draw the ship icons for number of lives remaining
        int livesX = gui.tileSize / 2;
        int livesY = gui.screenHeight - gui.tileSize;
        int lifeSize = 30;

        for (int i = 0; i < livesRemaining; i++) {
            g2d.drawImage(image1, livesX + (i * lifeSize), livesY,
                    lifeSize, lifeSize, null);
        }
    }

    public void update() {
        long curTime = System.currentTimeMillis();
        long powerUPTimer = (curTime - powerUPTime);
        int rightWing = (xCoordinate + width);

        if (isDead || isExploding) return;

        if (isDoneExploding) isDoneExploding = false;

        // Turns off the power up announcement after 2 sec:
        if (powerUPTimer > 2000) {
            gui.announcements.setVisible(false);
        }

        // Turns off Rapid Fire:
        if (powerUPTimer > 5000 && gotRapidFire) shotDelay = 750;

        // Left turn:
        if (keyMaker.leftPress && (xCoordinate >= 11)) {
            curImage = image2;
            xCoordinate -= speed;

        // Right turn:
        } else if (keyMaker.rightPress && rightWing < gui.screenWidth - 9) {
            curImage = image3;
            xCoordinate += speed;

        // Straight Ahead: (Also after explode, reset)
        } else curImage = image1;

        // Fire laser:
        if (keyMaker.firePress) {
            long shotTimer = (curTime - lastShot);

            if (shotTimer > shotDelay) {
                // bottom row of aliens == yCoordinate of 290
                fireWeapon(1);
                lastShot = System.currentTimeMillis();
            }
        }

        if (keyMaker.bombPress && missilesRemaining > 0 && !bombAirBorn()) {
            fireWeapon(2);
        }
    }

    public boolean bombAirBorn() {
        for (Shot shot : gui.shotList) {
            if (shot.owner.equals("player") && shot.type == 2 && !shot.isExploding) {
                return true;
            }
        }

        return false;
    }

    public boolean isHit(Entity other) {
        Rectangle me = new Rectangle();
        Rectangle you = new Rectangle();

        me.setBounds(xCoordinate, yCoordinate + height / 4, width, height / 2);
        you.setBounds(other.xCoordinate + (other.width / 4),
                other.yCoordinate, (other.width / 2), other.height);

        return me.intersects(you);
    }

    public void collectBounty(Alien enemy) {
        int multiplier;

        // Kill streak increases score multiplier:
        if (gui.killStreak >= 10 && gui.killStreak < 20 && !killStreakReached) {

            multiplier = 2;  
            gui.announcements.setText("KILL STREAK x 10");
            gui.announcements.setVisible(true);
            powerUPTime = System.currentTimeMillis();
            killStreakReached = true;

        } else if (gui.killStreak >= 20 && gui.killStreak < 30 && killStreakReached) {
            multiplier = 3;
            gui.announcements.setText("KILL STREAK x 20");
            gui.announcements.setVisible(true);
            powerUPTime = System.currentTimeMillis();
            killStreakReached = false;

        } else if (gui.killStreak >= 30) {
            multiplier = 4;
            gui.announcements.setText("KILL STREAK x 30");
            gui.announcements.setVisible(true);
            powerUPTime = System.currentTimeMillis();
            killStreakReached = true;
        } else {
            multiplier = 1;
            killStreakReached = false;
        }

        if ((enemy.type.equals("ufoL") || enemy.type.equals("ufoR")) && shotCount != 0) {
            this.score += (multiplier * 30);
        } else this.score += (multiplier * enemy.bounty);

        // update scoreCounter:
        gui.scoreCounter.setText("SCORE: " + score);

        if (score > gui.highScore) {
            gui.highScore = score;
            gui.highScoreTracker.setText("HIGH SCORE: " + gui.highScore);
        }

        if (score >= 500 && !missilesReceived) {
            claimMissiles();
        }

        if (score >= 1000 && !gotRapidFire) {
            claimRapidFire();
        }

        if (score >= 1500 && !gotXtraLife) {
            claimXtraLife();
        }
    }

    private void claimMissiles() {
        this.missilesRemaining = 3;
        this.missilesReceived = true;
        gui.announcements.setVisible(true);
        powerUPTime = System.currentTimeMillis();
    }

    private void claimRapidFire() {
        this.shotDelay = 100;
        this.gotRapidFire = true;
        gui.announcements.setText("RapidFire!!");
        gui.announcements.setVisible(true);
        powerUPTime = System.currentTimeMillis();
    }

    private void claimXtraLife() {
        this.livesRemaining++;
        this.gotXtraLife = true;
        gui.announcements.setText("LIFE UP!!");
        gui.announcements.setVisible(true);
        powerUPTime = System.currentTimeMillis();
    }

    public void die() {
        livesRemaining = 1;
        goBoom();
    }

    public void goBoom() {
        super.goBoom();
        SoundManager.playSound(audioFile2);
    }
}
