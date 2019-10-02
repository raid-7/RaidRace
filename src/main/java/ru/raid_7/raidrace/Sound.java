package ru.raid_7.raidrace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
	private boolean released = false;
	private Clip clip = null;
	
	public Sound(File f) {
		try {
			AudioInputStream stream = AudioSystem.getAudioInputStream(f);
			clip = AudioSystem.getClip();
			clip.open(stream);
			released = true;
		} catch(IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
			exc.printStackTrace();
			released = false;
		}
	}

	public boolean isReleased() {
		return released;
	}
	public boolean isPlaying() {
		return clip.isRunning();
	}
	public Clip getClip() {
		return clip;
	}

	public void play(boolean breakOld) {
		if (released) {
			if (breakOld) {
				clip.stop();
				clip.setFramePosition(0);
				clip.start();
			} else if (!isPlaying()) {
				clip.setFramePosition(0);
				clip.start();
			}
		}
	}
	
	public static boolean play(String st) {
		Sound s = new Sound(new File(st));
		if (!s.isReleased()) return false;
		s.getClip().start();
		return true;
	}
	public static SoundSystem loadSystem(File fol) {
		return new SoundSystem(fol.listFiles());
	}
	
	public static class SoundSystem {
		private ArrayList<Sound> sounds = new ArrayList<Sound>();
		private HashMap<String, Sound> map = new HashMap<String, Sound>();
		
		public SoundSystem(File fs[]) {
			for (int i=0; i<fs.length; i++) {
				Sound snd = new Sound(fs[i]);
				if (snd.isReleased()) {
					sounds.add(snd);
					String n = fs[i].getName();
					n = n.substring(0, n.lastIndexOf("."));
					map.put(n, snd);
				}
			}
		}

		public Sound[] getSounds() {
			Sound[] r = new Sound[sounds.size()];
			sounds.toArray(r);
			return r;
		}
		public Sound getSound(int i) {
			return sounds.get(i);
		}
		public Sound getSound(String s) {
			return map.get(s);
		}
		
		public boolean play(String s) {
			if (!map.containsKey(s)) return false;
			Sound c = map.get(s);
			if (!c.isPlaying()) {
				c.play(false);
			}
			return true;
		}
		public void stopAll() {
			for (Sound s : sounds) {
				s.getClip().stop();
			}
		}
	}

}