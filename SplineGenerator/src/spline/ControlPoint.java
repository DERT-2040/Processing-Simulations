package spline;

import processing.core.PApplet;

public class ControlPoint 
{
	
	float x, y; //The x and y position of the control point
	float width = 10; //The distance horizontally from the center to the side
	float height = 10; //The distance vertically from the center to the side
	float mouseCollisionTolerance = 5; //The tolerance of the mouse clicking the control points in pixels
	PApplet window; //the PApplet that the control point is in
	
	/**
	 * Creates a Control Point
	 * @param _window The PApplet the control point is in.
	 * @param _x The x location of the control point.
	 * @param _y The y location of the control point.
	 */
	public ControlPoint(PApplet _window, float _x, float _y) 
	{
		window = _window;
		x = _x;
		y = _y;
	}
	
	/**
	 * Draws the control point
	 */
	public void drawPoint() 
	{
		window.rectMode(2); //The rectangle drawing mode where the x and y given are the center instead of a corner
		window.rect(x, y, width, height);
	}
	
	/**
	 * Sets the location of the control point
	 * 
	 * @param _x the new x location
	 * @param _y the new y location
	 */
	public void setLocation(float _x, float _y) 
	{
		x = _x;
		y = _y;
	}
	
	/**
	 * Returns the x position of the control point
	 * @return x
	 */
	public float getX()
	{
		return x;
	}
	
	/**
	 * Returns the y position of the control point
	 * @return y
	 */
	public float getY() 
	{
		return y;
	}
	
	/**
	 * Gets the distance from the center to the vertical side
	 * @return width
	 */
	public float getWidth() 
	{
		return width;
	}
	
	/**
	 * Gets the distance from the center to the horizontal side
	 * @return height
	 */
	public float getHeight() 
	{
		return height;
	}
	
	/**
	 * Checks to see if the mouse is over the control point
	 * @return true if the mouse is over the point and false if it is not
	 */
	public boolean checkForMouseCollision() 
	{
		if(window.mouseX < x+width+mouseCollisionTolerance && window.mouseX > x-width-mouseCollisionTolerance) 
		{
			if(window.mouseY < y+height+mouseCollisionTolerance && window.mouseY > y-height-mouseCollisionTolerance) 
			{
				return true;
			}
		}
		return false;
	}
	
}
