import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CanvasDrawer 
{
	private GraphicsContext gc;
    Matrix model;
    private int cellSize = 1;
    private int posX = 0;
    private int posY = 0;
    
    public CanvasDrawer(Matrix model, GraphicsContext gc)
    {
    	this.model = model;
    	this.gc = gc;
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
    
    public void setPosY(int y)
    {
    	this.posY = y;

    }
    
    
    public void clearCanvas()
    {
    	gc.setFill(Color.BLACK);
    	gc.fillRect(0, 0, model.getX(), model.getY()*64l);
    }
    
    public void drawCell(int x, int y, Color color)
    {	
    	gc.setFill(color);
    	gc.fillRect(x, y, cellSize, cellSize);
    }
    
    public void drawNextGeneration()
    {
    	clearCanvas();
    	gc.setFill(model.getColor());

		for(int x = 0; x < model.getX(); x++)
			for(int j = 0; j < model.getY(); j++)
			{
			    if(!(model.getCurrentGeneration()[x][j] == 0))
				for(int k = 0; k < 64; k++)
				{
					if(((model.getCurrentGeneration()[x][j] >> k)& 1L) == 1) //if alive
					{		
						//6 bitshift til vestre tilsvarer å gang med 64, men er raskere for CPU
						long y = (j << 6) + k;
						gc.fillRect(cellSize*(x)-posX, cellSize*(y)-posY, cellSize, cellSize);		
					}
					else//else dead
					{
						
					}
				}
			}	
    }
    
    
    
    
	
}
