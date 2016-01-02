package hsenfow.pongh;

import java.awt.Dimension;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public final class Utils {
	
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
	
}
