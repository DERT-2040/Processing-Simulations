package pathing;
import processing.core.PApplet;
import java.util.ArrayList;
import java.util.List;
import Jama.Matrix;

public class Curve 
{
	PApplet parent;
	
	List<ControlPoint> controlPoints = new ArrayList<ControlPoint>(); 
	List<Point> points = new ArrayList<Point>();
	
	double interval = 0.01;
	
	boolean drawInterpolatedControlPoints = false;
	
	public Curve(PApplet p, List<ControlPoint> cp, boolean drawPoints)
	{
		parent = p;
		controlPoints = cp;
		drawInterpolatedControlPoints = drawPoints;
	}
	
	public void updateControlPoints(List<ControlPoint> cp) 
	{
		controlPoints = cp;
	}
	
	public void drawLine()
	{
		for(int i = 0; i < points.size()-1; i++)
		{
			parent.line(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y);
		}
	}
	
	public void interpolate(int splineType, int degree)
	{
	    float x = 0;
	    float y = 0;
	    
	    points.clear();
	    
	    List<ControlPoint> section = new ArrayList<ControlPoint>();
	    int numberPerSection = degree+1;
	    int sectionNum = 0;
	    int numberofSections = (controlPoints.size()-1)/degree;
	    

	    if(splineType == 0) //Piecewise Bezier Curve
	    {
	    
	      for(int i = 1; i <= numberofSections; i++)
	      {
	        
	        //clearing section list, of the points from the previous interval
	        section.clear();
	        
	        //adding new the new intervals points into the section list
	        for(int j = 0; j < numberPerSection; j++)
	        {
	        	
	            //-1 is multiplied by the current sectionNum
	            section.add(controlPoints.get(j+sectionNum*numberPerSection-1*sectionNum));
	            
	        }
	        
	        int n = section.size()-1;
	        
	        for(double t = 0; t <= 1; t += interval)
	        {
	        	
	          x = 0;
	          y = 0;
	          
	          for(int k = 0; k <= n; k++)
	          {
	            x += combination(n,k)*Math.pow((1-t),(n-k))*Math.pow(t,k)*section.get(k).getX();
	          }
	          
	          for(int k = 0; k <= n; k++)
	          {
	            y += combination(n,k)*Math.pow((1-t),(n-k))*Math.pow(t,k)*section.get(k).getY();
	          }
	          
	          points.add(new Point(x,y));
	          
	        }
	          sectionNum++;
	      }
	      
	    }
	    else if(splineType == 1) //Regular Bezier Curve
	    { 
	    
	      int n = controlPoints.size()-1;
	     
	      for(float t = 0; t <= 1; t += interval)
	      {
	        x = 0;
	        y = 0;
	        
	        for(int i = 0; i <= n; i++)
	        {
	          x += combination(n,i)*Math.pow((1-t),(n-i))*Math.pow(t,i)*controlPoints.get(i).getX();
	        }
	        
	        for(int i = 0; i <= n; i++)
	        {
	          y += combination(n,i)*Math.pow((1-t),(n-i))*Math.pow(t,i)*controlPoints.get(i).getY();
	        }
	        
	        points.add(new Point(x,y));
	      }
	      
	    }
	    else if(splineType == 2) //B-Spline Curve
	    { 
	    	bSpline();
	    }
	    else if(splineType == 3) //Interpolation B-spline
	    {
	    	
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
		    			bezierControlPoints.add(new ControlPoint(controlPoints.get(0).x, controlPoints.get(0).y, 0, parent));
		    			
		    		}
		    		else if(i == n-1) 
		    		{
		    			bezierControlPoints.add(new ControlPoint(controlPoints.get(n-1).x, controlPoints.get(n-1).y, i, parent));
		    		}
		    		else 
		    		{
		    			tx = (float) xSolutions.get(i-1, 0);
		    			ty = (float) ySolutions.get(i-1, 0);
		    			
		    			bezierControlPoints.add(new ControlPoint(tx , ty, i*10, parent));
		    		}
		    	}
		    	
		    	controlPoints = bezierControlPoints;
		    	
		    	if(drawInterpolatedControlPoints == true) 
		    	{
		    		for(int i = 0; i < controlPoints.size()-1; i++) 
		    		{
		    			controlPoints.get(i).drawControlPoint();
		    		}
		    	}
		    	
		    	bSpline();
	    	}
	    }
	    
	  }
	
	
	public void bSpline() 
	{
		 
		 List<Point> s = new ArrayList<Point>();
		 List<Point> tempControlPoints = new ArrayList<Point>();
	      
	     int n = controlPoints.size();
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
	      
	     for(int i = 0 ; i < s.size(); i++)
	     {
	    	 parent.fill(255,0,0);
	    	 parent.rect(s.get(i).x, s.get(i).y, 5, 5);
	     }	     
	}
	
	public List<Point> getPoints()
	{
		return points;
	}
	
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
	  
	  //factorial
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
	
}
