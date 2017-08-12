
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

public class SoundClass 
{
	private AudioInputStream sound;
	private Clip clip;

	public SoundClass(String fileName) {
		try {
			sound = AudioSystem.getAudioInputStream(new File(fileName));
			clip = AudioSystem.getClip();
			clip.stop();
			clip.open(sound);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		clip.start();
	}

	public void stop() {
		clip.stop();
	}
}