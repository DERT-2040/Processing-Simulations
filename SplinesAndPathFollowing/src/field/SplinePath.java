package field;
import processing.core.PApplet;
import robotSystems.Robot;

import java.util.ArrayList;
import java.util.List;

import pathing.*;

public class SplinePath extends PApplet 
{
	
	int splineType = 1;
	int degreeofPiecewise = 3;
	int indexofClickedCP;
	int xResolution = 750;
	int yResolution = 750;
	int obstacleNumber = 50;
	
	float sensitivity = 30;
	
	boolean analyze = false;
	boolean controlPointClicked = false;
	boolean robotSpawned = false;
	boolean drawNodeMap = true;
	boolean generateObstacles = true;
	boolean drawInterpolatedPoints = false;
	
	List<ControlPoint> controlPoints = new ArrayList<ControlPoint>();
	List<Obstacle> obstacles = new ArrayList<Obstacle>();
	
	Curve c = new Curve(this, controlPoints, drawInterpolatedPoints);
	Robot r = new Robot(this);
	NodeMap map;
	
	public static void main(String[] args) 
	{
		PApplet.main("field.SplinePath");
	}
	
	public void settings() 
	{
		size(xResolution, yResolution);
	}
	
	public void setup() 
	{
		background(255);
		
		if(generateObstacles == true) //Generates obstacles if generateObstacles equals true
		{
			for(int i = 0; i < obstacleNumber; i++) 
			{
				obstacles.add(new Obstacle(0, 0, this));
			}
		}
		map  = new NodeMap(50, 50, xResolution, yResolution, this, obstacles);
	}
	
	public void draw() 
	{
		background(255); //Sets the background to white and clears previous screen
		
		if(generateObstacles == true) //draws the generated obstacles
		{
			for(int i = 0; i < obstacles.size(); i++) 
			{
				obstacles.get(i).drawObstacle();
			}
		}
		
		if(drawNodeMap == true) //draws the Node Map
		{
			map.drawMap();
		}
		
		for(int i = 0; i < controlPoints.size(); i++) //draws all of the current Control Points
		{
			controlPoints.get(i).drawControlPoint();
		}
		
		c.updateControlPoints(controlPoints); 
		c.interpolate(splineType, degreeofPiecewise);
		c.drawLine();
		
		if(analyze == true) 
		{
			r.curveAnalysis(c.getPoints());
			analyze = false;
		}
		
		r.drawWaypoints();
		
		if(robotSpawned == true) //if robot has spawned follow the line, and update the Robots position;
		{
			r.followLine();
			r.updateRobot();
		}
	
	
		moveControlPoints();
	}
	
	public void keyReleased() 
	{
		if((key == 'z') && (controlPoints.size() > 0))
		{
			controlPoints.remove(controlPoints.size()-1);
		}
			  
		if(key == 'x')
		{
			ControlPoint p = new ControlPoint(mouseX, mouseY, controlPoints.size(), this);
			controlPoints.add(p);
		}
		
		if(key == 'p')
		{
			splineType = 0;
		}
			  
		if(key == 'b')
		{
			splineType = 1;
		}
			  
		if(key == 'n')
		{
			splineType = 2;
		}
		
		if(key == 'i') 
		{
			splineType = 3;
		}
		
		if(key == 'a') 
		{
			analyze = true;
		}
		
		if(key == 'r') 
		{
			r.spawnRobot(controlPoints.get(0).getX(), controlPoints.get(0).getY());
			robotSpawned = true;
		}
	}
	
	public void moveControlPoints() 
	{
		if(mousePressed) 
		{
			float x;
			float y;
			controlPointClicked = false;
			
			//Check old point first to see if it is still being clicked
			if(controlPoints.size()> 0) 
			{
				x = controlPoints.get(indexofClickedCP).getX();
				y = controlPoints.get(indexofClickedCP).getY();
				
				if((mouseX > x - sensitivity) && (mouseX < x + sensitivity) && (mouseY > y - sensitivity) && (mouseY < y + sensitivity)) 
				{
					controlPointClicked = true;
				}
			}
			
			//if old point isn't clicked, or this is the first iteration check all the points
			if((controlPoints.size() > 0)) 
			{
				for(int i = 0; i < controlPoints.size(); i++) 
				{
					x = controlPoints.get(i).getX();
					y = controlPoints.get(i).getY();
					
					if((mouseX > x - sensitivity) && (mouseX < x + sensitivity) && (mouseY > y - sensitivity) && (mouseY < y + sensitivity)) 
					{
						controlPointClicked = true;
						indexofClickedCP = i;
						break;
					}
				}
			}
			
			//update clicked points position
			if(controlPointClicked == true) 
			{
				controlPoints.get(indexofClickedCP).setX(mouseX);
				controlPoints.get(indexofClickedCP).setY(mouseY);
			}
			
		}
	}
}
