package mta.se.chitchat.utils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.InputStream;

import static mta.se.chitchat.utils.Constants.*;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut </p> Software Engineering
 *         Project </p> Audio utilities
 */
public class AudioUtils {
	private static final float[] netFrameSize = { 1, // nothing
			2, // CD
			2, // FM
			1, // Telephone
			33.0f // GSM
	};

	private static final float[] netSampleRate = { 1.0f, // nothing
			44100.0f, // CD
			22050.0f, // FM
			8000.0f, // Telephone
			8000.0f // GSM
	};

	private static final float[] netFrameRate = { 1.0f, // nothing
			44100.0f, // CD
			22050.0f, // FM
			8000.0f, // Telephone
			50.0f // GSM
	};

	public static long bytes2millis(long bytes, AudioFormat format) {
		return (long) (bytes / format.getFrameRate() * 1000 / format
				.getFrameSize());
	}

	public static long millis2bytes(long ms, AudioFormat format) {
		return (long) (ms * format.getFrameRate() / 1000 * format
				.getFrameSize());
	}

	public static AudioFormat getLineAudioFormat(int formatCode) {
		return getLineAudioFormat(netSampleRate[formatCode]);
	}

	public static AudioFormat getLineAudioFormat(float sampleRate) {
		return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, // sampleRate
				16, // sampleSizeInBits
				1, // channels
				2, // frameSize
				sampleRate, // frameRate
				false); // bigEndian
	}

	public static AudioFormat getNetAudioFormat(int formatCode)
			throws UnsupportedAudioFileException {
		if (formatCode == FORMAT_CODE_CD) {
			return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0f, // sampleRate
					16, // sampleSizeInBits
					1, // channels
					2, // frameSize
					44100.0f, // frameRate
					true); // bigEndian
		} else if (formatCode == FORMAT_CODE_FM) {
			return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 22050.0f, // sampleRate
					16, // sampleSizeInBits
					1, // channels
					2, // frameSize
					22050.0f, // frameRate
					true); // bigEndian
		} else if (formatCode == FORMAT_CODE_TELEPHONE) {
			return new AudioFormat(AudioFormat.Encoding.ULAW, 8000.0f, // sampleRate
					8, // sampleSizeInBits
					1, // channels
					1, // frameSize
					8000.0f, // frameRate
					false); // bigEndian
		} else if (formatCode == FORMAT_CODE_GSM) {
			/*
			 * try { Class.forName("org.tritonus.share.sampled.Encodings"); }
			 * catch (ClassNotFoundException cnfe) { throw new
			 * RuntimeException("Tritonus shared classes not properly installed!"
			 * ); } return new AudioFormat(
			 * org.tritonus.share.sampled.Encodings.getEncoding("GSM0610"),
			 * 8000.0F, // sampleRate -1, // sampleSizeInBits 1, // channels 33,
			 * // frameSize 50.0F, // frameRate false); // bigEndian
			 */
			return new AudioFormat(new AudioFormat.Encoding("GSM0610"),
					8000.0F, // sampleRate
					-1, // sampleSizeInBits
					1, // channels
					33, // frameSize
					50.0F, // frameRate
					false); // bigEndian
		}
		throw new RuntimeException("Wrong format code!");
	}

	public static AudioInputStream createNetAudioInputStream(int formatCode,
			InputStream stream) {
		try {
			AudioFormat format = getNetAudioFormat(formatCode);
			return new AudioInputStream(stream, format,
					AudioSystem.NOT_SPECIFIED);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int getFormatCode(AudioFormat format) {
		AudioFormat.Encoding encoding = format.getEncoding();
		// very simple check...
		if (encoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
			if (format.getSampleRate() == 44100.0f) {
				return FORMAT_CODE_CD;
			} else {
				return FORMAT_CODE_FM;
			}
		}
		if (encoding.equals(AudioFormat.Encoding.ULAW)) {
			return FORMAT_CODE_TELEPHONE;
		}
		if (encoding.toString().equals("GSM0610")) {
			return FORMAT_CODE_GSM;
		}
		throw new RuntimeException("Wrong Format");
	}

}
