package GameOfLife.Controller;

import GameOfLife.Model.GameBoard;
import GameOfLife.Model.DynamicGameBoard;
import GameOfLife.Model.BitGameBoard;
import javafx.animation.AnimationTimer;

/**
 * <h1>Game of life</h1>
 * The GameOfLife controles the speed of the game, can both change
 * the speed of the game, stop and start the game.
 * 
 * @author Markus Hellestveit
 *
 */
public class ExecutionControl extends AnimationTimer {
	private GameBoard model;
	private CanvasDrawer cd;
	private long a = System.nanoTime();
	private long delay = (long) Math.pow(10, 9);

	/**
	 * Class constructor. Initializes the {@link #model} 
	 * and {@link #cd}.
	 * 
	 * @param model This is an object of {@link BitGameBoard}
	 * @param cd This is an object of {@link CanvasDrawer}
	 */
	public ExecutionControl(GameBoard model, CanvasDrawer cd) {
		this.model = model;
		this.cd = cd;
	}

	@Override
	public void handle(long now) {
		if (now - a > delay) {
			model.nextGenerationConcurrent();
			cd.drawNextGeneration();
			a = now;
		}
	}

	/**
	 * This method sets the delay time for how fast the animation of
	 * game should be.
	 * 
	 * @param delay {@link #delay} This is the value thats decides animation time.
	 */
	public void setDelay(double delay) {
		this.delay = (long) delay;
	}

	/**
	 * Gets the value of delay 
	 * @return delay
	 * 
	 */
	public long getDelay() {
		return this.delay;
	}

	/**
	 * Starts the AnimetionTimer which calls the handle method
	 * 
	 * @see AnimationTimer#start()
	 */
	public void startGame() {
		this.start();
	}
}