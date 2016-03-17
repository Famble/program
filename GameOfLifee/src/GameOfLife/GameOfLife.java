package GameOfLife;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.animation.AnimationTimer;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.paint.Color;

public class GameOfLife extends AnimationTimer
{
    private Matrix model;
    private CanvasDrawer cd;
    private long a = System.nanoTime();
    private long delay = (long) Math.pow(10, 9);

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

 
    

    

}