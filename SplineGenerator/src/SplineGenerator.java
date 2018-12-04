import processing.core.PApplet;
import java.util.List;
import java.util.ArrayList;
import spline.*;

public class SplineGenerator extends PApplet
{
	
	List<ControlPoint> controlPoints = new ArrayList<ControlPoint>();
	Spline spline = new Spline(this);
	
	/*
	 * 0 = place mode
	 * 1 = move mode
	 * 2 = delete mode
	 */
	int controlPointMode = 0; 
	
	//int test = 0;
	
	int previousControlPointIndex = 0;
	
	public static void main(String[] args) 
	{
		PApplet.main("SplineGenerator");
	}
	
	public void settings() 
	{
		size(750,750);
	}
	
	public void setup() 
	{
		background(255);
	}
	
	public void mouseClicked() 
	{
		if(controlPointMode == 0) 
		{
			controlPoints.add(new ControlPoint(this, mouseX, mouseY));
			
		}
		else if(controlPointMode == 2) 
		{
			for(int i = 0; i < controlPoints.size(); i++) 
			{
				if(controlPoints.get(i).checkForMouseCollision() == true) 
				{
					controlPoints.remove(i);
					if(previousControlPointIndex > 0) 
					{
						previousControlPointIndex--;
					}
				}
			}
		}
	}
	
	public void mousePressed() 
	{

	}
	
	public void keyPressed() 
	{
		//Sets the control point mode to place, move, or delete
		if(key == 'p') 
		{
			controlPointMode = 0;
		}
		else if(key == 'm') 
		{
			controlPointMode = 1;
		}
		else if(key == 'd') 
		{
			controlPointMode = 2;
		}
		
		if(key == 'q') 
		{
			spline.exportPoints();
		}
		
	}
	
	public void draw() 
	{
		background(255);
		
		//Only runs if the control point mode is 1(moving mode))
		//Logic to move the currently clicked control point
		if(controlPointMode == 1 && mousePressed == true && controlPoints.size() > 0) 
		{
			//Checks to see if the previous point is still clicked to save time
			if(controlPoints.get(previousControlPointIndex).checkForMouseCollision() == true) 
			{
				controlPoints.get(previousControlPointIndex).setLocation(mouseX, mouseY);
				//System.out.println("previous");
			}
			else 
			{
				//Checks each point to see if it is being clicked
				for(int i = 0; i < controlPoints.size(); i++) 
				{
					if(controlPoints.get(i).checkForMouseCollision() == true) 
					{
						controlPoints.get(i).setLocation(mouseX, mouseY);
						//System.out.println("found");
						previousControlPointIndex = i;
						break;
					}
				}
			}
		}
		
		//Draws all of the control points
		for(int i = 0; i < controlPoints.size(); i++) 
		{
			controlPoints.get(i).drawPoint();
		}
		
		spline.setControlPoints(controlPoints);
		spline.calculateSpline();
		spline.calculateSimplifiedPoints();
		spline.drawSimplePoints();

	}
	
	
}