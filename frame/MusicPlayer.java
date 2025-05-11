package frame;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MusicPlayer {
    private Clip bgmClip;
    private Clip soundEffectPressingButtonClip;
    private Clip soundEffectMovingBlockClip;
    private static final String BGMPATH = "src/frame/theme/bgm.wav";
    private static final String SOUNDEFFECTPRESSINGBUTTONPATH = "src/frame/theme/pressingButton.wav";
    private static final String SOUNDEFFECTMOVINGBLOCKPATH = "src/frame/theme/movingBlock.wav";
    private boolean isBGMEnabled = true;

    public MusicPlayer() {
        try {
            setUpBGM();
            setUpSoundEffectPressingButton();
            setUpSoundEffectMovingBlock();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setUpBGM() throws IOException {
        try{
            AudioInputStream bgmInputStream = AudioSystem.getAudioInputStream(new File(BGMPATH));
            AudioFormat bgmFormat = bgmInputStream.getFormat();
            DataLine.Info bgmInfo = new DataLine.Info(Clip.class, bgmFormat);
            bgmClip = (Clip) AudioSystem.getLine(bgmInfo);
            bgmClip.open(bgmInputStream);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private void setUpSoundEffectPressingButton() throws IOException {
        try {
            AudioInputStream pressInputStream = AudioSystem.getAudioInputStream(new File(SOUNDEFFECTPRESSINGBUTTONPATH));
            AudioFormat pressFormat = pressInputStream.getFormat();
            DataLine.Info pressInfo = new DataLine.Info(Clip.class, pressFormat);
            soundEffectPressingButtonClip = (Clip) AudioSystem.getLine(pressInfo);
            soundEffectPressingButtonClip.open(pressInputStream);
        }catch (Exception e) {
            throw new IOException(e);
        }
    }

    private void setUpSoundEffectMovingBlock() throws IOException {
        try{
            AudioInputStream moveInputStream = AudioSystem.getAudioInputStream(new File(SOUNDEFFECTMOVINGBLOCKPATH));
            AudioFormat moveFormat = moveInputStream.getFormat();
            DataLine.Info moveInfo = new DataLine.Info(Clip.class, moveFormat);
            soundEffectMovingBlockClip = (Clip) AudioSystem.getLine(moveInfo);
            soundEffectMovingBlockClip.open(moveInputStream);
        }catch (Exception e){
            throw new IOException(e);
        }
    }

    public void playBGM() {
       bgmClip.setFramePosition(0);
       bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
       this.isBGMEnabled = true;
    }

    public void stopBGM() {
        bgmClip.stop();
        bgmClip.setFramePosition(0);
        this.isBGMEnabled = false;
    }

    public void playSoundEffectPressingButton() {
        if(isBGMEnabled) {
            soundEffectPressingButtonClip.setFramePosition(0);
            soundEffectPressingButtonClip.start();
        }
    }

    public void playSoundEffectMovingBlock() {
        if(isBGMEnabled) {
            soundEffectMovingBlockClip.setFramePosition(0);
            soundEffectMovingBlockClip.start();
        }
    }

}