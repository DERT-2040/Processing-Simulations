package pathing;
import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;
import field.Obstacle;

public class NodeMap 
{
	
	Node[][] nodeMap;
	
	int numberofColumns;
	int numberofRows;
	
	float screenWidth;
	float screenHeight;
	float xNodeInterval;
	float yNodeInterval;
	float sensitivity = 5;
	
	PApplet parent;
	
	List<Obstacle> obstacles = new ArrayList<Obstacle>();
	
	public NodeMap(int columns, int rows, int width, int height, PApplet p, List<Obstacle> o)
	{
		numberofColumns = columns;
		numberofRows = rows;
		
		parent = p;
		
		screenWidth = width;
		screenHeight = height;
		
		nodeMap = new Node[numberofRows][numberofColumns];
		
		obstacles = o;
		
		generateMap();
	}
	
	public void generateMap() 
	{
		int xCounter = 0;
		int yCounter = 0;
		
		float obstacleX;
		float obstacleY;
		float obstacleX2;
		float obstacleY2;
		
		float nodeX;
		float nodeY;
		
		xNodeInterval = (screenWidth + screenWidth/numberofColumns)/numberofColumns;
		yNodeInterval = (screenHeight + screenHeight/numberofRows)/numberofRows;
		
		for(int r = 0; r < numberofRows; r++) 
		{
			for(int c = 0; c < numberofColumns; c++) 
			{
				nodeMap[r][c] = new Node(xNodeInterval * xCounter, yNodeInterval * yCounter, parent);
				
				for(int i = 0; i < obstacles.size(); i++) 
				{
					obstacleX = obstacles.get(i).getX();
					obstacleY = obstacles.get(i).getY();
					obstacleX2 = obstacleX + obstacles.get(i).getWidth();
					obstacleY2 = obstacleY + obstacles.get(i).getHeight();
					
					nodeX = nodeMap[r][c].getX();
					nodeY = nodeMap[r][c].getY();
					
					if((nodeX > (obstacleX-sensitivity)) && (nodeX < (obstacleX2+sensitivity)) && (nodeY > (obstacleY-sensitivity)) && (nodeY < (obstacleY2+sensitivity))) 
					{
						nodeMap[r][c].disabled();
					}
				}
				
				xCounter++;
			}
			xCounter = 0;
			yCounter++;
		}
		
	}
	
	public void drawMap() 
	{
		for(int r = 0; r < numberofRows; r++) 
		{
			for(int c = 0; c < numberofColumns; c++) 
			{
				if(nodeMap[r][c].getNodeState() == false) 
				{
					nodeMap[r][c].drawNode();
				}
			}
		}
	}
}
