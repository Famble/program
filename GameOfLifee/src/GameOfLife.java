import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.animation.AnimationTimer;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class GameOfLife extends AnimationTimer
{
	private Matrix model;
	private CanvasDrawer cd;
	private long a = System.nanoTime();
	private long delay = 999999999;

	
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
		cd.setCellSize(cd.getCellSize() + zoom);
		cd.drawNextGeneration();
	}
	
	public void loadFile()
	{
		
	}
	
	public void setDelay(double delay)
	{
		this.delay = (long) delay;
	}
	
	public void startGame()
	{
		model.startNextGeneration();
		cd.drawNextGeneration();
	}
	
	public void pauseGame()
	{
		this.stop();
		
	}
	
	public void movePosition(int x, int y)
	{
		
		cd.drawNextGeneration(x*10, y*10);
	}
	
	public void selectCell(MouseEvent event)
	{
		int x = (int)event.getX();
		int y = (int)event.getY();
		
		int cellSize = cd.getCellSize();
		
		int x2 = x/cellSize;
		int y2 = y/cellSize;
		
		System.out.printf("x2, y2, z2: %d, %d, %d", x2, y2, y2%8);
		
		model.getCurrentGeneration()[x2][y2/8] ^= (1 << y2%8); //XOR på biten cellen representerer
	
		if(	((model.getCurrentGeneration()[x2][y2/8] >> y2%8) & 1) == 1) //if alive
			cd.drawCell(x2, y2, Color.WHITE);
		else
			cd.drawCell(x2, y2, Color.BLACK);
	}
	
	public void resetGame()
	{
		for(int i = 0; i < 1000; i++)
		{
			for(int j = 0; j < 125; j++)
			{
				for (int k = 0; k < 8; k++)
				{
					if(((model.getCurrentGeneration()[i][j] >> k) & 1) == 1)
					{
						System.out.printf("\ni, j, k %d, %d, %d", i, j, k);
					}
				}
			}
		}
		
	}
	
	
	
}
