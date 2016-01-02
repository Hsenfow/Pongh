package hsenfow.pongh.Entities;

import java.awt.Color;

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
	
	/**
	 * Creates a paddle at the specified position using the default size.
	 * @param x The X position of the paddle
	 * @param y The Y position of the paddle
	 */
	public Paddle(int x, int y){
		super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		// Default the paddle colour to white
		setColour(Color.WHITE);
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
		int gamePanelHeight = Utils.mainFrame.getGamePanelHeight();
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
}
