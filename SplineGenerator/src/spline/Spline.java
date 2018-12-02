package spline;

import java.util.ArrayList;
import java.util.List;
import Jama.Matrix;
import processing.core.PApplet;
import java.lang.Math;
import org.json.simple.*;
import java.io.FileWriter;
import java.io.IOException;

public class Spline 
{
	List<ControlPoint> controlPoints = new ArrayList<ControlPoint>();
	List<Point> points = new ArrayList<Point>();
	List<Point> simplePoints = new ArrayList<Point>();
	
	double[][] pointArray; //The array of simplified points to be exported to JSON
	double interval = .001; //The increment on which the b-spline is calculated on from [0,1]
	double changeInAngleTolerance = .07; //The minimum change in angle for a point to made
	double minimumSinceLastPoint = 35; //The minimum number of points to be skipped since the last point 
	double maximumSinceLastPoint = 150; //The maximum number points that can be skipped without generating a point
	
	int test;
	ControlPoint lastPoint;
	JSONObject export = new JSONObject();
	PApplet window; //The PApplet that the spline curve is in
	
	String fileWriterLocation = "/home/ubuntu/Desktop/spline.json";
	
	/**
	 * Creates a spline object
	 * @param _window The PApplet object that the spline is in
	 */
	public Spline(PApplet _window)
	{
		window = _window;
	}
	
	/**
	 * Takes in a list of control points and sets the spline's control points equal to it
	 * @param _controlPoints A list of control points
	 */
	public void setControlPoints(List<ControlPoint> _controlPoints) 
	{
		controlPoints = _controlPoints;
	}
	
