package field;
import processing.core.PApplet;
import java.util.List;

public class Obstacle 
{
	
	float x;
	float y;
	float width;
	float height;
	
	PApplet parent;
	
	Obstacle(float xPos, float yPos, PApplet p)
	{
		x = xPos;
		y = yPos;
		
		x = (float) Math.random()*750;
		y = (float) Math.random()*750;
		
		parent = p;
		
		width = parent.random(10,50);
		height = parent.random(10,50);

	}
	
	public void drawObstacle() 
	{
		parent.fill(255,0,0);
		parent.rect(x, y, width, height);
	}
	
	public float getX() 
	{
		return x;
	}
	
	public float getY() 
	{
		return y;
	}
	
	public float getWidth() 
	{
		return width;
	}
	
	public float getHeight() 
	{
		return height;
	}
}
