package GameOfLife;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;

public class CanvasDrawer
{
    private GraphicsContext gc;
    Matrix model;
    private int cellSize = 5;
    private int canvasDisplacedX;
    private int canvasDisplacedY;
    private double windowWidth;
    private double windowHeight;

    public CanvasDrawer(Matrix model, GraphicsContext gc)
    {

	this.model = model;
	this.gc = gc;
	canvasDisplacedX = 0; //model.getX() * cellSize / 2;
	canvasDisplacedY = 0; //model.getRealY() * cellSize / 2;
	gc.setStroke(Color.GRAY);
	
    }
    
    public void drawPatternBackground(int width, int height)
    {
    	gc.setFill(Color.GRAY);
    	gc.fillRect(50, 50, width, height);
    }
    
   
    
    public void drawCell(int x, int y)
    {
    	int cellSize = this.getCellSize();
    	int canvasDisplacedX = this.getCanvasDisplacedX();
    	int canvasDisplacedY = this.getCanvasDisplacedY();
    	
    	x = (x + canvasDisplacedX) / cellSize; 
    	y = (y + canvasDisplacedY) / cellSize;
    	    	
	    model.swapCellState(x, y, model.getCurrentGeneration());
	    model.setCellState(x, y, model.getActiveCells(), true);
	    		
	    if (model.cellIsAlive(x, y, model.getCurrentGeneration()))
	    {
	    	gc.setFill(model.getColor());
	    	gc.fillOval(cellSize * (x) - canvasDisplacedX, cellSize * (y) - canvasDisplacedY, cellSize, cellSize);
	    } 
	    else
	    {
	    	gc.setFill(Color.BLACK);
	    	gc.fillOval(cellSize * (x) - canvasDisplacedX, cellSize * (y) - canvasDisplacedY, cellSize, cellSize);	
	    }
    }
    
    public void drawCell(int x, int y, boolean dragDraw)
    {
    	int cellSize = this.getCellSize();
    	
    	int canvasDisplacedX = this.getCanvasDisplacedX();
    	int canvasDisplacedY = this.getCanvasDisplacedY();
    	
    	x = (x + canvasDisplacedX) / cellSize; 
    	y = (y + canvasDisplacedY) / cellSize;
    		  	    	
	    if(dragDraw)
	    {
		    if(!model.cellIsAlive(x, y, model.getCurrentGeneration()))
		    {
			    gc.setFill(model.getColor());
			    model.setCellState(x, y, model.getCurrentGeneration(), true);
			    model.setCellState(x, y, model.getActiveCells(), true);
			    gc.fillOval(cellSize * (x) - canvasDisplacedX, cellSize * (y) - canvasDisplacedY, cellSize, cellSize);
		    }
		    	
		    	
	    }
	    else
	    {
	    		
		    if(model.cellIsAlive(x, y, model.getCurrentGeneration()))
		    {
		    	gc.setFill(Color.BLACK);
		    	model.setCellState(x, y, model.getCurrentGeneration(), false);
		    	model.setCellState(x, y, model.getActiveCells(), true);
		    	gc.fillOval(cellSize * (x) - canvasDisplacedX, cellSize * (y) - canvasDisplacedY, cellSize, cellSize);
		    }	
	    } 	 	
    }
    
    

    
    public void zoom(int zoom)
    {
    	int cellSize = this.getCellSize();

		int middleOfScreenX = (int) this.getWindowWidth() / 2;
		int middleOfScreenY = (int) this.getWindowHeight() / 2;
	
		int x = ((middleOfScreenX + this.getCanvasDisplacedX()) / cellSize);
		int y = ((middleOfScreenY + this.getCanvasDisplacedY()) / cellSize);
	
		this.setCanvasDisplacedX(x * (this.getCellSize() + zoom) - middleOfScreenX);
		this.setCanvasDisplacedY(y * (this.getCellSize() + zoom) - middleOfScreenY);
	
		this.setCellSize(this.getCellSize() + zoom);
	
		this.drawNextGeneration();

    }

    public void clearCanvas()
    {
		gc.setFill(Color.BLACK);
		gc.fillRect(0,0, windowWidth, windowHeight);
		gc.setStroke(Color.BLUE);
    }

    public void movePosition(int x, int y)
    {
		this.setCanvasDisplacedX(this.getCanvasDisplacedX() + x);
		this.setCanvasDisplacedY(this.getCanvasDisplacedY() + y);
		this.drawNextGeneration();
    }
    
    public void zoom(int zoom, ScrollEvent event)
    {
	if ((this.getCellSize() + zoom) > 0 && (this.getCellSize() + zoom) <= 35)
	{
	    int cellSize = this.getCellSize();

	    this.setCellSize(this.getCellSize() + zoom);

	    int x = (int) event.getX();
	    int y = (int) event.getY();

	    int xDivCell = (x + this.getCanvasDisplacedX()) / cellSize;
	    int yDivCell = (y + this.getCanvasDisplacedY()) / cellSize;

	    this.setCanvasDisplacedX(xDivCell * (cellSize + zoom) - x + (x + this.getCanvasDisplacedX()) % cellSize);
	    this.setCanvasDisplacedY(yDivCell * (cellSize + zoom) - y + (y + this.getCanvasDisplacedY()) % cellSize);

	    this.drawNextGeneration();
	}

    }

    public void drawNextGeneration()
    {
	clearCanvas();
	gc.setFill(model.getColor());
	for (int x = 0; x < model.getWidth(); x++)
	    for (int y = 0; y < model.getHeight(); y++)
	    {
		if(x*cellSize < canvasDisplacedX || x*cellSize > canvasDisplacedX+windowWidth
				|| cellSize*(y + 64) < canvasDisplacedY || cellSize*(y)  > canvasDisplacedY + windowHeight) 
		{	
		
		}
		else
		if (!(model.getCurrentGeneration()[x][y/64] == 0))
			if (model.cellIsAlive(x, y, model.getCurrentGeneration()))
			{
				gc.fillOval(cellSize * (x) - canvasDisplacedX, cellSize * (y) - canvasDisplacedY, cellSize, cellSize);
			}
        		 
	    }
    }
    
    public double getWindowWidth()
    {
        return windowWidth;
    }

    public void setWindowWidth(double windowWidth)
    {
        this.windowWidth = windowWidth;
    }

    public double getWindowHeight()
    {
        return windowHeight;
    }

    public void setWindowHeight(double windowHeight)
    {
        this.windowHeight = windowHeight;
    }

    public int getCanvasDisplacedX()
    {
    	return canvasDisplacedX;
    }

    public int getCanvasDisplacedY()
    {
    	return canvasDisplacedY;
    }

    public void setCellSize(int size)
    {
	this.cellSize = size;
    }

    public int getCellSize()
    {
	return this.cellSize;
    }

    public void setCanvasDisplacedX(int x)
    {
    	this.canvasDisplacedX = x;
    }
    

    public void setCanvasDisplacedY(int y)
    {
    	this.canvasDisplacedY = y;
    }
    

}
