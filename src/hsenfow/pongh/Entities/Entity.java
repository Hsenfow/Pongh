package hsenfow.pongh.Entities;

import java.awt.Color;
import java.awt.Graphics;

public class Entity {
	
	// The size of the entity
	protected int width, height;
	
	// The position of the entity
	protected int x, y;
	
	// The colour of the entity
	protected Color colour = null;
	
	/**
	 * Creates an entity using the given position and size.
	 * @param x The X position of the entity
	 * @param y The Y position of the entity
	 * @param width The width of the entity
	 * @param height The height of the entity
	 */
	public Entity(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Used to get the X coordinate of the entity.
	 * @return The entity's X coordinate
	 */
	public int getX(){
		return x;
	}
	
	/**
	 * Used to get the Y coordinate of the entity.
	 * @return The entity's Y coordinate
	 */
	public int getY(){
		return y;
	}
	
	/**
	 * Used to get the width of the entity.
	 * @return The entity's width
	 */
	public int getWidth(){
		return width;
	}
	
	/**
	 * Used to get the height of the entity.
	 * @return The entity's height
	 */
	public int getHeight(){
		return height;
	}
	
	/**
	 * Sets the entity to the given colour.
	 * @param newColour The entity's new colour
	 */
	public void setColour(Color newColour){
		this.colour = newColour;
	}
	
	/**
	 * Renders the entity.
	 * @param graphics The graphics object to use to render the paddle
	 */
	public void render(Graphics graphics){
		if(colour != null) graphics.setColor(colour);
		graphics.fillRect(x, y, width, height);
	}
}
