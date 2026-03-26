package spaceinvaders;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


// Power up: invincibility!
// killStreak of 10, 20, 30, etc. extra points + label!!!
// switching levels screen!

public class Game implements Runnable {

    public final JFrame frame;

    public Thread gameThread;

    public GameScreen screen;
    public int screenWidth;
    public int screenHeight;

    public JLabel scoreCounter;

    public JLabel highScoreTracker;
    public JLabel shotCounter;

    public JLabel announcements;
    public int highScore;

    public KeyHandler keyMaker;

    public ActionHandler actionJackson;

    public Ship player1;

    public ArrayList<Alien> badGuys;

    // Sentinels:
    private Alien leftMostAlien;
    private Alien rightMostAlien;
    private Alien bottomMostAlien;

    // SpokesPerson:
    private Alien bottomSpeaker;
    private Alien middleSpeaker;
    private Alien topSpkeaker;

    public ArrayList<Shot> shotList;

    private final ArrayList<Barricade> barricades;

    public JButton startButton;


    public int tileSize;
    public int level;

    // Time Stuff:
    private int updateDelay;
    private final int shotDelay;
    private long lastShotTime;
    private long lastSpecialShot;
    private long lastUFOTime;

    private boolean okToIncreaseDiff;
    public boolean gameOver, iLost;

    private int badGuysRemaining;
    private long specialShotDelay;

    public int killStreak;



