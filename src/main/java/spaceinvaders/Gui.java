package spaceinvaders;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


public class  Gui {

    private JFrame frame;
    private JLabel label;

    private int xPos = 25;
    private int yPos = 25;

    public void go() {
        // Creating instances of everything:
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//        label = new JLabel("I'm a label");

//        JButton labelButton = new JButton("Change Label");
//        labelButton.addActionListener(event -> label.setText("Ouch!"));

//        DrawingBoard myCanvas = new DrawingBoard();

//        JButton colorButton = new JButton("Change Color");
//        colorButton.addActionListener(event -> myCanvas.repaint());


        Canvas doodle = new Canvas();

        // Adding these widgets to the frame:

        frame.getContentPane().add(BorderLayout.CENTER, doodle);

//        frame.getContentPane().add(BorderLayout.CENTER, myCanvas);
//        frame.getContentPane().add(BorderLayout.SOUTH, colorButton);
//        frame.getContentPane().add(BorderLayout.EAST, labelButton);
//        frame.getContentPane().add(BorderLayout.WEST, label);

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

    public static void main(String[] args) {
        try {
            // Load the audio file
            File audioFile = new File("./Resources/player/shoot.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            // Define the desired format (match the format from your audio file)
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            // Get the source data line
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            // Buffer to hold audio data
            byte[] buffer = new byte[4096];
            int bytesRead;

            // Play the audio
            while ((bytesRead = audioStream.read(buffer, 0, buffer.length)) != -1) {
                line.write(buffer, 0, bytesRead);
            }

            // Close the resources
            line.drain();
            line.close();
            audioStream.close();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
