package robotSystems;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

import pathing.Point;

public class Robot 
{
	double Rx;
	double Ry;
	double maxSpeed = 4;
	double xComponent;
	double yComponent;
	double xVelocity;
	double yVelocity;
	double angle;
	
	float x;
	float y;
	float robotWidth = 15;
	float robotHeight = 15;
	
	int waypointWidth = 5;
	int waypointHeight = 5;
	
	PID pid = new PID(0.03, 0.0, 0.0);
	
	List<Point> waypoints = new ArrayList<Point>();
	
	PApplet parent;
	
	public Robot(PApplet p)
	{
		parent = p;
	}
	
	public void spawnRobot(float xPos, float yPos) 
	{
		x = xPos;
		y = yPos;
		
		parent.fill(0, 0, 255);
		parent.rect(x, y, robotWidth, robotHeight);
		 
	    Rx = x + .5*robotWidth;
	    Ry = y + .5*robotHeight;
	}
	
	public void curveAnalysis(List<Point> points) 
	{
		int numSinceLastPoint = 0;
		int n = points.size() - 1;
		waypoints.add(new Point(points.get(0).getX(), points.get(0).getY()));
		
		for(int i = 0; i < points.size()-1; i++) 
		{
			
			numSinceLastPoint++;
			
			if(numSinceLastPoint > 25)
			{
				waypoints.add(new Point(points.get(i).getX(), points.get(i).getY()));
			    numSinceLastPoint = 0;
			}
			
		}
		
		waypoints.add(new Point(points.get(n).getX(), points.get(n).getY()));
	}
	
	public void drawWaypoints()
	{	
	    for(int i = 0; i < waypoints.size(); i++)
	    { 	
	      parent.fill(0,255,0);
	      parent.rect(waypoints.get(i).getX(), waypoints.get(i).getY(), waypointWidth, waypointHeight);   
	    } 
	}
	
	public double waypointDistance(int startingWP, int finalWP) 
	{
		double distance = 0;
		
		for(int i = 0; i < waypoints.size()-1; i++)
		{
			distance += Math.sqrt(Math.pow((waypoints.get(i+1).getX() - waypoints.get(i).getX()),2)+Math.pow((waypoints.get(i+1).getY() - waypoints.get(i).getY()),2));
		}
		
		return distance;
	}
	
	public void followLine(){
	    
	    double speed;
	    
	    if(waypoints.size() > 1)
	    {
	      if((Rx > waypoints.get(0).getX()-5) && (Rx < waypoints.get(0).getX() + waypointWidth+5) && (Ry < waypoints.get(0).getY()+5) && (Ry > waypoints.get(0).getY() - waypointHeight-5))
	      {
	         waypoints.remove(0);
	      }
	    }
	    
	    if(waypoints.size() > 0)
	    {
	      
	      xComponent = (waypoints.get(0).getX() - Rx);
	      yComponent = (waypoints.get(0).getY() - Ry);

	      angle = Math.atan2(yComponent, xComponent);
	    
	      if(waypoints.size() > 1){
	        speed = -pid.getResultant(waypointDistance(0, waypoints.size()));
	      }
	      else{
	        speed = -pid.getResultant(Math.sqrt(Math.pow((waypoints.get(0).getX() - Rx), 2) + Math.pow((waypoints.get(0).getY() - Ry), 2)));
	      }
	      
	      if(speed > maxSpeed){
	        speed = maxSpeed;
	      }
	      
	      xVelocity = speed*Math.cos(angle);
	      yVelocity = speed*Math.sin(angle);
	      
	    }
	    
	}
	
	public void updateRobot() 
	{
		
		x += xVelocity;
		y += yVelocity;
		
		parent.fill(0, 0, 255);
		parent.rect(x, y, robotWidth, robotHeight);
		
		Rx = x + .5*robotWidth;
		Ry = y + .5*robotHeight;
	}
	
}
