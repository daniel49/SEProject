package mta.se.chitchat.utils;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JOptionPane;

import mta.se.chitchat.model.ChatModel;
import mta.se.chitchat.security.Security;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut </p> Software Engineering
 *         Project </p> Class that reads its audio from an AudioInputStream
 */
public class AudioPlayback extends AudioBase {

	ChatModel m_Model;
	protected AudioInputStream ais;
	private PlayThread thread;
	private Security security;
	
	public AudioPlayback(int formatCode, Mixer mixer, int bufferSizeMillis) {
		super("Speaker", formatCode, mixer, bufferSizeMillis);
		security = new Security();
	}

	public void setChatModel(ChatModel model) {
		m_Model = model;
	}

	protected void createLineImpl() throws Exception {
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, lineFormat);
		if (mixer != null) {
			line = (SourceDataLine) mixer.getLine(info);
		} else {
			line = AudioSystem.getSourceDataLine(lineFormat);
		}
	}

	protected void openLineImpl() throws Exception {
		SourceDataLine sdl = (SourceDataLine) line;
		sdl.open(lineFormat, bufferSize);
	}

	public synchronized void start() throws Exception {
		boolean needStartThread = false;
		if (thread != null && thread.isTerminating()) {
			thread.terminate();
			needStartThread = true;
		}
		if (thread == null || needStartThread) {
			thread = new PlayThread();
			thread.start();
		}
		super.start();
	}

	protected void closeLine(boolean willReopen) {
		PlayThread oldThread = null;
		synchronized (this) {
			if (!willReopen && thread != null) {
				thread.terminate();
			}
			super.closeLine(willReopen);
			if (!willReopen && thread != null) {
				oldThread = thread;
				thread = null;
			}
		}
		if (oldThread != null) {
			oldThread.waitFor();
		}
	}


	public void setAudioInputStream(AudioInputStream ais) {
		this.ais = AudioSystem.getAudioInputStream(lineFormat, ais);
	}
	
	
	/**
	 * 
	 * 
	 *  Thread used for the playback feature
	 *
	 */
	class PlayThread extends Thread {
		private boolean doTerminate = false;
		private boolean terminated = false;

		public void run() {
			byte[] buffer = new byte[getBufferSize()];
			try {
				while (!doTerminate) {
					SourceDataLine sdl = (SourceDataLine) line;
					if (ais != null) {
						int r = ais.read(buffer, 0, buffer.length);
						if (r > 0) {
							if (isMuted()) {
								muteBuffer(buffer, 0, r);
							}
							// run some simple analysis
							calcCurrVol(buffer, 0, r);
							if (sdl != null) {
								//DECRYPTING
//								byte[] decrypted = security.decrypt(Arrays.copyOf(m_Model.getSharedSecret(), 16),
//										Arrays.copyOfRange(m_Model.getSharedSecret(), 16, 32),
//										buffer);
//								sdl.write(decrypted, 0, r);
								
								sdl.write(buffer, 0, r);
							}
						} else {
							if (r == 0) {
								synchronized (this) {
									this.wait(40);
								}
							}
						}
					} else {
						synchronized (this) {
							this.wait(50);
						}
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Call has been terminated...!");
				System.exit(1);
			} catch (InterruptedException ie) {
					ie.printStackTrace();
			}
			terminated = true;
		}

		public synchronized void terminate() {
			doTerminate = true;
			this.notifyAll();
		}

		public synchronized boolean isTerminating() {
			return doTerminate || terminated;
		}

		public synchronized void waitFor() {
			if (!terminated) {
				try {
					this.join();
				} catch (InterruptedException ie) {
						ie.printStackTrace();
				}
			}
		}
	}

}