	/**
	 * It calculates a interpolated b-spline by magic for which I forget the incantation.
	 * The generated points are stored in the spline points variable 
	 */
	public void calculateSpline() 
	{
		if(controlPoints.size() > 1) 
		{
			lastPoint = controlPoints.get(controlPoints.size()-1);
		}
		points.clear();
		int n = controlPoints.size();
    	if(n > 3)
    	{
	    	float tx;
	    	float ty;
	    	
	    	List<ControlPoint> bezierControlPoints = new ArrayList<ControlPoint>();
	    	
	    	Point[] bezierCP = new Point[n];
	    	
	    	for(int i = 0; i < n; i++) 
	    	{
	    		bezierCP[i] = new Point(0,0);
	    	}
	    		
	    	
	    	int sizeofArray = n-2;
	    	
	    	double[][] coeffArray = new double[sizeofArray][sizeofArray];
	    	
	    	int counter = 0;
	    	
	    	for(int r = 0; r < sizeofArray; r++) 
	    	{
	    		if(r == 0) 
	    		{
	    			coeffArray[0][0] = 4;
	    			coeffArray[0][1] = 1;
	    		}
	    		else if(r == sizeofArray-1) 
	    		{
	    			coeffArray[sizeofArray-1][sizeofArray-1] = 4;
	    			coeffArray[sizeofArray-1][sizeofArray-2] = 1;
	    		}
	    		else 
	    		{
	    			coeffArray[r][counter+0] = 1;
	    			coeffArray[r][counter+1] = 4;
	    			coeffArray[r][counter+2] = 1;
	    			counter++;
	    		}
	    	}
	    	
	    	Matrix coeffMatrix = new Matrix(coeffArray);
	    	
	    	double[][] knotXArray = new double[sizeofArray][1];
	    	double[][] knotYArray = new double[sizeofArray][1];
	    	
	    	for(int r = 0; r < sizeofArray; r++) 
	    	{
	    		if(r == 0) 
	    		{
	    			knotXArray[0][0] = (controlPoints.get(1).x*6) - controlPoints.get(0).x;
	    			knotYArray[0][0] = (controlPoints.get(1).y*6) - controlPoints.get(0).y;
	    		}
	    		else if(r == sizeofArray-1) 
	    		{
	    			knotXArray[sizeofArray-1][0] = (controlPoints.get(n-2).x*6) - controlPoints.get(n-1).x;
	    			knotYArray[sizeofArray-1][0] = (controlPoints.get(n-2).y*6) - controlPoints.get(n-1).y;
	    		}
	    		else 
	    		{
	    			knotXArray[r][0] = controlPoints.get(r+1).x*6;
	    			knotYArray[r][0] = controlPoints.get(r+1).y*6;
	    		}
	    	}

	    	Matrix knotXMatrix = new Matrix(knotXArray);
	    	Matrix knotYMatrix = new Matrix(knotYArray);
	    	
	    	Matrix xSolutions;
	    	Matrix ySolutions;
	    	
	    	xSolutions = coeffMatrix.inverse().times(knotXMatrix);
	    	ySolutions = coeffMatrix.inverse().times(knotYMatrix);

	    	for(int i = 0; i < n; i++) 
	    	{
	    		if(i == 0) 
	    		{
	    			bezierControlPoints.add(new ControlPoint(window, controlPoints.get(0).x, controlPoints.get(0).y));
	    			
	    		}
	    		else if(i == n-1) 
	    		{
	    			bezierControlPoints.add(new ControlPoint(window, controlPoints.get(n-1).x, controlPoints.get(n-1).y));
	    		}
	    		else 
	    		{
	    			tx = (float) xSolutions.get(i-1, 0);
	    			ty = (float) ySolutions.get(i-1, 0);
	    			
	    			bezierControlPoints.add(new ControlPoint(window , tx, ty));
	    		}
	    	}
	    	
	    	controlPoints = bezierControlPoints;
    	}
    	
		List<Point> s = new ArrayList<Point>();
		List<Point> tempControlPoints = new ArrayList<Point>();
	    n = controlPoints.size();
	    int p;
	  
	    float sx;
	    float sy;
	    float x = 0;
	    float y = 0;
	      
	    for(int i = 0; i < n; i++)
	    {
	   	  
	    	if(i == 0)
	        {
	    	    sx = controlPoints.get(0).x;
	    	    sy = controlPoints.get(0).y; 
	        }
	        else if(i == n-1)
	        {
	    	    sx = controlPoints.get(i).x;
	    	    sy = controlPoints.get(i).y;
	        }
	        else
	        {
	    	    sx = (controlPoints.get(i-1).x*1/6) + (controlPoints.get(i).x*2/3) + (controlPoints.get(i+1).x*1/6);
	    	    sy = (controlPoints.get(i-1).y*1/6) + (controlPoints.get(i).y*2/3) + (controlPoints.get(i+1).y*1/6);
	        }
	        
	        s.add(new Point(sx,sy));
	    }
	      
	    for(int iB = 1; iB < n; iB++)
	    {
	        
	    	tempControlPoints.clear();
	        
	    	tempControlPoints.add(s.get(iB-1));
	    	tempControlPoints.add(new Point(controlPoints.get(iB-1).x*2/3 + controlPoints.get(iB).x*1/3, controlPoints.get(iB-1).y*2/3 + controlPoints.get(iB).y*1/3));
	    	tempControlPoints.add(new Point(controlPoints.get(iB-1).x*1/3 + controlPoints.get(iB).x*2/3, controlPoints.get(iB-1).y*1/3 + controlPoints.get(iB).y*2/3));
	    	tempControlPoints.add(s.get(iB));
	        
	    	p = tempControlPoints.size()-1;
	        
	    	for(float t = 0; t <= 1; t += interval)
	    	{
	    		x = 0;
	    		y = 0;
	    		for(int i = 0; i <= p; i++)
	    		{
	    			x += combination(p,i)*Math.pow((1-t),(p-i))*Math.pow(t,i)*tempControlPoints.get(i).x;
	    		}
	    		for(int i = 0; i <= p; i++)
	    		{
	    			y += combination(p,i)*Math.pow((1-t),(p-i))*Math.pow(t,i)*tempControlPoints.get(i).y;
	    		}
	    		 
	    		points.add(new Point(x,y)); 
	    	}
	        
	    }
	      
	}
	
	/**
	 * Draws the interpolated b-spline curve
	 */
	public void drawPoints() 
	{
		for(int i = 0; i < points.size(); i++) 
		{
			window.rectMode(2);
			window.point(points.get(i).getX(), points.get(i).getY());
		}
	}
	
