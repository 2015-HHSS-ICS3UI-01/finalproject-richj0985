


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Matthew
 */
public class SoundEffect {

      Clip clip;

      // Constructor
      public SoundEffect() {

      }

      public SoundEffect( String soundFilename ) {


         try {
             // Open an audio input stream.
             File f = new File( soundFilename );
             AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);

             // Get a sound clip resource.
             clip = AudioSystem.getClip();
             // Open audio clip and load samples from the audio input stream.
             clip.open(audioIn);

          } 

          catch (MalformedURLException e) {
              e.printStackTrace();
              throw new RuntimeException("Sound: Malformed URL: " + e);
          }
          catch (UnsupportedAudioFileException e) {
              e.printStackTrace();
              throw new RuntimeException("Sound: Unsupported Audio File: " + e);
          }
          catch (IOException e) {
              e.printStackTrace();
              throw new RuntimeException("Sound: Input/Output Error: " + e);
          }
          catch (LineUnavailableException e) {
              e.printStackTrace();
              throw new RuntimeException("Sound: Line Unavailable Exception Error: " + e);
          }

      }

     public void Start() {
         clip.setFramePosition(0);
         clip.start();       
      }
     public void Stop() {
         clip.stop();       
      }
}
