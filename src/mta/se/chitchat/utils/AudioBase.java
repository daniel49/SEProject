package mta.se.chitchat.utils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut </p> Software Engineering
 *         Project </p> The base audio functionalities
 */
public abstract class AudioBase implements LineListener {

	protected AudioFormat lineFormat;
	protected AudioFormat netFormat;
	protected int formatCode = -1;
	protected int bufferSizeMillis;
	protected int bufferSize;
	protected Mixer mixer;
	protected String title;
	protected DataLine line;
	protected int lastLevel = -1;
	protected boolean muted = false;

	protected AudioBase(String title, int formatCode, Mixer mixer,
			int bufferSizeMillis) {
		this.title = title;
		this.bufferSizeMillis = bufferSizeMillis;
		this.mixer = mixer;
		try {
			setFormatCode(formatCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(LineEvent event) {
	}

	public void open() throws Exception {
		closeLine(false);
		destroyLine();
		createLine();
		openLine();
	}

	protected abstract void createLineImpl() throws Exception;

	private void createLine() throws Exception {
		try {
			line = null;
			createLineImpl();
			line.addLineListener(this);
		} catch (LineUnavailableException ex) {
			throw new Exception("Unable to open " + title + ": "
					+ ex.getMessage());
		}
	}

	protected abstract void openLineImpl() throws Exception;

	private void openLine() throws Exception {
		try {
			bufferSize = (int) AudioUtils.millis2bytes(bufferSizeMillis,
					lineFormat);
			bufferSize -= bufferSize % lineFormat.getFrameSize();
			openLineImpl();
			bufferSize = line.getBufferSize();
		} catch (LineUnavailableException ex) {
			throw new Exception("Unable to open " + title + ": "
					+ ex.getMessage());
		}
	}

	public void start() throws Exception {
		if (line == null) {
			throw new Exception(title + ": cannot start");
		}
		line.flush();
		line.start();
	}

	public void close() {
		close(false);
	}

	public void close(boolean willReopen) {
		closeLine(willReopen);
		destroyLine();
	}

	protected void closeLine(boolean willReopen) {
		if (!willReopen)
			lastLevel = -1;
		if (line != null) {
			line.flush();
			line.stop();
			line.close();
		}
	}

	private void destroyLine() {
		if (line != null) {
			line.removeLineListener(this);
		}
		line = null;
	}

	public boolean isStarted() {
		return (line != null) && (line.isActive());
	}

	public boolean isOpen() {
		return (line != null) && (line.isOpen());
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public int getBufferSizeMillis() {
		return bufferSizeMillis;
	}

	public void setBufferSizeMillis(int bufferSizeMillis) throws Exception {
		if (this.bufferSizeMillis == bufferSizeMillis)
			return;
		boolean wasOpen = isOpen();
		boolean wasStarted = isStarted();
		closeLine(true);

		this.bufferSizeMillis = bufferSizeMillis;

		if (wasOpen) {
			openLine();
			if (wasStarted) {
				start();
			}
		}
	}

	public int getFormatCode() {
		return formatCode;
	}

	public void setFormatCode(int formatCode) throws Exception {
		if (this.formatCode == formatCode)
			return;
		boolean wasOpen = isOpen();
		if (wasOpen) {
			throw new Exception("cannot change format while open");
		}
		this.lineFormat = AudioUtils.getLineAudioFormat(formatCode);
		this.netFormat = AudioUtils.getNetAudioFormat(formatCode);
	}

	public void setMixer(Mixer mixer) throws Exception {
		if (this.mixer == mixer)
			return;
		boolean wasOpen = isOpen();
		boolean wasStarted = isStarted();
		close(true);

		this.mixer = mixer;

		if (wasOpen) {
			createLine();
			openLine();
			if (wasStarted) {
				start();
			}
		}
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	public boolean isMuted() {
		return this.muted;
	}

	public int getLevel() {
		return lastLevel;
	}

	protected void calcCurrVol(byte[] b, int off, int len) {
		int end = off + len;
		int sampleSize = (lineFormat.getSampleSizeInBits() + 7) / 8;
		int max = 0;
		if (sampleSize == 1) {
			for (; off < end; off++) {
				int sample = (byte) (b[off] + 128);
				if (sample < 0)
					sample = -sample;
				if (sample > max)
					max = sample;
			}
			lastLevel = max;
		} else if (sampleSize == 2) {
			if (lineFormat.isBigEndian()) {
				for (; off < end; off += 2) {
					int sample = (short) ((b[off] << 8) | (b[off + 1] & 0xFF));
					if (sample < 0)
						sample = -sample;
					if (sample > max)
						max = sample;
				}
			} else {
				for (; off < end; off += 2) {
					int sample = (short) ((b[off + 1] << 8) | (b[off] & 0xFF));
					if (sample < 0)
						sample = -sample;
					if (sample > max)
						max = sample;
				}
			}
			lastLevel = max >> 8;
		} else {
			lastLevel = -1;
		}
	}

	protected void muteBuffer(byte[] b, int off, int len) {
		int end = off + len;
		int sampleSize = (lineFormat.getSampleSizeInBits() + 7) / 8;
		byte filler = 0;
		if (sampleSize == 1) {
			filler = -128;
		}
		for (; off < end; off++) {
			b[off] = filler;
		}
	}

}
