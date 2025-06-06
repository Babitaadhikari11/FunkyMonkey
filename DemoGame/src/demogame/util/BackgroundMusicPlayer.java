package demogame.util;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class BackgroundMusicPlayer {
    private static Clip clip;
    private static boolean isPlaying = false;
    private static String currentFilePath;

    public static void playLoop(String filePath) {
        try {
            // Save path
            currentFilePath = filePath;

            // Stop any existing music
            stop();

            // Load and start new clip
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("Audio file not found: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            isPlaying = true;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close();
            clip = null;
        }
        isPlaying = false;
    }

    public static boolean isPlaying() {
        return isPlaying;
    }
}
