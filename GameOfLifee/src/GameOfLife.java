	import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.animation.AnimationTimer;
import javafx.scene.input.MouseEvent;
    import javafx.scene.input.ZoomEvent;
    import javafx.scene.paint.Color;

public class GameOfLife extends AnimationTimer
{
	private Matrix model;
	private CanvasDrawer cd;
	private long a = System.nanoTime();
	private long delay = (long) Math.pow(10, 9);
	int counter = 0;
	
	
	public GameOfLife(Matrix model, CanvasDrawer cd)
	{
		this.model = model;
		this.cd = cd;	
		cd.clearCanvas();
	}
	

	@Override
	public void handle(long now) 
	{
		if(now - a > delay) {
			model.startNextGeneration();
			cd.drawNextGeneration();
			a = now;
		}
		
	}
	
	public void saveGame() throws FileNotFoundException
	{
		
	}
	
	public void loadGame()
	{
	
	}
	
	public void zoom(int zoom)
	{
			cd.setCellSize(zoom);
			cd.setPosX(0);
			cd.setPosY(0);
			cd.drawNextGeneration();	
	}

    public void zoom(int zoom, ZoomEvent event)
    {
        if((cd.getCellSize() + zoom) > 0)
        {
            int cellSize = cd.getCellSize();

            cd.setCellSize(cd.getCellSize() + zoom);

            int x = (int) event.getX();
            int y = (int) event.getY();


            int x2 = (x+cd.getpositionX())/cellSize;
            int y2 = (y+cd.getpositionY())/cellSize;

            cd.setPosX(x2*(cellSize+zoom) - x);
            cd.setPosY(y2*(cellSize+zoom) - y);

            cd.drawNextGeneration();
        }

    }


    public CanvasDrawer getCanvasDrawer()
	{
		return this.cd;
	}
	
	public void setDelay(double delay)
	{
		this.delay = (long) delay;
	}
	
	public long getDelay()
	{
		return this.delay;
	}
	
	public void startGame()
	{
		this.start();
	}
	
	public void pauseGame()
	{
		this.stop();
		
	}
	
	public void movePosition(int x, int y)
	{
		cd.setPosX(cd.getpositionX() + x);
		cd.setPosY(cd.getpositionY() + y);
		cd.drawNextGeneration();
	}
	
	public void selectCell(MouseEvent event)
	{

		int x = (int)event.getX();
		int y = (int)event.getY();
		
		int cellSize = cd.getCellSize();
		int posX = cd.getpositionX();
		int posY = cd.getpositionY();

		
		int x2 = (x+posX)/cellSize;
		int y2 = (y+posY)/cellSize;

								
		(model.getCurrentGeneration()[x2][y2/64]) ^= (1L << y2%64); //XOR på biten cellen representerer
		model.getInactiveCells()[x2][y2/64] ^= (1L << y2%64);
	
		if(((model.getCurrentGeneration()[x2][y2/64] >> y2%64) & 1) == 1) //if alive
			cd.drawCell(cellSize*(x2)-posX, cellSize*(y2)-posY, model.getColor());
		else
			cd.drawCell(cellSize*(x2)-posX, cellSize*(y2)-posY, Color.BLACK);
	}
	
	
	public void resetGame()
	{
		for(int i = 0; i < model.getX(); i++)
			for(int j = 0; j < model.getY(); j++)
			{
				model.getCurrentGeneration()[i][j] = 0;
				model.getNewGeneration()[i][j] = 0;
			}
		
		cd.drawNextGeneration();
	}
	
	
	
}
