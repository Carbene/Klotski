package frame;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class BGM {
    private Clip bgmClip;
    private Clip soundEffectClip;
    private boolean isPlaying = false;
    private Thread playbackThread;
    private String bgmPath;
    private String soundEffectPath;

    // 初始化音乐播放
    public void play() {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(this.bgmPath));
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("Unsupported audio format");
                return;
            }

            bgmClip = (Clip) AudioSystem.getLine(info);
            bgmClip.open(audioStream);
            setupLooping();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println(e.getMessage());
        }
    }

    // 设置循环播放逻辑
    private void setupLooping() {
        bgmClip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP && isPlaying) {
                bgmClip.setFramePosition(0);  // 重置播放位置
                bgmClip.start();             // 重新开始播放
            }
        });
    }

    // 开始播放（线程安全）
    public void startPlayback() {
        if (bgmClip != null && !isPlaying) {
            isPlaying = true;
            playbackThread = new Thread(() -> {
                bgmClip.start();
            });
            playbackThread.start();
        }
    }

    // 停止播放并释放资源
    public void stopPlayback() {
        if (bgmClip != null && isPlaying) {
            isPlaying = false;
            bgmClip.stop();
            bgmClip.close();
            playbackThread.interrupt();
        }
    }
}
