package mta.se.chitchat.utils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.InputStream;

import static mta.se.chitchat.utils.Constants.FORMAT_CODE_TELEPHONE;

/**
 * 
 * @author Ilie Daniel, Cosovanu Vasile and Radu Ionut </p> Software Engineering
 *         Project </p> Audio utilities
 */
public class AudioUtils {

	private static final float netSampleRate = 8000.0f;

	public static long bytes2millis(long bytes, AudioFormat format) {
		return (long) (bytes / format.getFrameRate() * 1000 / format
				.getFrameSize());
	}

	public static long millis2bytes(long ms, AudioFormat format) {
		return (long) (ms * format.getFrameRate() / 1000 * format
				.getFrameSize());
	}

	public static AudioFormat getLineAudioFormat(int formatCode) {
		return getLineAudioFormat(netSampleRate);
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
		if (formatCode == FORMAT_CODE_TELEPHONE) {
			return new AudioFormat(AudioFormat.Encoding.ULAW, 8000.0f, // sampleRate
					8, // sampleSizeInBits
					1, // channels
					1, // frameSize
					8000.0f, // frameRate
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
		if (encoding.equals(AudioFormat.Encoding.ULAW)) {
			return FORMAT_CODE_TELEPHONE;
		}
		throw new RuntimeException("Wrong Format");
	}

}
