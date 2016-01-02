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

public class GamePanel extends JPanel{
	private static final long serialVersionUID = -5321059453993299436L;
	
	// The amount of time to sleep per frame (minus the amount of time the frame took to update and
	// render). We'll be targeting roughly 60fps
	private final long FRAME_SLEEP_TIME = 17;
	
	// The game key listener
	private GameKeyListener gameKeyListener;
	
	// The two paddles
	private Paddle paddleOne, paddleTwo;
	
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
		
		// Create the two paddles
		paddleOne = new Paddle(50, (Utils.mainFrame.getHeight() / 2) - (Paddle.DEFAULT_HEIGHT / 2));
		paddleTwo = new Paddle(Utils.mainFrame.getWidth() - Paddle.DEFAULT_WIDTH - 50,
				(Utils.mainFrame.getHeight() / 2) - (Paddle.DEFAULT_HEIGHT / 2));
		
		// Create the ball
		ball = new Ball(paddleOne.getX() + paddleOne.getWidth(), paddleOne.getY());
	}
	
	/**
	 * Updates and renders the game.
	 */
	@Override
	public void paintComponent(Graphics graphics){
		super.paintComponent(graphics);
		// TODO Should probably change this to use a BufferedImage, then AA can be enabled
		
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
		
		// Update paddle one
		paddleOne.currentDirection = Paddle.Direction.NONE;
		if(gameKeyListener.isKeyPressed(KeyEvent.VK_UP) && !gameKeyListener.isKeyPressed(KeyEvent.VK_DOWN)){
			paddleOne.move(Paddle.Direction.UP);
			
			// If we're playing multiplayer then send a message stating which way the paddle has moved
			if(NetworkUtils.connected) NetworkCommunications.sendMessage(NetworkCommunications.MESSAGE_MOVE + Paddle.Direction.UP.name());
		}
		else if(gameKeyListener.isKeyPressed(KeyEvent.VK_DOWN) && !gameKeyListener.isKeyPressed(KeyEvent.VK_UP)){
			paddleOne.move(Paddle.Direction.DOWN);
			
			// If we're playing multiplayer then send a message stating which way the paddle has moved
			if(NetworkUtils.connected) NetworkCommunications.sendMessage(NetworkCommunications.MESSAGE_MOVE + Paddle.Direction.DOWN.name());
		}
		
		// Automatically update paddle two if we're not playing Multipongh
		if(!NetworkUtils.connected) paddleTwo.autoUpdate(ball);
		
		// Update the ball
		ball.update();
		
		// Check whether the ball is colliding with either of the paddles
		if(!ball.checkPaddleCollision(paddleOne)){
			ball.checkPaddleCollision(paddleTwo);
		}
	}
	
	/**
	 * Moves paddle one in the specified direction.
	 */
	public void movePaddleOne(Direction direction){
		paddleOne.move(direction);
	}
	
	/**
	 * Moves paddle two in the specified direction.
	 */
	public void movePaddleTwo(Direction direction){
		paddleTwo.move(direction);
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
	}
}
