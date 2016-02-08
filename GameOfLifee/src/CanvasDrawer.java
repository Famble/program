import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CanvasDrawer 
{
	private GraphicsContext gc;
    Matrix model;
    
    public CanvasDrawer(Matrix model, GraphicsContext gc)
    {
    	this.model = model;
    	this.gc = gc;
    }
    
    public void clearCanvas(int canvasWidth, int canvasHeight, int upperLeftX, int upperLeftY )
    {
		gc.setFill(Color.BLACK);
    	gc.fillRect(upperLeftX, upperLeftY, canvasWidth, canvasHeight);
    }
    
    public void drawCell(int x, int y, Color color)
    {
    	gc.setFill(color);
    	gc.fillRect(x, y, 10, 10);
    }
    
    public void drawNextGeneration()
	{

		for(int i = 0; i < 80; i++)
			for(int j = 0; j < 50; j++)
			{
				int x = i*10;
				int y = j*10;
				
				Cell cell = model.getCurrentGeneration()[i][j];
				
				gc.setFill(Color.BLACK);
				
				if(cell.getAlive())
				{
					gc.setFill(cell.getColor());
					gc.fillRect(x, y, 10, 10);
					gc.setFill(Color.BLACK);
				}
				else
				{
					gc.fillRect(x, y, 10, 10);	
				}		
			}
	}
    
    
    
    
	
}
