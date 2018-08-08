package field;
import processing.core.PApplet;

public class Window extends PApplet 
{
	Window()
	{
		PApplet.runSketch(new String[] {this.getClass().getSimpleName()}, this);
	}
	
	public void settings() 
	{
		size(500,500);
	}
	
	public void setup() 
	{
		
	}
	
	public void draw() 
	{
		background(255);
	}
}
