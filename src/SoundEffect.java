


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/*
 * SoccerEffect - used to load and play .WAV files
 * 
 * @author richj0985
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
              throw new RuntimeException("SoundEffect: Malformed URL: " + e);
          }
          catch (UnsupportedAudioFileException e) {
              e.printStackTrace();
              throw new RuntimeException("SoundEffect: Unsupported Audio File: " + e);
          }
          catch (IOException e) {
              e.printStackTrace();
              throw new RuntimeException("SoundEffect: Input/Output Error: " + e);
          }
          catch (LineUnavailableException e) {
              e.printStackTrace();
              throw new RuntimeException("SoundEffect: Line Unavailable Exception Error: " + e);
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
