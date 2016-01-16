package hsenfow.pongh;

import java.awt.Color;
import java.awt.Dimension;
import java.lang.reflect.Field;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public final class Utils {
	
	// The resources folder location
	public static final String RESOURCES_FOLDER = "/resources/";
	// The audio folder location
	public static final String AUDIO_FOLDER = RESOURCES_FOLDER + "Audio/";
	
	// The screen size
	public static Dimension screenSize;
	
	// The main frame
	public static MainFrame mainFrame;
	
	// Whether the game is paused
	public static boolean gamePaused = false;
	
	/**
	 * Logs the given message.
	 * @param message The message to log
	 */
	public static void log(String message){
		System.out.println(message);
	}
	
	/**
	 * Sets the look and feel to the requested one if possible.
	 * @param lookAndFeel The name of the look and feel to use
	 * @return Returns whether or not the look and feel was enabled
	 */
	public static boolean setLookAndFeel(String lookAndFeel){
		try{
			// Go through the look and feels, looking for the one requested
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
				if(info.getName().equals(lookAndFeel)){
					UIManager.setLookAndFeel(info.getClassName());
					return true;
				}
			}
		} catch(Exception e){
			Utils.log("Error enabling look and feel: " + e.getMessage());
		}
		Utils.log(lookAndFeel + " look and feel not found");
		return false;
	}
	
	/**
	 * Plays some audio.
	 * @param filePath The file path of the audio to play
	 */
	public static void playAudio(String filePath){
		try{
			// Open an input stream for the audio file
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(Utils.class.getResourceAsStream(filePath));
			
			// Get a clip
			Clip clip = AudioSystem.getClip();
			
			// Open the audio and play it
			clip.open(inputStream);
			clip.start();
		} catch(Exception e){
			Utils.log("Error playing audio: " + filePath + " - " + e.toString());
		}
	}
	
	/**
	 * Converts the given string into its matching colour. E.g, 'Yellow' -> Color.YELLOW
	 * @param colour A string version of a colour
	 * @return The colour object representing the given string or null if one wasn't found
	 */
	public static Color getColourFromString(String colourString){
		// Make sure a valid string was given
		if(colourString == null) return null;
		
		// Convert the string to uppercase so it matches the names of the colours
		colourString = colourString.toUpperCase();
		try{
			// Try to find a matching colour
			Field field = Class.forName("java.awt.Color").getField(colourString);
			return (Color)field.get(null);
		} catch(Exception e){
			Utils.log("Invalid colour: " + colourString + " - " + e.getMessage());
			return null;
		}
	}
	
}