    public Game() {
        tileSize = 50;

        updateDelay = 25;
        lastShotTime = 0;
        lastSpecialShot = 0;
        lastUFOTime = 0;
        shotDelay = 800;
        specialShotDelay = 4000;

        highScore = 0;
        killStreak = 0;

        okToIncreaseDiff = false;
        gameOver = true;
        iLost = false;

        frame = new JFrame();
        frame.setSize((tileSize * 18), (tileSize * 16));

        keyMaker = new KeyHandler();
        frame.addKeyListener(keyMaker);

        screen = new GameScreen();
        // TitleScreen go away!

        // Allows us to set components where we want
        screen.setLayout(null);
        // titleScreen.setLayout(null) -> go away!

        // (885, 762)
        screenWidth = 885;
        screenHeight = 762;

        // JLabel (score)
        int scoreCountLength = tileSize * 4;
        scoreCounter = new JLabel("SCORE: " + highScore);
        scoreCounter.setBounds(10, 10, scoreCountLength, 20);
        scoreCounter.setForeground(Color.magenta);
        scoreCounter.setFont(new Font("Serif", Font.BOLD, 20));
        screen.add(scoreCounter);

        // HighScoreTracker:
        int hiScoreCountLength = scoreCountLength * 2;
        highScoreTracker = new JLabel("HIGH SCORE: ");
        highScoreTracker.setBounds(screenWidth - (tileSize * 5), 10, hiScoreCountLength, 20);
        highScoreTracker.setForeground(Color.red);
        highScoreTracker.setFont(new Font("Serif", Font.BOLD, 20));
        screen.add(highScoreTracker);

        // ShotCounter:
        shotCounter = new JLabel("SHOTS: ");
        shotCounter.setBounds(screenWidth - 125, 730, scoreCountLength, 15);
        shotCounter.setForeground(Color.WHITE);
        shotCounter.setFont(new Font("Serif", Font.BOLD, 15));
        screen.add(shotCounter);

        // Announcer:
        announcements = new JLabel("MISSILES (x3)");
        announcements.setBounds(screenWidth / 3 + 75, screenHeight / 2 + 150, scoreCountLength, 20);
        announcements.setForeground(Color.MAGENTA);
        announcements.setFont(new Font("Serif", Font.BOLD, 20));
        announcements.setVisible(false);
        screen.add(announcements);


        // StartButton:
        startButton = new JButton("  START GAME  ");
        startButton.setBounds(screenWidth / 4 + 30, screenHeight - 100, tileSize * 5 + 30, 30);
        startButton.setBorderPainted(false);
        screen.add(startButton);  // titleScreen.add(startButton) -> gameScreen
        startButton.setForeground(Color.RED);
        startButton.setBackground(new Color(0, 0, 0, 0));
        startButton.setFont(new Font("Serif", Font.ITALIC, 30));
        actionJackson = new ActionHandler(this);
        startButton.addActionListener(actionJackson);
        startButton.setEnabled(true);


        // Testing (x,y location)
//        screen.addMouseListener(new MouseHandler(this));

        badGuys = new ArrayList<>();

        shotList = new ArrayList<>();

        barricades = new ArrayList<>();

        frame.getContentPane().add(screen);  // add gameScreen
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


        frame.setTitle("SPACE INVADERS");
        frame.setLocationRelativeTo(null); // Centers the frame
        frame.setResizable(false);
        // Add a WindowListener to detect when the window is closing
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Call SoundManager.shutdown() when the window is closing
                SoundManager.shutdown();
                System.exit(0);  // Exit the game (this will terminate the app)
            }
        });
        frame.setVisible(true);

        startIntroDudes();
    }

    private void startIntroDudes() {
        while (gameOver) {
            long currentTime = System.currentTimeMillis();
            int randomDude = new Random().nextInt(1, 5);

            // Making random aliens:
            if (currentTime - lastShotTime > 12000) {
                Alien dude = new Alien(this, randomDude, 10, 10);
                dude.speed = 3;
                dude.dropSpeed = 50;

                badGuys.add(dude); // introDudes.add(dude);
                lastShotTime = currentTime;
            }

            // Making random shots fire at aliens:
            if (currentTime - lastSpecialShot > 1000) {
                Random rando = new Random();
                int randoX = rando.nextInt(10, screenWidth);
                int randoShot = rando.nextInt(1,3);
                Shot killShot = new Shot(this, "player", randoShot, randoX, 700, keyMaker);
                killShot.speed = 25;
                shotList.add(killShot);

                lastSpecialShot = currentTime;
            }

            // Making aliens move around screen:
            badGuys.removeIf(alien -> alien.isDead);

            for (Alien dude: badGuys) { // *** introDudes
                if (!dude.isDead && !dude.isExploding) {

                    // Reach sides = about face
                    if (dude.xCoordinate < 8 || dude.xCoordinate > (screenWidth - dude.width - 4)) {
                        dude.aboutFace();
                    }
                    // Reaches bottom = dead
                    if (dude.yCoordinate + dude.height >= screenHeight) {
                        dude.isDead = true;
                    }
                    // Move em
                    dude.update();
                    if (!dude.type.equals("ufoL") && !dude.type.equals("ufoR")) {
                        if (dude.imageCounter % 45 == 0) {
                            SoundManager.playSound(dude.audioFile1);
                        }
                    } else if (dude.xCoordinate <= 15 || dude.xCoordinate >= screenWidth - dude.width - 5) {
                        SoundManager.playSound(dude.audioFile1);
                    }
                }
            }

            // Move em
            updateShots();

            // Collisions:
            shotList.forEach(shot -> badGuys.stream()
                    .filter(dude -> !dude.isDead && !dude.isExploding && dude.isHit(shot))
                    .forEach(dude -> {
                        dude.goBoom();
                        shot.isDead = true;
                    }));

            screen.repaint();

            try {
                TimeUnit.MILLISECONDS.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Dont need run()
    public void run() {
        setScene();
        go();
    }

    public void clearScene() {
        frame.setVisible(false);
        player1 = null;
        shotList.clear();
        badGuys.clear();
        barricades.clear();
    }

    public void setScene() {
        player1 = new Ship(this, keyMaker);
        scoreCounter.setText("SCORE: " + player1.score);
        shotCounter.setText("SHOTS: " + player1.shotCount);
        lastShotTime = lastSpecialShot = lastUFOTime = 0;
        this.level = 0;

        createEnemyFleet();
        assignSentinels();  // ******* Glitch!!!
        assignSpokesMen();  // Sound effects!
        buildBarricades();

        frame.setVisible(true);
    }

    private void buildBarricades() {
        // 110, 385, 660 x 630
        // 115 x 55
        barricades.add(new Barricade(this, 110, 630));
        barricades.add(new Barricade(this, 385, 630));
        barricades.add(new Barricade(this, 660, 630));
    }

    private void createEnemyFleet() {
        int xStartPoint = tileSize;
        int yStartPoint = tileSize + (level * 30);

        int numOfRows = 5;
        int numOfCols = 11;

        int gap = 60;

        // Create top row:
        for (int i = 0; i < numOfCols; i++) {
            badGuys.add(new Alien(this, 3, xStartPoint + (gap * i), yStartPoint));
        }

        // Create middle rows:
        for (int i = 1; i < 3; i++) {
            for (int j = 0; j < numOfCols; j++) {
                badGuys.add(new Alien(this, 2, xStartPoint +
                        (gap * j), yStartPoint + (gap * i)));
            }
        }

        // Creating bottom rows:
        for (int i = 3; i < numOfRows; i++) {
            for (int j = 0; j < numOfCols; j++) {
                badGuys.add(new Alien(this, 1, xStartPoint +
                        (gap * j), yStartPoint + (gap * i)));
            }
        }
        // at this point, no ufo's:
        badGuysRemaining = badGuys.size();
    }

    private void assignSentinels() {
        int lowestX = 900;
        int highestX = 0;
        int highestY = 0;

        for (Alien enemy : badGuys) {

            if (enemy.xCoordinate < lowestX) {
                lowestX = enemy.xCoordinate;
                leftMostAlien = enemy;
            }

            if (enemy.xCoordinate > highestX) {
                highestX = enemy.xCoordinate;
                rightMostAlien = enemy;
            }

            if (enemy.yCoordinate > highestY) {
                highestY = enemy.yCoordinate;
                bottomMostAlien = enemy;
            }
        }
    }

    private void updateSentinels() {
        int lowestX = 900;
        int highestX = 0;
        int highestY = 0;

        if (leftMostAlien.isDead) {

            // Searching for lft replacement:
            for (Alien leftReplacement : badGuys) {
                if (!leftReplacement.type.equals("ufoL") && !leftReplacement.type.equals("ufoR")) {
                    if (!leftReplacement.isDead && !leftReplacement.isExploding) {

                        if (leftReplacement.xCoordinate < lowestX) {
                            lowestX = leftReplacement.xCoordinate;
                            leftMostAlien = leftReplacement;
                        }
                    }
                }
            }
        }

        if (rightMostAlien.isDead) {

            // Right replacement:
            for (Alien rightReplacement : badGuys) {
                if (!rightReplacement.type.equals("ufoL") && !rightReplacement.type.equals("ufoR")) {
                    if (!rightReplacement.isDead &&!rightReplacement.isExploding) { //**** isDead!!!

                        if (rightReplacement.xCoordinate > highestX) {
                            highestX = rightReplacement.xCoordinate;
                            rightMostAlien = rightReplacement;
                        }
                    }
                }
            }
        }

        if (bottomMostAlien.isDead) {

            // Bottom replacement:
            for (Alien bottomReplacement : badGuys) {
                if (!bottomReplacement.type.equals("ufoL") && !bottomReplacement.type.equals("ufoR")) {
                    if (!bottomReplacement.isDead && !bottomReplacement.isExploding) {

                        if (bottomReplacement.yCoordinate > highestY) {
                            highestY = bottomReplacement.yCoordinate;
                            bottomMostAlien = bottomReplacement;
                        }
                    }
                }
            }
        }
    }

    private void assignSpokesMen() {
        for (Alien enemy : badGuys) {
            switch (enemy.type) {
                case "top" -> topSpkeaker = enemy;
                case "mid" -> middleSpeaker = enemy;
                case "bottom" -> bottomSpeaker = enemy;
            }
        }
    }

    private void updateSpokesMen() {
        if (topSpkeaker.isDead) {
            for (Alien replacement : badGuys) {
                if (replacement.type.equals("top") && !replacement.isDead && !replacement.isExploding) {
                    topSpkeaker = replacement;
                }
            }
        }

        if (middleSpeaker.isDead) {
            for (Alien replacement : badGuys) {
                if (replacement.type.equals("mid") && !replacement.isDead && !replacement.isExploding) {
                    middleSpeaker = replacement;
                }
            }
        }

        if (bottomSpeaker.isDead) {
            for (Alien replacement : badGuys) {
                if (replacement.type.equals("bottom") && !replacement.isDead && !replacement.isExploding) {
                    bottomSpeaker = replacement;
                }
            }
        }
    }

    public void go() { deployAliens(); }

    private void deployAliens() {
        // Main game loop: starts alien marching;

        while (!gameOver) {
            check4GameOver();

            if (leftMostAlien.isDead || rightMostAlien.isDead || bottomMostAlien.isDead) { // ******
                updateSentinels();
            }
            if (bottomSpeaker.isDead || middleSpeaker.isDead || topSpkeaker.isDead) {
                updateSpokesMen();
            }

            startMysteryShips();
            startShooting();
//            testShoot(); // testing purposes!
            updateEntities();
            checkForCollisions();
            screen.repaint();

            try {
                TimeUnit.MILLISECONDS.sleep(updateDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (badGuysRemaining == 0) youWin();
        else youLose();
    }

    private void check4GameOver() {
        boolean alienReachesBottom =
                (bottomMostAlien.yCoordinate +
                        bottomMostAlien.height) >= screenHeight;

        if (player1.isDead || badGuysRemaining == 0 || alienReachesBottom) { // ****  || alien reaches bottom ***** \\
            gameOver = true;
        }
    }

    private void startMysteryShips() {
        long curTime = System.currentTimeMillis();
        long ufoTimer = curTime - lastUFOTime;
        Random randy = new Random();

        // Choose left -> right or left <- right:
        int startX = -tileSize;
        int type = randy.nextInt(4, 6);
        if (type == 5) startX = screenWidth;

        Alien mysteryDude = null;
        if (!ufoPresent() && ufoTimer > 10000) {
            mysteryDude = new Alien(this, type, startX, 10);
            if (type == 5) mysteryDude.speed = -mysteryDude.speed;

            badGuys.add(mysteryDude);
            SoundManager.playSound(mysteryDude.audioFile1);
            lastUFOTime = curTime;
        }
    }

    private boolean ufoPresent() {
        return badGuys.stream().anyMatch(alien -> alien.type.equals("ufoL") || alien.type.equals("ufoR"));
    }

    private void startShooting() {

        long currentTime = System.currentTimeMillis();
        long shotTimer = currentTime - lastShotTime;

        if (shotTimer > shotDelay) {

            Random rando = new Random();
            Alien randomEnemy = badGuys.get(rando.nextInt(badGuys.size()));

            while (randomEnemy.isDead || randomEnemy.isExploding) {
                randomEnemy = badGuys.get(rando.nextInt(badGuys.size()));
            }

            int type = 1;
            if (currentTime - lastSpecialShot > specialShotDelay) {
                type = rando.nextInt(2,4);
                lastSpecialShot = currentTime;
            }

            for (Alien enemy : badGuys) {
                if ((enemy.type.equals("ufoL") || enemy.type.equals("ufoR")) && enemy.imageCounter % 6 == 0) {
                    enemy.fireWeapon(type);
                }
            }

            randomEnemy.fireWeapon(type);
            lastShotTime = currentTime;
        }
    }

    private void testShoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime > 1000) {
            bottomMostAlien.fireWeapon(1);
            lastShotTime = currentTime;
        }
    }

    private void updateEntities() {
        updateEnemies();
        player1.update();
        updateShots();
        updateBarricades();
    }

    private void updateEnemies() {
        badGuys.removeIf(alien -> alien.isDead);

        checkPerimeter(); // ***

        badGuys.stream()
                .filter(alien -> !alien.isExploding)
                .forEach(Alien::update);

        badGuys.stream()
                .filter(alien -> (alien.equals(bottomSpeaker) || alien.equals(middleSpeaker) ||
                        alien.equals(topSpkeaker) || alien.type.equals("ufoR")) && alien.imageCounter % 30 == 0)
                .forEach(alien -> SoundManager.playSound(alien.audioFile1));
    }

    private void checkPerimeter() {
        boolean reachLeftSide = leftMostAlien.xCoordinate < 8;
        boolean reachRightSide = rightMostAlien.xCoordinate > (screenWidth - rightMostAlien.width - 4);

        // Check perimeter:
        if (reachLeftSide || reachRightSide) {
            for (Alien badGuy : badGuys) {
                if (!badGuy.type.equals("ufoL") && !badGuy.type.equals("ufoR")) {
                    if (!badGuy.isDead && !badGuy.isExploding) {

                        badGuy.aboutFace();
                    }
                }
            }
        }
    }

    // need a spokesperson for each alien type to make the noise:
    // we update them as they die off like the sentinels
    // Then we say if image count % 30 == 0, alien.makeNoise

    private void updateShots() {
        shotList.removeIf(shot -> shot.isDead);
        shotList.stream()
                .filter(shot -> !shot.isExploding)
                .forEach(Shot::update);
    }

    private void updateBarricades() {
        barricades.removeIf(barricade -> barricade.isDead);
    }

    // ########## COLLISIONS ########################## \\
    private void checkForCollisions() {
        checkEnemyCollisions();
        check4DifficultyX();
        checkShipCollisions();
        checkShotCollisions();
        checkSpriteCollisions();
        checkBarricadeCollisions();
    }

    private void check4DifficultyX() {
        if (okToIncreaseDiff) {
            if (badGuysRemaining == 37) increaseDiff(1, 0, 0);
            else if (badGuysRemaining == 19) increaseDiff(1, 10, 0);
            else if (badGuysRemaining == 1) increaseDiff(5, 10, 3);
        }
    }

    private void increaseDiff(double speed, int dropSpeed, int shotSpeed) {
        for (Alien enemy : badGuys) {
            if (enemy.speed > 0) {
                enemy.speed += speed;
            } else enemy.speed -= speed;

            enemy.dropSpeed += dropSpeed;
            enemy.shotSpeed += shotSpeed;
        }

        okToIncreaseDiff = false;
    }

    public void reduceBadGuys() {
        badGuysRemaining--;
        killStreak++;

        if (badGuysRemaining == 37 ||
                badGuysRemaining == 19 ||
                    badGuysRemaining == 1) {

            okToIncreaseDiff = true;
        }
    }

    private void checkEnemyCollisions() {
        for (int i = 0; i < shotList.size();) {
            Shot shot = shotList.get(i);
            if (shot.owner.equals("player")) {
               if (shot.type == 1) {

                    for (Alien enemy : badGuys) {
                        if (!enemy.isExploding) { // !enemy.isDead
                            if (enemy.isHit(shot)) {

                                enemy.goBoom();
                                shot.isDead = true;
                                player1.collectBounty(enemy);
                                i++;
                            }
                        }
                    }

                } else if (!keyMaker.bombPress && !shot.isExploding) {
                   boolean hitNothing = true;

                   for (Alien enemy : badGuys) {
                       if (!enemy.isExploding) { // !enemy.isDead
                           if (enemy.isHit(shot)) {

                               hitNothing = false;
                               shot.isDead = true;
                               enemy.goBoom();
                               player1.collectBounty(enemy);
                           }
                       }

                   }

                   if (hitNothing) {
                       shot.goBoom();
                       killStreak = 0;
                   }
               }
            }

            i++;
        }
    }

    private void checkShipCollisions() {
        for (Shot shot : shotList) {
            if (shot.owner.equalsIgnoreCase("alien")) {
                if (!player1.isExploding && player1.isHit(shot)) {

                    player1.goBoom();
                    shot.isDead = true;
                    break;
                }
            }
        }
    }

    private void checkShotCollisions() {
        for (Shot shot1 : shotList) {
            if (shot1.owner.equals("player") && !shot1.isExploding) {

                for (Shot shot2 : shotList) {
                    if (shot2.owner.equals("alien") && !shot2.isExploding) {

                        if (shot1.isHit(shot2)) {

                            if ((shot1.type == 1 && shot2.type < 3) ||
                                 shot1.type == 2 && shot2.type == 3) {

                                // both shots go boom!
                                shot1.goBoom();
                                shot2.goBoom();
                                SoundManager.playSound(shot2.audioFile);

                            } else if (shot1.type == 1 && shot2.type == 3) {

                                // just mine goes boom!!
                                shot1.goBoom();
                                SoundManager.playSound(shot1.audioFile);

                                // just his goes boom!!!
                            } else {
                                shot2.goBoom();
                                SoundManager.playSound(shot2.audioFile);
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkSpriteCollisions() {

        for (Alien enemy : badGuys) {
            if (!enemy.isDead && !enemy.isExploding) {
                if (player1.isHit(enemy)) {

                    enemy.goBoom();
                    player1.die();
                }
            }
        }
    }

    private void checkBarricadeCollisions() {

        // Shot collision (top & bottom):
        for (Barricade bCade : barricades) {
            if (!bCade.isDead) {

                // Shots collide:
                for (Shot shot : shotList) {
                    if (!shot.isExploding) {
                        if (shot.owner.equals("alien") && bCade.isHit(shot)) {

                            ArrayList<Rectangle> topOfBarricade = bCade.getPointsOfImpact(shot);

                            shot.goBoom();
                            bCade.blastAHole(topOfBarricade, shot.type, true);

                        } else if (shot.owner.equals("player") && bCade.isHit(shot)) {

                            ArrayList<Rectangle> bottomOfBarricade = bCade.getPointsOfImpact(shot);

                            shot.goBoom();
                            bCade.blastAHole(bottomOfBarricade, shot.type, false);
                        }
                    }
                }

                for (Alien enemy : badGuys) {
                    if (!enemy.isDead) {
                        if (bCade.isHit(enemy)) {

                            bCade.blastAHole(bCade.getPointsOfImpact(enemy), enemy.strength, true);
                        }
                    }
                }
            }
        }
    }

    // #################### DRAWING THINGS ###########################

    private void drawEnemies(Graphics2D g2d) { badGuys.forEach(badGuy -> badGuy.draw(g2d)); }

    private void drawShots(Graphics2D g2d) {
        shotList.forEach(shot -> shot.draw(g2d));
    }

    private void drawBarricades(Graphics2D g2d) {
        barricades.forEach(barricade -> barricade.draw(g2d));
    }


    public void youWin() {

        frame.setVisible(false);
        shotList.clear();
        badGuys.clear();
        barricades.clear();

        lastShotTime = lastSpecialShot = lastUFOTime = 0;
        player1.score = 0;

        createEnemyFleet();
        assignSentinels();
        assignSpokesMen();
        buildBarricades();

        // Resets at 10:
        gameOver = false;    
        this.level = (level + 1) % 10;
        frame.setVisible(true);
        go();  
    }

    public void youLose() {
        clearScene();
        iLost = true;
        // Dont need to switch screens
        startButton.setVisible(true);
        startButton.setText("PLAY AGAIN");
        lastShotTime = lastSpecialShot = 0;
        frame.setVisible(true);
        startIntroDudes();
    }


    // ###############################################################################################################



    private class GameScreen extends JPanel {
        public BufferedImage backGround;
        private BufferedImage intro;
        private BufferedImage gameInfo;
        private BufferedImage youLose;

        public GameScreen() {
            this.setDoubleBuffered(true);

            try {
                backGround = ImageIO.read(Objects.requireNonNull(getClass()
                        .getResourceAsStream("/general/space.png")));

                intro = ImageIO.read(Objects.requireNonNull(getClass()
                        .getResourceAsStream("/general/intro.png")));

                youLose = ImageIO.read(Objects.requireNonNull(getClass()
                        .getResourceAsStream("/general/loser.png")));

                gameInfo = ImageIO.read(Objects.requireNonNull(getClass()
                        .getResourceAsStream("/general/info.png")));

            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        public void paintComponent(Graphics g) {

            Graphics2D g2d = (Graphics2D) g;

            // Don't need super.paintComponent(g) since we are painting a background.
            // otherwise we would get smearing!
            drawBackGround(g2d);
            if (player1 != null) player1.draw(g2d);
            drawEnemies(g2d);
            drawShots(g2d);
            drawBarricades(g2d);
        }

        public void drawBackGround(Graphics2D g2d) {
            g2d.drawImage(backGround, 0, 0, frame.getWidth(), frame.getHeight(), null);

            if (gameOver) {
                if (!iLost) {
                    g2d.drawImage(intro, 175, 50, 500, 250, null);
                    g2d.drawImage(gameInfo, 215, 350, 400, 250, null);

                } else g2d.drawImage(youLose, 200, 150, 400, 400, null);
            }
        }
    }

    // ############################################################################################################


    public static void main(String[] args) {
        new Game();
    }
}
