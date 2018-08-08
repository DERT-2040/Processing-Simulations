package pathing;
import processing.core.PApplet;

public class ControlPoint 
{
	  float x;
	  float y;
	  float pWidth = 15;
	  float pHeight = 15;
	  
	  int controlPointNumber;
	  
	  PApplet parent;
	  
	  public ControlPoint(float xPos, float yPos, int num, PApplet p)
	  {
	    x = xPos;
	    y = yPos;
	    controlPointNumber = num;
	    parent = p;
	  }
	  
	  public void drawControlPoint()
	  {
	    parent.fill(255);
	    parent.rect(x, y, pWidth, pHeight);
	    parent.fill(0);
	    parent.textSize(15);
	    parent.text(Integer.toString(controlPointNumber), x, y);
	  }
	  
	  public void clicked(){
	    x = parent.mouseX;
	    y = parent.mouseY;
	    drawControlPoint();
	  }
	  
	  public float getX(){
	    return x;
	  }
	  
	  public float getY(){
	    return y;
	  }
	  
	  public void setX(float xPosition) 
	  {
		  x = xPosition;
	  }
	  
	  public void setY(float yPosition) 
	  {
		  y = yPosition;
	  }
}
