package hsenfow.pongh.Input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import javax.swing.JOptionPane;

import hsenfow.pongh.Utils;
import hsenfow.pongh.Network.NetworkCommunications;
import hsenfow.pongh.Network.NetworkUtils;

/**
 * Handles all the key presses while the game panel is in focus.
 */
public class GameKeyListener implements KeyListener{
	
	// The current key states
	private HashMap<Integer, Boolean> keyStates;
	
	/**
	 * Initialises all things required by the game key listener.
	 */
	public GameKeyListener(){
		// Create the hash map used to store the key states
		keyStates = new HashMap<>();
	}
	
	/**
	 * Used to check whether or not a key is currently pressed.
	 * @param keyCode The key code for the key
	 * @return True or false depending on whether the key is pressed
	 */
	public boolean isKeyPressed(int keyCode){
		Boolean keyState = keyStates.get(keyCode);
		if(keyState != null) return keyState;
		else return false;
	}
	
	/**
	 * Handles all key presses.
	 */
	@Override
	public void keyPressed(KeyEvent event){
		int keyCode = event.getKeyCode();
		
		// Handle the key press
		if(keyCode == KeyEvent.VK_ESCAPE){
			// Pause the game if we're not on multiplayer
			if(!NetworkUtils.connected) Utils.gamePaused = true;
			
			// Get the user to confirm that they want to end the game
			int confirm = JOptionPane.showConfirmDialog(Utils.mainFrame, "Exit game?", "Exit", JOptionPane.YES_NO_OPTION);
			if(confirm == JOptionPane.YES_OPTION){
				// Destroy the game panel
				Utils.mainFrame.destroyGamePanel();
				
				// Disconnect from multiplayer
				if(NetworkUtils.connected){
					NetworkCommunications.sendMessage(NetworkCommunications.MESSAGE_DC);
					NetworkUtils.closeAllConnections();
				}
			}
			
			// Un-pause the game
			Utils.gamePaused = false;
		}
		
		// Set the key as pressed
		keyStates.put(keyCode, true);
	}

	/**
	 * Handles all key releases.
	 */
	@Override
	public void keyReleased(KeyEvent event){
		int keyCode = event.getKeyCode();
		
		// Set the key as not pressed
		keyStates.put(keyCode, false);
	}

	/**
	 * Handles all keys typed.
	 */
	@Override
	public void keyTyped(KeyEvent event){
		// Not needed at this time
	}
}
