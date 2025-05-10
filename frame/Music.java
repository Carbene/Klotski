package frame;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Music {
    private  Clip bgmClip;
    private Clip soundEffectPressingButtonClip;
    private Clip soundEffectMovingBlockClip;
    private boolean isBgmPlaying = false;
    private Thread bgmPlaybackThread;
    private static final String bgmPath = "src/frame/theme/bgm.wav";
    private static final String soundEffectPressingButtonPath = "src/frame/theme/pressingButton.wav";
    private static final String soundEffectMovingBlockPath = "src/frame/theme/movingBlock.wav";
    private boolean isBGMEnabled = true;

    public void playBGM() {
        if (bgmPath == null || bgmPath.isEmpty()) {
            System.err.println("BGM path is not set.");
            return;
        }
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(this.bgmPath));
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            if (!AudioSystem.isLineSupported(info) || !isBGMEnabled) {
                System.err.println("Unsupported audio format for BGM");
                return;
            }

            bgmClip = (Clip) AudioSystem.getLine(info);
            bgmClip.open(audioStream);
            setupLoopingBGM();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing BGM: " + e.getMessage());
        }
    }

    private void setupLoopingBGM() {
        if (bgmClip == null) return;
        bgmClip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP && isBgmPlaying) {
                if (bgmClip.isOpen()) {
                    bgmClip.setFramePosition(0);
                    bgmClip.start();
                }
            }
        });
    }

    public void startBgmPlayback() {
        if (bgmClip != null && !isBgmPlaying) {
            isBgmPlaying = true;
            bgmPlaybackThread = new Thread(() -> {
                bgmClip.start();
            });
            bgmPlaybackThread.start();
        }
    }

    public void stopBgmPlayback() {
        if (bgmClip != null && isBgmPlaying) {
            isBgmPlaying = false;
            bgmClip.stop();
            bgmClip.close();
            if (bgmPlaybackThread != null && bgmPlaybackThread.isAlive()) {
                bgmPlaybackThread.interrupt();
            }
        }
    }

    public void playSoundEffect() {
        if (soundEffectPressingButtonPath == null || soundEffectPressingButtonPath.isEmpty()) {
            System.err.println("Sound effect path is not set.");
            return;
        }
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(this.soundEffectPressingButtonPath));
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("Unsupported audio format for sound effect");
                return;
            }

            soundEffectPressingButtonClip = (Clip) AudioSystem.getLine(info);
            soundEffectPressingButtonClip.open(audioStream);
            soundEffectPressingButtonClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    if (soundEffectPressingButtonClip.isOpen()) {
                        soundEffectPressingButtonClip.close(); // 播放完毕后关闭
                    }
                }
            });
            soundEffectPressingButtonClip.start(); // 播放一次
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing sound effect: " + e.getMessage());
        }
    }

    public boolean isBgmPlaying() {
        return isBgmPlaying;
    }

    // 可选：添加一个方法来切换BGM播放状态
    public void toggleBgmPlayback() {
        if (isBgmPlaying) {
            stopBgmPlayback();
        } else {
            playBGM(); // 确保在播放前已加载
            startBgmPlayback();
        }
    }
}