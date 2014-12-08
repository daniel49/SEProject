package mta.se.chitchat;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.FloatControl;


public class Voice {
	
	private float sampleRate=16000;
	private int sampleSizeInBits=8;
	int channels = 2;
	boolean signed = true;
	boolean bigEndian = true;
	
	
	
	public void setAudioFormat(float sampleRate,int sampleSizeInBits,int channels, boolean signed, boolean bigEndian) {
		
		this.sampleRate = sampleRate;
		this.sampleSizeInBits = sampleSizeInBits;
		this.channels = channels;
		this.signed = signed;
		this.bigEndian = bigEndian;
	
	}
	
	
	public byte[] recordVoice(ByteArrayOutputStream out) throws Exception {
		
			//final ByteArrayOutputStream out = new ByteArrayOutputStream();	    
		    
		    final AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
		        bigEndian);
		    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		    final TargetDataLine recLine = (TargetDataLine) AudioSystem.getLine(info);
		    recLine.open(format);
		    recLine.start();
		    
		    int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
		    byte buffer[] = new byte[bufferSize];
		    
	        try {     	
			      int seconds = 3;
			      while(seconds>0) {
			          int count = recLine.read(buffer, 0, buffer.length);
			          if (count > 0) {
			            out.write(buffer, 0, count);
			          }
			          seconds--;
			      }  
		          out.close();
	        }
	        catch (IOException e) {
		          System.err.println("I/O problems: " + e);
		          System.exit(-1);
	        } 		    
		    
	        return out.toByteArray();
	}
	
	
	
	public void playVoice(byte[] audio) throws Exception {
		
		InputStream input = new ByteArrayInputStream(audio);	    
		    
		final AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,bigEndian);
	    
	    DataLine.Info playInfo = new DataLine.Info(SourceDataLine.class, format);
	    final SourceDataLine playLine = (SourceDataLine) AudioSystem.getLine(playInfo);  
	    final AudioInputStream ais = new AudioInputStream(input, format, audio.length
	        / format.getFrameSize());
	    playLine.open(format);
	    
	    // Increase volume
	    FloatControl gainControl = (FloatControl) playLine.getControl(FloatControl.Type.MASTER_GAIN);
	    gainControl.setValue(6.00f);
	    
	    playLine.start();
	    
	    int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
	    byte buffer[] = new byte[bufferSize];
		
	    try {
	          int count;
	          while ((count = ais.read(buffer, 0, buffer.length)) != -1) {
	            if (count > 0) {
	            	playLine.write(buffer, 0, count);
	            }
	          }
	          playLine.drain();
	          playLine.close();
	        }
	    catch (IOException e) {
	          System.err.println("I/O problems: " + e);
	          System.exit(-3);
	    }
	}
}