	/**
	 * Takes the combination nCr
	 * @param n
	 * @param r
	 * @return the number of combinations
	 */
	float combination(int n, int r)
	{
		float c;
		if(r > 0)
	    {
			if(n > 0)
	        {
				c = (fact(n)/(fact(n-r)*fact(r)));
				return c;
	        }
	    }
	    else
	    {
	    	return 1;
	    }
	    return 0;
	}
	  
	/**
	 * Takes the factorial of a number  
	 * @param number 
	 * @return the factorial
	 */
	float fact(float number)
	{
	 
		if(number == 0)
		{
			return 1;
	    }
	    else
	    {
	    	for(float i = number-1; i >0; i--)
	    	{
	    		number=number*i;
	    	}
	    	
	    	return number;
	    }
	  }

	/**
	 * Returns the spline's list of calculated points
	 * @return points
	 */
	public List<Point> getPoints()
	{
		return points;
	}

	/**
	 * Calculates a list of simplified points.(less points on straights, more on turns)
	 */
	public void calculateSimplifiedPoints()
	{
		simplePoints.clear();
		double previousAngle = 0;
		double angle;
		int numberSinceLastPoint = 0;
		
		//Loops through of b-spline points
		for(int i = 0; i < points.size()-1; i++) 
		{
			//Calculates the angle from the current point to the next and converts it to degrees
			angle = Math.atan((points.get(i+1).getY() - points.get(i).getY())/(points.get(i+1).getX()-points.get(i).getX())) * 180/Math.PI;
		
			//If the angle is over the tolerance and its been a minimum number of points skipped create a new point as the average of the current and the next
			if(Math.abs(previousAngle - angle) > changeInAngleTolerance && numberSinceLastPoint  > minimumSinceLastPoint) 
			{
				simplePoints.add(new Point((points.get(i).getX() + points.get(i+1).getX())/2, (points.get(i).getY() + points.get(i+1).getY())/2));
				numberSinceLastPoint = 0;
			}
			else 
			{
				numberSinceLastPoint++;
			}
			
			previousAngle = angle;
			
			//If if to many points have been within the tolerance defined above create a point
			if(numberSinceLastPoint > maximumSinceLastPoint) 
			{
				simplePoints.add(new Point((points.get(i).getX() + points.get(i+1).getX())/2, (points.get(i).getY() + points.get(i+1).getY())/2));
				numberSinceLastPoint = 0;
			}
		}
		
		if(lastPoint != null) 
		{
			simplePoints.add(new Point(lastPoint.getX(), lastPoint.getY()));
		}
	}
	
	/**
	 * Draws a a spline curve of the simplified points
	 */
	public void drawSimplePoints() 
	{
		for(int i = 0; i < simplePoints.size(); i++) 
		{
			//window.point(simplePoints.get(i).getX(), simplePoints.get(i).getY());
			window.rectMode(2);
			window.rect(simplePoints.get(i).getX(), simplePoints.get(i).getY(), 1, 1);
		}
	}
	
	/**
	 * Gets the list of simplified spline points
	 * @return returns the objects ArrayList of simplePoints
	 */
	public List<Point> getsimplePoints()
	{
		return simplePoints;
	}

	/**
	 * Exports the list of simplified points as a 2D array in a JSON file
	 */
	public void exportPoints() 
	{
		//Creates the main JSON point array,
		JSONArray pointList = new JSONArray();
		
		//populates the JSONArray pointList with JSONArray's each containing the x and y value of a point
		for(int i = 0; i < simplePoints.size(); i++) 
		{
			JSONArray currentPoints = new JSONArray();
			for(int j = 0; j < 2; j++) 
			{
				if(j == 0) 
				{
					currentPoints.add(simplePoints.get(i).getX());
				}
				else 
				{
					currentPoints.add(simplePoints.get(i).getY());
				}
			}
			pointList.add(currentPoints);
		}
		
		export.put("ID", 1); //Puts an ID onto the JSON object
		export.put("points", pointList); //Puts the pointList JSON Array into the JSON object
		
		//Try/Catch to see if the FileWriter has permissions and can write.
		try(FileWriter file = new FileWriter(fileWriterLocation))
		{
			//Writes the JSON object to the file
			file.write(export.toJSONString());
			file.flush();
		}
		catch(IOException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println(export);		
	}
}
