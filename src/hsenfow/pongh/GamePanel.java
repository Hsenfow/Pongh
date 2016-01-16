package hsenfow.pongh;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import hsenfow.pongh.Entities.Ball;
import hsenfow.pongh.Entities.Paddle;
import hsenfow.pongh.Entities.Paddle.Direction;
import hsenfow.pongh.Input.GameKeyListener;
import hsenfow.pongh.Network.NetworkCommunications;
import hsenfow.pongh.Network.NetworkUtils;
import hsenfow.pongh.Settings.Setting;

public class GamePanel extends JPanel{
	private static final long serialVersionUID = -5321059453993299436L;
	
	// The amount of time to sleep per frame (minus the amount of time the frame took to update and
	// render). We'll be targeting roughly 60fps
	private final long FRAME_SLEEP_TIME = 17;
	
	// The game key listener
	private GameKeyListener gameKeyListener;
	
	// Whether the game setup has been performed
	private boolean gameSetup = false;
	
	// The two paddles
	public Paddle paddleOne, paddleTwo;
	
	// The ball
	private Ball ball;
	
	/**
	 * Initialises everything to do with the game.
	 */
	public GamePanel(){
		// We want to be focusable
		setFocusable(true);
		
		// Set the background colour
		// TODO Allow the background colour (and paddle colour) to be customised
		setBackground(Color.BLACK);
		
		// We'll be listening for key presses
		gameKeyListener = new GameKeyListener();
		addKeyListener(gameKeyListener);
		
		// Ensure the game isn't paused
		Utils.gamePaused = false;
	}
	
	/**
	 * Sets up the game. This must be done outside of the constructor, as we require the panel's
	 * size.
	 */
	private void setupGame(){
		// Create the two paddles
		paddleOne = new Paddle(50, (getHeight() / 2) - (Paddle.DEFAULT_HEIGHT / 2));
		paddleOne.setColour(Utils.getColourFromString(Settings.getSettingValue(Setting.PADDLE_1_COLOUR)));
		paddleTwo = new Paddle(getWidth() - Paddle.DEFAULT_WIDTH - 50,
				(getHeight() / 2) - (Paddle.DEFAULT_HEIGHT / 2));
		
		// Create the ball
		ball = new Ball((getWidth() / 2) - (Ball.DEFAULT_WIDTH / 2),
				(getHeight() / 2) - (Ball.DEFAULT_HEIGHT / 2));
		
		// The game has been set up
		gameSetup = true;
	}
	
	/**
	 * Updates and renders the game.
	 */
	@Override
	public void paintComponent(Graphics graphics){
		super.paintComponent(graphics);
		// TODO Should probably change this to use a BufferedImage, then AA can be enabled
		
		// If this is the first call then set up the game first
		if(!gameSetup){
			setupGame();
		}
		
		// Get the current time
		long startTime = System.currentTimeMillis();
		
		// Update everything
		update();
		
		// Render everything
		render(graphics);
		
		// See how long the updating and rendering took
		long timeTaken = System.currentTimeMillis() - startTime;
		
		// Work out the sleep time
		long sleepTime = FRAME_SLEEP_TIME - timeTaken;
		
		// Sleep if the sleep time is greater than 0
		if(sleepTime > 0){
			try{
				Thread.sleep(sleepTime);
			}catch(InterruptedException ie){ /* Nothing */ }
		}
		
		// Redraw the game again
		repaint();
	}
	
	/**
	 * Updates the game.
	 */
	private void update(){
		// If the game is paused then don't do anything
		if(Utils.gamePaused) return;
		
		// If we're playing multiplayer, then do the multiplayer update instead
		if(NetworkUtils.connected){
			multiplayerUpdate();
			return;
		}
		else{
			// Update paddle one
			paddleOne.currentDirection = Paddle.Direction.NONE;
			if(gameKeyListener.isKeyPressed(KeyEvent.VK_UP) && !gameKeyListener.isKeyPressed(KeyEvent.VK_DOWN)){
				paddleOne.move(Paddle.Direction.UP);
			}
			else if(gameKeyListener.isKeyPressed(KeyEvent.VK_DOWN) && !gameKeyListener.isKeyPressed(KeyEvent.VK_UP)){
				paddleOne.move(Paddle.Direction.DOWN);
			}
			
			// Automatically update paddle two
			paddleTwo.autoUpdate(ball);
			
			// Update the ball
			ball.update();
			
			// Check whether the ball is colliding with either of the paddles
			if(!ball.checkPaddleCollision(paddleOne)){
				ball.checkPaddleCollision(paddleTwo);
			}
		}
	}
	
	/**
	 * Does the updating required for multiplayer.
	 */
	private void multiplayerUpdate(){
		// Update paddle one
		paddleOne.currentDirection = Paddle.Direction.NONE;
		if(gameKeyListener.isKeyPressed(KeyEvent.VK_UP) && !gameKeyListener.isKeyPressed(KeyEvent.VK_DOWN)){
			paddleOne.move(Paddle.Direction.UP);
			
			// Send a message with the player's current paddle direction
			NetworkCommunications.sendMessage(NetworkCommunications.MESSAGE_MOVE + paddleOne.currentDirection.name());
		}
		else if(gameKeyListener.isKeyPressed(KeyEvent.VK_DOWN) && !gameKeyListener.isKeyPressed(KeyEvent.VK_UP)){
			paddleOne.move(Paddle.Direction.DOWN);
			
			// Send a message with the player's current paddle direction
			NetworkCommunications.sendMessage(NetworkCommunications.MESSAGE_MOVE + paddleOne.currentDirection.name());
		}
		
		// Update the ball if we're the server
		if(NetworkUtils.isServer){
			ball.update();
			
			// Now send the ball's new position to the client, but invert its X first, so that it
			// appears in the correct place when positioned on the client's screen
			NetworkCommunications.sendMessage(NetworkCommunications.MESSAGE_BALL_POS
					+ ((getWidth() - ball.getX()) - ball.getWidth()) + ";" + ball.getY());
		}
		
		// Check whether the ball is colliding with either of the paddles
		if(!ball.checkPaddleCollision(paddleOne)){
			ball.checkPaddleCollision(paddleTwo);
		}
	}
	
	/**
	 * Moves paddle one in the specified direction.
	 */
	public void movePaddleOne(Direction direction){
		if(paddleOne != null) paddleOne.move(direction);
	}
	
	/**
	 * Moves paddle two in the specified direction.
	 */
	public void movePaddleTwo(Direction direction){
		if(paddleTwo != null) paddleTwo.move(direction);
	}
	
	/**
	 * Sets the ball to the given position.
	 * @param x The ball's new X coordinate
	 * @param y The ball's new Y coordinate
	 */
	public void setBallPosition(int x, int y){
		if(ball != null) ball.setPosition(x, y);
	}
	
	/**
	 * Renders the game.
	 * @param graphics The graphics object to use to render everything
	 */
	private void render(Graphics graphics){
		// Render the two paddles
		paddleOne.render(graphics);
		paddleTwo.render(graphics);
		
		// Render the ball
		ball.render(graphics);
		
		// Render the middle line
		int middleX = getWidth() / 2;
		graphics.setColor(Color.DARK_GRAY);
		graphics.drawLine(middleX, 0, middleX, getHeight());
		
		// If we're creating a message, then display it
		if(gameKeyListener.creatingMessage){
			graphics.setColor(Color.GREEN);
			graphics.drawString("Message: " + gameKeyListener.currentMessage, 10, 32);
		}
	}
}
