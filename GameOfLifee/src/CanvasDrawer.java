import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CanvasDrawer 
{
	private GraphicsContext gc;
    Matrix model;
    int cellSize = 1;
    int posX = 0;
    int posY = 0;
    
    public CanvasDrawer(Matrix model, GraphicsContext gc)
    {
    	this.model = model;
    	this.gc = gc;
    }
    
    public void setCellSize(int size)
    {
    	this.cellSize = size;
    }
    
    public int getCellSize()
    {
    	return this.cellSize;
    }
    
    public void clearCanvas()
    {
		gc.setFill(Color.BLACK);
    	gc.fillRect(0, 0, 1000, 1000);
    }
    
    public void drawCell(int x, int y, Color color)
    {
    	gc.setFill(color);
    	gc.fillRect(cellSize*x, y*cellSize, cellSize, cellSize);
    }
    
    public void drawNextGeneration(int x, int y)
    {
    	this.posX += x;
    	this.posY += y;
    	
    	clearCanvas();

		for(int i = 0; i < 1000; i++)
			for(int j = 0; j < 125; j++)
			{
				for(int k = 0; k < 8; k++)
				{
					if(((model.getCurrentGeneration()[i][j] >> k)& 1) == 1) //if alive
					{	
						if(i >= this.posX)
						{
							gc.setFill(Color.WHITE);
							gc.fillRect(i*cellSize-this.posX*cellSize, ((j*8) + k)*cellSize-this.posY*cellSize, cellSize, cellSize);
						}
							
					}
					else//else dead
					{
						
					}
				}
			}	
    }
    
    public void drawNextGeneration()
	{
    	
    	clearCanvas();

		for(int i = 0; i < 1000; i++)
			for(int j = 0; j < 125; j++)
			{
				for(int k = 0; k < 8; k++)
				{
					if(((model.getCurrentGeneration()[i][j] >> k)& 1) == 1) //if alive
					{	
						gc.setFill(Color.WHITE);
						gc.fillRect(i*cellSize, ((j*8) + k)*cellSize, cellSize, cellSize);	
					}
					else//else dead
					{
						
					}
				}
			}	
			
	}
    
    
    
    
	
}
