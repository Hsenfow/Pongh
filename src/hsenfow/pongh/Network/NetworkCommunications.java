package hsenfow.pongh.Network;

import hsenfow.pongh.Utils;
import hsenfow.pongh.Entities.Paddle;

public class NetworkCommunications {
	
	// The different message results.
	public enum MessageResult{
		// Just continue as we are
		CONTINUE,
		// No data was received
		NO_DATA,
		// The client/server has requested a disconnect
		REQUESTED_DC,
		// The message received is missing the client-server key
		NO_KEY,
		// The message contains unknown content
		UNKNOWN_MESSAGE,
		// The message was invalid in one way or another
		INVALID_MESSAGE
	};
	
	// The different types of message and their formats.
	// The player has moved their paddle
	// Format: {MOVE}PADDLE_DIRECTION_ENUM
	public static final String MESSAGE_MOVE = "{MOVE}";
	// The ball's position has changed (server-side only)
	// Format: {BALLPOS}posX;posY
	public static final String MESSAGE_BALL_POS = "{BALLPOS}";
	// The player has quit the game (via the menu)
	// Format: {DISCONNECT}
	public static final String MESSAGE_DC = "{DISCONNECT}";
	// A player message has been sent
	// Format: {PLAYERMSG}The message
	public static final String MESSAGE_PLAYER_MESSAGE = "{PLAYERMSG}";
	
	/**
	 * Processes the given message received from a client or server and either performs an action
	 * or returns a 'message result'.
	 * @return A message result representing the given message
	 */
	public static MessageResult processMessage(String message){
		// If no message was given then return the 'no data' result
		if(message == null || message.length() == 0){
			Utils.log("No data received");
			return MessageResult.NO_DATA;
		}
		
		//Utils.log("Processing message: " + message);
		
		// Ensure the message begins with the client-server key
		if(message.indexOf(NetworkUtils.clientServerKey) != 0){
			Utils.log("Missing key: " + message);
			return MessageResult.NO_KEY;
		}
		
		// Remove the key from the message
		message = message.replace(NetworkUtils.clientServerKey, "");
		
		// Process the message
		if(message.indexOf(MESSAGE_MOVE) == 0){
			message = message.replace(MESSAGE_MOVE, "");
			
			// Move the other player's paddle in the specified direction
			Utils.mainFrame.gamePanel.movePaddleTwo(Paddle.Direction.valueOf(message));
		}
		else if(message.indexOf(MESSAGE_DC) == 0){
			// A disconnect has been requested
			return MessageResult.REQUESTED_DC;
		}
		else if(message.indexOf(MESSAGE_BALL_POS) == 0){
			message = message.replace(MESSAGE_BALL_POS, "");
			
			// Split what remains of the message, which will give us the ball's new position
			String[] ballPosition = message.split(";");
			
			// Ensure the ball position we've got is valid
			if(ballPosition == null || ballPosition.length != 2){
				Utils.log("Invalid ball position: " + message);
				return MessageResult.INVALID_MESSAGE;
			}
			
			// Update the position of the ball
			Utils.mainFrame.gamePanel.setBallPosition(Integer.parseInt(ballPosition[0]), Integer.parseInt(ballPosition[1]));
		}
		else if(message.indexOf(MESSAGE_PLAYER_MESSAGE) == 0){
			message = message.replace(MESSAGE_PLAYER_MESSAGE, "");
			
			// Set paddle two's latest message to the one received
			Utils.mainFrame.gamePanel.paddleTwo.setLatestMessage(message);
		}
		else{
			Utils.log("Unknown message: " + message);
			return MessageResult.UNKNOWN_MESSAGE;
		}
		
		return MessageResult.CONTINUE;
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
	
}
