package hsenfow.pongh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;

public class Settings {
	
	// The settings save file
	private static final String SETTINGS_FILE = "settings.txt";
	
	// The different types of settings
	public enum SettingType{
		TEXT,
		CHECKBOX,
		SELECT_BOX
	};
	
	// The available settings
	public enum Setting{
		PADDLE_1_COLOUR,
		BALL_COLOUR
	};
	
	// The possible colours of things
	private static final String[] COLOURS = {"White", "Red", "Green", "Blue", "Cyan", "Magenta", "Orange", "Pink", "Yellow"};
	
	// The information about each setting
	public static final HashMap<Setting, SettingInfo> SETTINGS_INFO = new HashMap<>();
	static{
		SETTINGS_INFO.put(Setting.PADDLE_1_COLOUR, new SettingInfo("Paddle One Colour", SettingType.SELECT_BOX, COLOURS));
		SETTINGS_INFO.put(Setting.BALL_COLOUR, new SettingInfo("Ball Colour", SettingType.SELECT_BOX, COLOURS));
	}
	
	/**
	 * Saves the current settings to the settings file.
	 */
	public static void saveSettings(){
		// Create the save file content
		String fileContent = "";
		for(Entry<Setting, SettingInfo> entry : SETTINGS_INFO.entrySet()){
			// Each line is in the format: key=value
			fileContent += entry.getKey() + "=" + entry.getValue().value + "\n";
		}

		// Write the content to the file
		Utils.writeFile(SETTINGS_FILE, fileContent);
	}
	
	/**
	 * Loads the current settings from the settings file.
	 */
	public static void loadSettings(){
		// Get the settings file
		File settingsFile = new File(SETTINGS_FILE);
		
		// If the settings file doesn't yet exist, then just return here, because there are no
		// settings to load
		if(!settingsFile.exists()) return;
		
		try(
			// Open the settings file
			BufferedReader reader = new BufferedReader(new FileReader(settingsFile));
		){
			String currentLine;
			String lineKey, lineValue;
			int equalsIndex;
			SettingInfo settingInfo;
			while((currentLine = reader.readLine()) != null){
				// Get the index of the = character, which separates the key and the value
				equalsIndex = currentLine.indexOf("=");
				
				// Get the key and value
				lineKey = currentLine.substring(0, equalsIndex);
				lineValue = currentLine.substring(equalsIndex + 1);
				
				// Make sure the key and value are valid
				if(lineKey.length() == 0 || lineValue.length() == 0){
					Utils.log("Invalid settings line: " + currentLine);
					continue;
				}
				
				// Get the 'setting info' for this setting
				settingInfo = SETTINGS_INFO.get(Setting.valueOf(lineKey));
				
				// Make sure we found one
				if(settingInfo == null){
					Utils.log("Unknown setting: " + currentLine);
					continue;
				}
				
				// Update the value of the setting
				settingInfo.value = lineValue;
			}
		} catch(IllegalArgumentException iae){
			Utils.log("Invalid setting: " + iae.getMessage());
		} catch(IOException ioe){
			Utils.log("Error loading settings: " + ioe.getMessage());
		}
	}
	
	/**
	 * Gets and returns the value for the requested setting.
	 * @param setting The setting to get the value for
	 * @return The requested setting's value
	 */
	public static String getSettingValue(Setting setting){
		return SETTINGS_INFO.get(setting).value;
	}
	
	/**
	 * An object used to group all the information about a setting into one place.
	 */
	public static class SettingInfo{
		// The setting's name, displayed in the 'Settings' menu
		public final String fieldName;
		// The setting's type
		public final SettingType type;
		// The select values that are available (when the type is set to a select box
		public String[] selectValues = null;
		
		// The setting's current value
		public String value = null;
		
		/**
		 * Creates a setting with the given values.
		 * @param fieldName The setting's (user-facing) name
		 * @param type The type of the setting
		 */
		public SettingInfo(String fieldName, SettingType type){
			this.fieldName = fieldName;
			this.type = type;
		}
		
		/**
		 * Creates a setting with the given values and select values.
		 * @param selectValues The list of select values for this setting
		 * @see #Settings(String, String, int)
		 */
		public SettingInfo(String fieldName, SettingType type, String[] selectValues){
			this(fieldName, type);
			this.selectValues = selectValues;
		}
	}
}
