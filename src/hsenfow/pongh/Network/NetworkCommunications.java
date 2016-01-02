package hsenfow.pongh.Network;

import hsenfow.pongh.Utils;
import hsenfow.pongh.Entities.Paddle;

public class NetworkCommunications {
	
	// The different message results.
	// No data was received
	public static final int MESSAGE_RESULT_NO_DATA = 0;
	// Just continue as we are
	public static final int MESSAGE_RESULT_CONTINUE = 1;
	// The client/server has requested a disconnect
	public static final int MESSAGE_RESULT_DISCONNECT = 2;
	// The message received is missing the client-server key
	public static final int MESSAGE_RESULT_NO_KEY = 3;
	
	// The different types of message and their formats.
	// The player has moved their paddle
	// Format: {MOVE}PADDLE_DIRECTION_ENUM
	public static final String MESSAGE_MOVE = "{MOVE}";
	// The player has quit the game (via the menu)
	// Format: {DISCONNECT}
	public static final String MESSAGE_DC = "{DISCONNECT}";
	
	/**
	 * Processes the given message received from a client or server and either performs an action
	 * or returns a 'message result'.
	 * @return The result of the message (see 'MESSAGE_RESULT' declarations)
	 */
	public static int processMessage(String message){
		// If no message was given then return the 'no data' result
		if(message == null || message.length() == 0){
			Utils.log("No data received");
			return MESSAGE_RESULT_NO_DATA;
		}
		
		Utils.log("Processing message: " + message);
		
		// Ensure the message begins with the client-server key
		if(message.indexOf(NetworkUtils.clientServerKey) != 0){
			Utils.log("Missing key: " + message);
			return MESSAGE_RESULT_NO_KEY;
		}
		
		// Remove the key from the message
		message = message.replace(NetworkUtils.clientServerKey, "");
		
		// Process the message
		if(message.indexOf(MESSAGE_MOVE) == 0){
			// Remove the message type
			message = message.replace(MESSAGE_MOVE, "");
			
			// Move the other player's paddle in the specified direction
			Utils.mainFrame.gamePanel.movePaddleTwo(Paddle.Direction.valueOf(message));
		}
		else if(message.indexOf(MESSAGE_DC) == 0){
			// A disconnect has been requested
			return MESSAGE_RESULT_DISCONNECT;
		}
		
		return MESSAGE_RESULT_CONTINUE;
	}
	
	/**
	 * Sends the given message to the client/server.
	 * @param message The message to send (should not include the client-server key)
	 */
	public static void sendMessage(String message){
		// Ensure we're actually connected to a server/client
		if(!NetworkUtils.connected){
			Utils.log("Unable to send message - Not connected");
			return;
		}
		
		// Put the client-server key in front of the message
		message = NetworkUtils.clientServerKey + message;
		
		// Send the message
		NetworkUtils.socketWriter.println(message);
	}
	
	/**
	 * Gets the textual version of the given message result.
	 * @param result A message result
	 * @return The string representing the given message result
	 */
	public static String getMessageResultString(int result){
		switch(result){
		case NetworkCommunications.MESSAGE_RESULT_NO_DATA:
			return "No response received";
		case NetworkCommunications.MESSAGE_RESULT_CONTINUE:
			return "Normal response - Continuing";
		case NetworkCommunications.MESSAGE_RESULT_DISCONNECT:
			return "Connection closed";
		case NetworkCommunications.MESSAGE_RESULT_NO_KEY:
			return "Message missing key";
		default:
			return "Unknown [" + result + "]";
		}
	}
	
}
