package hsenfow.pongh.Entities;

import java.awt.Color;
import java.awt.Graphics;

import hsenfow.pongh.Settings;
import hsenfow.pongh.Utils;
import hsenfow.pongh.Settings.Setting;

public class Ball extends Entity{
	
	// The name of the bounce sound file
	public static final String BOUNCE_SOUND_FILE = "bounce.wav";
	
	// The default size of the ball
	public static final int DEFAULT_WIDTH = 20;
	public static final int DEFAULT_HEIGHT = 20;
	
	// The default move speed
	public static final int DEFAULT_MOVE_SPEED = 5;
	
	// The ball's current move speed
	private int moveSpeedX = DEFAULT_MOVE_SPEED;
	private int moveSpeedY = DEFAULT_MOVE_SPEED;
	
	/**
	 * Creates a ball at the given position using the default size.
	 * @param x The X position of the ball
	 * @param y The Y position of the ball
	 */
	public Ball(int x, int y){
		super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		// Try to get the setting for the ball's colour
		Color ballColour = Utils.getColourFromString(Settings.getSettingValue(Setting.BALL_COLOUR));
		// Default the ball's colour to white if one hasn't been specified
		if(ballColour == null) ballColour = Color.WHITE;
		setColour(ballColour);
	}
	
	/**
	 * Used to get the ball's X move speed.
	 * @return The ball's X move speed
	 */
	public int getMoveSpeedX(){
		return moveSpeedX;
	}
	
	/**
	 * Updates the position of the ball.
	 */
	public void update(){
		y += moveSpeedY;
		x += moveSpeedX;
		
		// Make the ball bounce (reverse direction) when it reaches the top or bottom of the window
		int gamePanelHeight = Utils.mainFrame.gamePanel.getHeight();
		if(y + height > gamePanelHeight){
			// Correct its position
			y -= (y + height) - gamePanelHeight;
			// Reverse its direction
			moveSpeedY *= -1;
			// Play a bounce sound
			Utils.playAudio(Utils.AUDIO_FOLDER + Ball.BOUNCE_SOUND_FILE);
		}
		else if(y < 0){
			// Correct its position
			y += (0 - y);
			// Reverse its direction
			moveSpeedY *= -1;
			// Play a bounce sound
			Utils.playAudio(Utils.AUDIO_FOLDER + Ball.BOUNCE_SOUND_FILE);
		}
		
		// TODO Game over
		// For the time being, just reposition the ball back into the centre of the screen
		if(x < 0 || x > Utils.mainFrame.gamePanel.getWidth()){
			x = Utils.mainFrame.gamePanel.getWidth() / 2;
			y = Utils.mainFrame.gamePanel.getHeight() / 2;
			moveSpeedX *= -1;
			moveSpeedY *= -1;
		}
	}
	
	/**
	 * Checks whether the ball is colliding with the given paddle and if so, reverses its X
	 * direction.
	 * @param paddle The paddle to check the collision with
	 * @return Whether or not the ball was colliding with the paddle
	 */
	public boolean checkPaddleCollision(Paddle paddle){
		// TODO There are some small problems with this
		
		// Check whether the ball has collided with the paddle horizontally in some way
		if((x + width >= paddle.x && x + width <= paddle.x + paddle.width)
				|| (x >= paddle.x && x <= paddle.x + paddle.width)){
			
			// Check how the ball has collided vertically
			if((moveSpeedX > 0 ? (x + width <= paddle.x + moveSpeedX) : (x >= paddle.x + paddle.width + moveSpeedX))
					&&(y <= paddle.y + paddle.height && y >= paddle.y)
					|| (y + height <= paddle.height && y + height >= paddle.y)){ // A normal, side-on collision
				
				// Correct the ball's position (so it isn't inside the paddle
				if(moveSpeedX > 0) x += (x + width) - paddle.x;
				else x += paddle.x + paddle.width - x;
				
				// Reverse the X direction
				moveSpeedX *= -1;
				
				// Set the ball's new Y direction depending on the direction the paddle was
				// travelling in
				if(paddle.currentDirection == Paddle.Direction.UP){
					if(moveSpeedY > 0) moveSpeedY *= -1;
				}
				else if(paddle.currentDirection == Paddle.Direction.DOWN){
					if(moveSpeedY < 0) moveSpeedY *= -1;
				}
				
				Utils.playAudio(Utils.AUDIO_FOLDER + Ball.BOUNCE_SOUND_FILE);
				return true;
			}
			else if(moveSpeedY > 0 && y + height >= paddle.y
					&& y + height < paddle.y + paddle.height){ // Collided with the top of the paddle
				// Correct the ball's position
				y = paddle.y - height;
				// Reverse the ball's Y position
				moveSpeedY *= -1;
				
				Utils.playAudio(Utils.AUDIO_FOLDER + Ball.BOUNCE_SOUND_FILE);
				return true;
			}
			else if(moveSpeedY < 0 && y <= paddle.y + paddle.height
					&& y > paddle.y){ // Collided with the bottom of the paddle
				// Correct the ball's position
				y = paddle.y + paddle.height;
				// Reverse the ball's Y position
				moveSpeedY *= -1;
				
				Utils.playAudio(Utils.AUDIO_FOLDER + Ball.BOUNCE_SOUND_FILE);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Overridden so that the ball can be a circle.
	 */
	@Override
	public void render(Graphics graphics){
		graphics.setColor(colour);
		graphics.fillOval(x, y, width, height);
	}
	
}
