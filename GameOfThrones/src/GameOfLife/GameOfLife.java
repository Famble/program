package GameOfLife;

import GameOfLife.Controller.CanvasDrawer;
import GameOfLife.Model.StaticMatrix;
import javafx.animation.AnimationTimer;

/**
 * <h1>Game of life</h1>
 * The GameOfLife controles the speed of the game, can both change
 * the speed of the game, stop and start the game.
 * 
 * @author Markus Bikilla Seyoum Hellestveit
 *
 */
public class GameOfLife extends AnimationTimer {
	private StaticMatrix model;
	private CanvasDrawer cd;
	private long a = System.nanoTime();
	private long delay = (long) Math.pow(10, 9);

	/**
	 * Class constructor. Initializes the {@link #model} 
	 * and {@link #cd}.
	 * 
	 * @param model This is an object of {@link StaticMatrix}
	 * @param cd This is an object of {@link CanvasDrawer}
	 */
	public GameOfLife(StaticMatrix model, CanvasDrawer cd) {
		this.model = model;
		this.cd = cd;
	}

	@Override
	public void handle(long now) {
		if (now - a > delay) {
			model.startNextGeneration();
			cd.drawNextGeneration();
			a = now;
		}
	}

	/**
	 * This method sets the delay time for how fast the animation of
	 * game should be.
	 * 
	 * @param delay This is the value thats decides animation time.
	 */
	public void setDelay(double delay) {
		this.delay = (long) delay;
	}

	/**
	 * Gets the value of delay 
	 * @return {@link #delay}
	 * 
	 */
	public long getDelay() {
		return this.delay;
	}

	/**
	 * Starts the AnimetionTimer which calls the handle method
	 * 
	 * @see {@link AnimationTimer#start()}
	 */
	public void startGame() {
		this.start();
	}
}