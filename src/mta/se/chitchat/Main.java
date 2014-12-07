package mta.se.chitchat;

import java.io.ByteArrayOutputStream;

import mta.se.chitchat.Voice;

public class Main {

    public static void main(String[] args) throws Exception {
	    
    	
    	final Voice vc = new Voice();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();	

        Runnable recordRunner = new Runnable() {
        	 
    	      public void run() {
    	    	  try {
    	    		  vc.recordVoice(out);
    	    		  System.out.print(out.toByteArray());
    	    	  }
    	    	  catch(Exception e) {
    	    		  System.exit(-1);
    	    	  }
    	      }
          
          
        };
        
        
        
        
        Thread captureThread = new Thread(recordRunner);
        captureThread.start();  
        
        Thread.sleep(5000);
        

        final byte[] data = out.toByteArray();
        
        Runnable playRunner = new Runnable() {
        	Voice vc = new Voice();
    	      public void run() {
    	    	  try {
    	    		  vc.playVoice(data);
    	    	  }
    	    	  catch(Exception e) {
    	    		  System.exit(-1);
    	    	  }
    		 }
      
        };
        
        
        
        Thread playThread = new Thread(playRunner);
        playThread.start();

      }
}
