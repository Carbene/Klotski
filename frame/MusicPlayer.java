package frame;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * 这是一个音乐播放器类，实现游戏的背景音乐
 */
public class MusicPlayer {
    private Clip bgmClip;
    private Clip soundEffectPressingButtonClip;
    private Clip soundEffectMovingBlockClip;
    private static final String BGMPATH = "/bgm.wav";
    private static final String SOUNDEFFECTPRESSINGBUTTONPATH = "/pressingButton.wav";
    private static final String SOUNDEFFECTMOVINGBLOCKPATH = "/movingBlock.wav";
    private boolean isBGMEnabled = true;
    private boolean isSoundEffectEnabled = true;

    /**
     * 无参构造器，创建音乐播放器对象
     */
    public MusicPlayer() {
        try {
            setUpBGM();
            setUpSoundEffectPressingButton();
            setUpSoundEffectMovingBlock();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 这是一个设置背景音乐的方法
     * @throws IOException 文件不存在等错误
     */
    private void setUpBGM() throws IOException {
        try{
            URL bgmURL = getClass().getResource(BGMPATH);
            if (bgmURL == null) {
                throw new IOException("BGM file not found");
            }
            AudioInputStream bgmInputStream = AudioSystem.getAudioInputStream(bgmURL);
            AudioFormat bgmFormat = bgmInputStream.getFormat();
            DataLine.Info bgmInfo = new DataLine.Info(Clip.class, bgmFormat);
            bgmClip = (Clip) AudioSystem.getLine(bgmInfo);
            bgmClip.open(bgmInputStream);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * 这是一个设置按键音效的方法
     * @throws IOException 文件不存在等错误
     */
    private void setUpSoundEffectPressingButton() throws IOException {
        try {
            URL pressButtonURL = getClass().getResource(SOUNDEFFECTPRESSINGBUTTONPATH);
            if (pressButtonURL == null) {
                throw new IOException("Sound effect file not found");
            }
            AudioInputStream pressInputStream = AudioSystem.getAudioInputStream(pressButtonURL);
            AudioFormat pressFormat = pressInputStream.getFormat();
            DataLine.Info pressInfo = new DataLine.Info(Clip.class, pressFormat);
            soundEffectPressingButtonClip = (Clip) AudioSystem.getLine(pressInfo);
            soundEffectPressingButtonClip.open(pressInputStream);
        }catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * 这是一个设置移动音效的方法
     * @throws IOException 文件不存在等错误
     */
    private void setUpSoundEffectMovingBlock() throws IOException {
        try{
            URL moveBlockURL = getClass().getResource(SOUNDEFFECTMOVINGBLOCKPATH);
            if (moveBlockURL == null) {
                throw new IOException("Sound effect file not found");
            }
            AudioInputStream moveInputStream = AudioSystem.getAudioInputStream(moveBlockURL);
            AudioFormat moveFormat = moveInputStream.getFormat();
            DataLine.Info moveInfo = new DataLine.Info(Clip.class, moveFormat);
            soundEffectMovingBlockClip = (Clip) AudioSystem.getLine(moveInfo);
            soundEffectMovingBlockClip.open(moveInputStream);
        }catch (Exception e){
            throw new IOException(e);
        }
    }

    /**
     * 循环播放背景音乐，并保证总是从0开始
     */
    public void playBGM() {
       bgmClip.setFramePosition(0);
       bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
       this.isBGMEnabled = true;
    }

    /**
     * 停止播放背景音乐
     */
    public void stopBGM() {
        bgmClip.stop();
        bgmClip.setFramePosition(0);
        this.isBGMEnabled = false;
    }

    /**
     * 播放按键音效
     */
    public void playSoundEffectPressingButton() {
        if(isBGMEnabled) {
            soundEffectPressingButtonClip.setFramePosition(0);
            soundEffectPressingButtonClip.start();
        }
    }

    /**
     * 播放方块移动音效
     */
    public void playSoundEffectMovingBlock() {
        if(isBGMEnabled) {
            soundEffectMovingBlockClip.setFramePosition(0);
            soundEffectMovingBlockClip.start();
        }
    }

}