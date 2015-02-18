package mta.se.chitchat.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;

import mta.se.chitchat.model.ChatModel;
import mta.se.chitchat.security.Security;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut </p> Software Engineering
 *         Project </p> Class that provides an AudioInputStream that reads its
 *         data from the soundcard input </p>the AudioInputStream is in the
 *         network format
 */
public class AudioCapture extends AudioBase {

	ChatModel m_Model;
	protected AudioInputStream ais;
	protected OutputStream outputStream;
	private CaptureThread thread;
	private Security security;
	
	/**
	 * AudioCapture constructor
	 * @param formatCode
	 * @param mixer
	 * @param bufferSizeMillis
	 */
	public AudioCapture(int formatCode, Mixer mixer, int bufferSizeMillis) {
		super("Microphone", formatCode, mixer, bufferSizeMillis);
		security = new Security();
	}

	public void setChatModel(ChatModel model) {
		m_Model = model;
	}

	protected void createLineImpl() throws Exception {
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, lineFormat);
		if (mixer != null) {
			line = (TargetDataLine) mixer.getLine(info);
		} else {
			line = AudioSystem.getTargetDataLine(lineFormat);
		}
	}

	protected void openLineImpl() throws Exception {
		TargetDataLine tdl = (TargetDataLine) line;
		tdl.open(lineFormat, bufferSize);
		ais = new TargetDataLineMeter(tdl);
		ais = AudioSystem.getAudioInputStream(netFormat, ais);
	}

	public synchronized void start() throws Exception {
		boolean needStartThread = false;
		if (thread != null && (thread.isTerminating() || outputStream == null)) {
			thread.terminate();
			needStartThread = true;
		}
		if ((thread == null || needStartThread) && outputStream != null) {
			// start thread
			thread = new CaptureThread();
			thread.start();
		}
		super.start();
	}

	protected void closeLine(boolean willReopen) {
		CaptureThread oldThread = null;
		synchronized (this) {
			if (!willReopen && thread != null) {
				thread.terminate();
			}
			super.closeLine(willReopen);
			if (!willReopen) {
				if (ais != null) {
					try {
						ais.close();
					} catch (IOException ioe) {
					}
				}
				if (thread != null) {
					if (outputStream != null) {
						try {
							outputStream.close();
						} catch (IOException ioe) {
						}
						outputStream = null;
					}
					oldThread = thread;
				}
			}
		}
		if (oldThread != null) {
			oldThread.waitFor();
		}
	}


	public AudioInputStream getAudioInputStream() {
		return ais;
	}

	/**
	 * Set the output stream to write to. Must be set *before* calling start.
	 * When writing to the Output Stream, the stream returned by
	 * getAudioInputStream must not be read from.
	 */
	public synchronized void setOutputStream(OutputStream stream) {
		this.outputStream = stream;
		if (this.outputStream == null && thread != null) {
			thread.terminate();
			thread = null;
		}
	}

	public synchronized OutputStream getOutputStream() {
		return this.outputStream;
	}

	/**
	 *  Thread used for writing the captured audio data to the output stream
	 */
	class CaptureThread extends Thread {
		private boolean doTerminate = false;
		private boolean terminated = false;

		public void run() {
			byte[] buffer = new byte[getBufferSize()];
			try {
				AudioInputStream localAIS = ais;
				while (!doTerminate) {
					if (localAIS != null) {
						int r = localAIS.read(buffer, 0, buffer.length);
						if (r > 0) {
							synchronized (AudioCapture.this) {
								if (outputStream != null) {
									try {
										// ENCRYPTING
//										byte[] encrypted = security.encrypt(
//												Arrays.copyOf(m_Model.getSharedSecret(), 16),
//												Arrays.copyOfRange(m_Model.getSharedSecret(), 16, 32), buffer);							
//										outputStream.write(encrypted, 0, r);
										outputStream.write(buffer, 0, r);
									} catch (IOException e) {
										JOptionPane.showMessageDialog(null,
												"Call has been terminated...!");
										e.printStackTrace();
										System.exit(1);
									}
								}
							}
							if (outputStream == null) {
								synchronized (this) {
									this.wait(100);
								}
							}
						} else {
							if (r == 0) {
								synchronized (this) {
									this.wait(20);
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
			} catch (InterruptedException e) {
				e.printStackTrace();
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

	/**
	 *  an AudioInputStream that reads from a TargetDataLine, and
	 *  that calculates the current level "on the fly"
	 */
	private class TargetDataLineMeter extends AudioInputStream {
		private TargetDataLine line;

		TargetDataLineMeter(TargetDataLine line) {
			super(new ByteArrayInputStream(new byte[0]), line.getFormat(),
					AudioSystem.NOT_SPECIFIED);
			this.line = line;
		}

		public int available() throws IOException {
			return line.available();
		}

		public int read() throws IOException {
			throw new IOException("illegal call to TargetDataLineMeter.read()!");
		}

		public int read(byte[] b, int off, int len) throws IOException {
			try {
				int ret = line.read(b, off, len);
				if (isMuted()) {
					muteBuffer(b, off, ret);
				}
				if (ret > 0) {
					calcCurrVol(b, off, ret);
				}
				return ret;
			} catch (IllegalArgumentException e) {
				throw new IOException(e.getMessage());
			}
		}

		public void close() throws IOException {
			if (line.isActive()) {
				line.flush();
				line.stop();
			}
			line.close();
		}

		public int read(byte[] b) throws IOException {
			return read(b, 0, b.length);
		}

		public long skip(long n) throws IOException {
			return 0;
		}

		public void mark(int readlimit) {
		}

		public void reset() throws IOException {
		}

		public boolean markSupported() {
			return false;
		}

	} 
}
