package GameOfLife;
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
    }

    @Override
    public void handle(long now)
    {
	if (now - a > delay)
	{
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
	int cellSize = cd.getCellSize();

	System.out.println("direction" + zoom);
	
	int middleOfScreenX = (int)cd.getWindowWidth()/2;
	int middleOfScreenY = (int)cd.getWindowHeight()/2;
	
	int x = ((middleOfScreenX + cd.getpositionX())/cellSize);
	int y = ((middleOfScreenY + cd.getpositionY()) / cellSize);
	
	cd.setPosX(x * (cd.getCellSize() + zoom) -middleOfScreenX);
	cd.setPosY(y * (cd.getCellSize() + zoom)  -middleOfScreenY);
	
	cd.setCellSize(cd.getCellSize()+ zoom);
	
	cd.drawNextGeneration();
	

    }

    public void zoom(int zoom, ZoomEvent event)
    {
	if ((cd.getCellSize() + zoom) > 0 && (cd.getCellSize() + zoom) <= 35)
	{
	    int cellSize = cd.getCellSize();

	    cd.setCellSize(cd.getCellSize() + zoom);

	    int x = (int) event.getX();
	    int y = (int) event.getY();

	    int xDivCell = (x + cd.getpositionX()) / cellSize;
	    int yDivCell = (y + cd.getpositionY()) / cellSize;

	    cd.setPosX(xDivCell * (cellSize + zoom) - x +  (x + cd.getpositionX()) % cellSize);
	    cd.setPosY(yDivCell * (cellSize + zoom) - y + (y + cd.getpositionY()) % cellSize);

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

	int x = (int) event.getX();
	int y = (int) event.getY();
	

	int cellSize = cd.getCellSize();
	int posX = cd.getpositionX();
	int posY = cd.getpositionY();

	int xDivCell = (x + posX) / cellSize;
	int yDivCell = (y + posY) / cellSize;

	System.out.printf("(x,y): (%d,%d)\n", x, y);

	if(! (xDivCell > model.getX() || yDivCell > model.getRealY()))
	{
	    (model.getCurrentGeneration()[xDivCell][yDivCell / 64]) ^= (1L << yDivCell % 64); 
	
	
	
	model.getActiveCells()[xDivCell][yDivCell / 64] ^= (1L << yDivCell % 64);

	if (((model.getCurrentGeneration()[xDivCell][yDivCell / 64] >> yDivCell % 64) & 1) == 1) // if									       // alive
	{
	    cd.drawCell(cellSize * (xDivCell) - posX, cellSize * (yDivCell) - posY, model.getColor());
	    model.setAliveCells(model.getAliveCells() + 1);
	}
	else
	{
	    cd.drawCell(cellSize * (xDivCell) - posX, cellSize * (yDivCell) - posY, Color.BLACK);
	    model.setAliveCells(model.getAliveCells() - 1);

	}
	}
	
	  
    }

    public void resetGame()
    {
	for (int i = 0; i < model.getX(); i++)
	    for (int j = 0; j < model.getRealY(); j++)
	    {
		model.getCurrentGeneration()[i][j] = 0;
		model.getNewGeneration()[i][j] = 0;
	    }

	cd.drawNextGeneration();
    }

}
