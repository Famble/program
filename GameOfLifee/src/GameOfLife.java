import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.animation.AnimationTimer;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class GameOfLife extends AnimationTimer
{
	private Matrix model;
	private CanvasDrawer canvasDrawer;
	private long a = System.nanoTime();
	private long delay = 999999999;

	
	public GameOfLife(Matrix model, CanvasDrawer canvasDrawer)
	{
		this.model = model;
		this.canvasDrawer = canvasDrawer;
		
		for(int i = 0; i < 80; i++)
    	{
    		for (int j = 0; j < 50; j++)
    		{
    			model.getCurrentGeneration()[i][j] = new Cell(Color.WHITE);
    			model.getNewGeneration()[i][j] = new Cell(Color.WHITE);
    		}
    	}
		
		canvasDrawer.clearCanvas(800, 500, 0, 0);
	}
	

	@Override
	public void handle(long now) 
	{
		if(now - a > delay) {
			model.startNextGeneration();
			canvasDrawer.drawNextGeneration();
			a = now;
		}
		
	}
	
	public void saveGame() throws FileNotFoundException
	{
		
	}
	
	public void loadGame()
	{
	
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
		this.start();
	}
	
	public void pauseGame()
	{
		this.stop();
	}
	
	public void selectCell(MouseEvent event)
	{
		int x = (int)event.getX();
		int y = (int)event.getY();
		x = x - x % 10;
		y = y - y % 10; 
				
		Cell selectedCell = model.getCurrentGeneration()[x/10][y/10];
				
		selectedCell.setAlive(!selectedCell.getAlive()); //if cell is alive --> kill, if cell is dead -->ressurect
		
		if(selectedCell.getAlive())
			canvasDrawer.drawCell(x, y, selectedCell.getColor());
		else
			canvasDrawer.drawCell(x, y, Color.BLACK);
		
	}
	
	public void resetGame()
	{
		for(int i = 0; i < 80; i++)
    	{
    		for (int j = 0; j < 50; j++)
    		{
    			model.getCurrentGeneration()[i][j].setAlive(false);
    			model.getNewGeneration()[i][j].setAlive(false);
    		}
    	}
		
		canvasDrawer.clearCanvas(800, 500, 0, 0);
	}
	
	
	
}
