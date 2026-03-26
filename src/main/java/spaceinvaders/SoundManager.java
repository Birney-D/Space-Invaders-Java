package spaceinvaders;

import javax.sound.sampled.*;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SoundManager {

    private static final ExecutorService soundThreadPool = Executors.newFixedThreadPool(8);

    // Submit a sound task to the thread pool
    public static void playSound(String fileDir) {
        soundThreadPool.submit(() -> {
            try {
                AudioInputStream soundSource = AudioSystem.getAudioInputStream(
                        Objects.requireNonNull(SoundManager.class.getResourceAsStream(fileDir))
                );

                Clip soundClip = AudioSystem.getClip();
                soundClip.open(soundSource);
                soundClip.start();
                soundClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        soundClip.close(); // Close when done playing
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Static method to shutdown the sound thread pool
    public static void shutdown() {
        soundThreadPool.shutdown();  // Stop accepting new tasks
        try {
            if (!soundThreadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                soundThreadPool.shutdownNow(); // Force shutdown if tasks don't finish
            }
        } catch (InterruptedException e) {
            soundThreadPool.shutdownNow();
            Thread.currentThread().interrupt();  // Restore interrupted status
        }
    }
}

