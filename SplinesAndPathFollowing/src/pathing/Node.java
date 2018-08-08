package pathing;

import processing.core.PApplet;

public class Node 
{
	
	float x;
	float y;
	float nodeWidth = 5;
	float nodeHeight = 5;
	
	boolean disabled = false;
	
	PApplet parent;
	
	public Node(float xPos, float yPos, PApplet p) 
	{
		x = xPos;
		y = yPos;
		
		parent = p;
	}
	
	public void drawNode() 
	{
		parent.fill(135, 206, 250);
		parent.ellipse(x, y, nodeWidth, nodeHeight);
	}
	
	public float getX() 
	{
		return x;
	}
	
	public float getY() 
	{
		return y;
	}
	
	public boolean getNodeState() 
	{
		return disabled;
	}
	
	public void disabled() 
	{
		disabled = true;
	}
	
	public void enable() 
	{
		disabled = false;
	}
}
