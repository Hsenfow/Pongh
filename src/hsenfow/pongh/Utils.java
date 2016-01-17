package hsenfow.pongh;

import java.awt.Color;
import java.awt.Dimension;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Stack;

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

	// The player
	public static Player player;
	
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
	 * Writes the given content to the specified file.
	 * @param file The file to write to
	 * @param content The content to write to the file
	 * @return Returns true if the file was successfully written to
	 */
	public static boolean writeFile(String file, String content){
		try{
			// Write to the file
			Files.write(Paths.get(file), content.getBytes());

			// The write was a success
			return true;
		} catch(IOException | SecurityException e){
			Utils.log("Error writing to file: " + file + " - " + e.getMessage());

			// Something went wrong
			return false;
		}
	}

	/**
	 * Reads the content in a file.
	 * @param file The file to read the content from
	 * @return An array of the read lines or null if the file couldn't be read
	 */
	public static String[] readFile(String file){
		try{
			// Get the lines in the file
			List<String> lines = Files.readAllLines(Paths.get(file));

			// Return them as an array
			return (String[])lines.toArray();
		} catch(IOException | SecurityException e){
			Utils.log("Error reading file: " + file + " - " + e.getMessage());
			return null;
		}
	}

	/**
	 * Writes the given object(s) to the specified file.
	 * @param file The file to save the object(s) to
	 * @param objects An array of one or more objects to write to the file
	 * @return Returns true if the file was successfully written to
	 */
	public static boolean writeObjects(String file, Object... objects){
		if(objects == null || objects.length == 0) return false;

		try(
				// Open the file
				ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
				){
			// Write each object to the file
			for(Object object : objects){
				outputStream.writeObject(object);
			}

			// Close the file
			outputStream.close();

			// The file was successfully written to
			return true;
		} catch(IOException ioe){
			Utils.log("Error writing objects to file: " + file + " - " + ioe.getMessage());
			return false;
		}
	}

	/**
	 * Reads all the objects from the specified file.
	 * @param file The file to load the objects from
	 * @return An array of the loaded objects or null if the file couldn't be read
	 */
	public static Object[] readObjects(String file){
		try(
				// Open the file
				ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
				){
			// Create a stack to store the loaded objects
			Stack<Object> loadedObjects = new Stack<>();

			try{
				// Load each object
				Object currentObject;
				while((currentObject = inputStream.readObject()) != null){
					loadedObjects.push(currentObject);
				}
			} catch(EOFException eofe){ /* Nothing */ }

			// Return the loaded objects as an array
			return loadedObjects.toArray();
		} catch(IOException | ClassNotFoundException e){
			Utils.log("Error loading objects from file: " + file + " - " + e.getMessage());
			return null;
		}
	}

	/**
	 * Delets the specified file.
	 * @param file The file to delete
	 * @return Returns true if the file was successfully deleted
	 */
	public static boolean deleteFile(String file){
		try{
			// Delete the file
			return Files.deleteIfExists(Paths.get(file));
		} catch(IOException ioe){
			Utils.log("Unable to delete file: " + file + " - " + ioe.getMessage());
			return false;
		}
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
	 * @param colourString A string version of a colour
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
