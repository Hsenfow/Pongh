package hsenfow.pongh.Entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import hsenfow.pongh.Utils;

public class Paddle extends Entity{
	
	// The default width and height of the paddle
	public static final int DEFAULT_WIDTH = 18;
	public static final int DEFAULT_HEIGHT = 80;
	
	// The speed at which the paddle moves
	private static final int MOVE_SPEED = 7;
	
	// The possible directions in which the paddle can move
	public enum Direction{
		NONE, UP, DOWN
	};
	
	// The paddle's current direction
	public Direction currentDirection = Direction.NONE;
	
	// The number of characters in each message part
	private static final int MESSAGE_LINE_CHARS = 16;
	
	// The font used for the latest message
	private static Font latestMessageFont = null;
	// The latest message sent from this paddle
	private String latestMessage = null;
	// The length of the latest message's longest line. Used for positioning.
	private int latestMessageLongestLine = 0;
	
	/**
	 * Creates a paddle at the specified position using the default size.
	 * @param x The X position of the paddle
	 * @param y The Y position of the paddle
	 */
	public Paddle(int x, int y){
		super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		// Default the paddle colour to white
		setColour(Color.WHITE);
		
		// Create the font used for the latest message if it hasn't been created yet
		if(Paddle.latestMessageFont == null){
			Paddle.latestMessageFont = new Font("Monospaced", Font.PLAIN, 12);
		}
	}
	
	/**
	 * Moves the paddle in the given direction.
	 * @param direction
	 */
	public void move(Direction direction){
		switch(direction){
		case UP:{
			y -= MOVE_SPEED;
			break;
		}
		case DOWN:{
			y += MOVE_SPEED;
			break;
		}
		default:{
			Utils.log("Unknown direction given: " + direction);
			break;
		}
		}
		
		// Prevent the paddle going off the screen
		int gamePanelHeight = Utils.mainFrame.gamePanel.getHeight();
		if(y + height > gamePanelHeight) y -= ((y + height) - gamePanelHeight);
		else if(y < 0) y += (0 - y);
		
		// Set the current direction to the one given
		currentDirection = direction;
	}
	
	/**
	 * Automatically updates the paddle, simulating another player's movement.
	 * @param ball The ball
	 */
	public void autoUpdate(Ball ball){
		// Only update if the ball is moving towards this paddle
		int ballMoveX = ball.getMoveSpeedX();
		if((ball.x + ball.width < x && ballMoveX > 0) || (ball.x > x + width && ballMoveX < 0)){
			// Move the paddle, trying to get the centre of it to match up with the ball.
			// Get the difference between the ball's centre point and paddle's centre point
			int centreDifference = (ball.y + (ball.height / 2)) - (y + (height / 2));
			if(centreDifference > 0) move(Direction.DOWN);
			else if(centreDifference < 0) move(Direction.UP);
		}
	}
	
	/**
	 * Overridden to render the latest message above the paddle.
	 */
	public void render(Graphics graphics){
		super.render(graphics);
		
		// If there's a latest message then render it
		if(latestMessage != null){
			// We'll be using the 'latest message' font
			graphics.setFont(latestMessageFont);
			
			// Get the font metrics for the font
			FontMetrics fontMetrics = graphics.getFontMetrics();
			
			// Get the width of the latest message
			int messageWidth;
			if(latestMessage.length() > MESSAGE_LINE_CHARS){
				// Get the width of a message part
				messageWidth = fontMetrics.stringWidth(latestMessage.substring(0, latestMessageLongestLine));
			}
			else{
				// Get the width of the whole message
				messageWidth = fontMetrics.stringWidth(latestMessage);
			}
			
			// Calculate the X coordinate of the message
			int messageX = (x + (width / 2)) - (messageWidth / 2);
			
			// Split the message on its new lines (if there are any)
			String[] messageParts = latestMessage.split("\n");
			
			// Draw each message part
			for(int part = 0; part < messageParts.length; part++){
				graphics.drawString(messageParts[part], messageX, y - (fontMetrics.getHeight() * (messageParts.length - part)));
			}
		}
	}
	
	/**
	 * Sets the latest message to the one given.
	 * @param message The new latest message
	 */
	public void setLatestMessage(String message){
		// If needed, then put the message into lines to prevent it being too long and going off
		// the screen
		if(message.length() > MESSAGE_LINE_CHARS){
			// Split the message at its spaces
			String[] messageParts = message.split(" ");
			
			// Put the message back together, adding a new line every so often
			String newMessage = "";
			int currentLineChars = 0;
			int longestLine = 0;
			for(int part = 0; part < messageParts.length; part++){
				// If this part will make us reach the character limit for a line then add a \n.
				// (Add 1 to the part length because we'll be adding a space too
				if(currentLineChars + messageParts[part].length() + 1 >= MESSAGE_LINE_CHARS){
					currentLineChars = 0;
					newMessage += "\n";
				}
				
				// Add a space in front of the message part if it's not the first one (because we
				// split on spaces) and if this isn't the first part in the current line
				if(part != 0 && currentLineChars > 0) messageParts[part] = " " + messageParts[part];
				
				// Add the part to the new message
				newMessage += messageParts[part];
				currentLineChars += messageParts[part].length();
				
				// Check if the current line is longer than the previous longest one
				if(currentLineChars > longestLine) longestLine = currentLineChars;
			}
			
			// Store the new message
			latestMessage = newMessage;
			latestMessageLongestLine = longestLine;
		}
		else{
			// Otherwise we'll just use the message as it was given to us
			latestMessage = message;
		}
	}
}
