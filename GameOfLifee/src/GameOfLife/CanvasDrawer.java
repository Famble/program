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
	canvasDisplacedX = model.getX() * cellSize / 2;
	canvasDisplacedY = model.getRealY() * cellSize / 2;
	gc.setStroke(Color.GRAY);
	
    }
    
    /*
     * Receives the position (x,y) of the click relative to the top left corner of the canvas
     * We add the displacement of the canvas to the parameters to get the position relative to the
     * entire canvas(top left corner).
     * We divide by the cell size to get the indices of the bit that represent the cell clicked
     * 
     */
    
    public void drawCell(int x, int y)
    {
    	int cellSize = this.getCellSize();
    	
    	int canvasDisplacedX = this.getCanvasDisplacedX();
    	int canvasDisplacedY = this.getCanvasDisplacedY();
    	x = (x + canvasDisplacedX) / cellSize; 
    	y = (y + canvasDisplacedY) / cellSize;
    	int cellsInLong = 64;
    	
    	int yDiv64 = y/64;
    	int bitPos = y%64;
    	
    	try
    	{
    	
	    	if(y > model.getRealY())
	    		throw new ArrayIndexOutOfBoundsException();
	  
	    	
	    	model.getCurrentGeneration()[x][yDiv64] ^= (1L << bitPos);
	    	model.getActiveCells()[x][yDiv64] ^= (1L << bitPos);
	    		
	    	if (((model.getCurrentGeneration()[x][yDiv64] >> bitPos) & 1) == 1)
	    	{
	    		gc.setFill(model.getColor());
	    		gc.fillOval(cellSize * (x) - canvasDisplacedX, cellSize * (y) - canvasDisplacedY, cellSize, cellSize);
	    	} else
	    	{
	    		gc.setFill(Color.BLACK);
	    		gc.fillOval(cellSize * (x) - canvasDisplacedX, cellSize * (y) - canvasDisplacedY, cellSize, cellSize);
	    	}
	    	
    	}catch(ArrayIndexOutOfBoundsException e)
    	{
    		
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
	gc.setStroke(Color.WHITE);
	int cellsInLong = 64;

	for (int x = 0; x < model.getX(); x++)
	    for (int j = 0; j < model.getY(); j++)
	    {
		//forsikrer at vi bare tegner i vinduet til programmet
		if(x*cellSize < canvasDisplacedX || x*cellSize > canvasDisplacedX+windowWidth
				|| cellSize*(j*64 + 64) < canvasDisplacedY || cellSize*(j*64)  > canvasDisplacedY + windowHeight) 
		{
		    //hvis cellen er utenform skjermen
		}
		else
		    if (!(model.getCurrentGeneration()[x][j] == 0))
			for (int k = 0; k < cellsInLong; k++)
			{
			    if (((model.getCurrentGeneration()[x][j] >> k) & 1L) == 1) // if										   // alive
			    {
				// 6 bitshift til vestre tilsvarer å gang med 64,
				// men er raskere for CPU
				long y = (j << 6) + k;
				gc.fillOval(cellSize * (x) - canvasDisplacedX, cellSize * (y) - canvasDisplacedY, cellSize, cellSize);

			    } else// else dead
			    {
        
			    }
        		 }
	    }
    }

}
