package GameOfLife;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;

public class CanvasDrawer
{
    private GraphicsContext gc;
    Matrix model;
    private int cellSize = 5;
    private int posX;
    private int posY;
    private double windowWidth;
    private double windowHeight;

    public CanvasDrawer(Matrix model, GraphicsContext gc)
    {

	this.model = model;
	this.gc = gc;
	posX = model.getX() * cellSize / 2;
	posY = model.getRealY() * cellSize / 2;
	gc.setStroke(Color.GRAY);
	
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

    public int getpositionX()
    {
	return posX;
    }

    public int getpositionY()
    {
	return posY;
    }

    public void setCellSize(int size)
    {
	this.cellSize = size;
    }

    public int getCellSize()
    {
	return this.cellSize;
    }

    public void setPosX(int x)
    {
	this.posX = x;

    }
    
    public void setStroke(Color c)
    {
	gc.setStroke(c);

    }

    public void setPosY(int y)
    {
	this.posY = y;

    }

    public void clearCanvas()
    {
	gc.setFill(Color.BLACK);
	gc.fillRect(0, 0, windowWidth, windowHeight);
    }

    public void drawCell(int x, int y, Color color)
    {
	gc.setFill(model.getColor());
	gc.setStroke(model.getColor());
	gc.strokeOval(x, y,cellSize, cellSize);
	gc.fillOval(x, y, cellSize, cellSize);
	System.out.println("hey");

	

    }

    public void drawNextGeneration()
    {
	clearCanvas();
	gc.setFill(model.getColor());
	int cellsInLong = 64;

	for (int x = 0; x < model.getX(); x++)
	    for (int j = 0; j < model.getY(); j++)
	    {
		//forsikrer at vi bare tegner i vinduet til programmet
		if(x*cellSize < posX || x*cellSize > posX+windowWidth || cellSize*(j*64 + 64) < posY || cellSize*(j*64)  > posY + windowHeight) 
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
				gc.strokeOval(cellSize * (x) - posX, cellSize * (y) - posY, cellSize, cellSize);
				gc.fillOval(cellSize * (x) - posX, cellSize * (y) - posY, cellSize, cellSize);

			    } else// else dead
			    {
        
			    }
        		 }
	    }
    }

}
