package GameOfLife.Controller;

import GameOfLife.Model.GameBoard;
import GameOfLife.Model.BitGameBoard;
import javafx.animation.AnimationTimer;

/**
 * <h1>Execution Control</h1>
 * The ExecutionControl controls the speed of the game, can both change
 * the speed of the game, stop and start the game.
 * 
 * @author
 *
 */
public class ExecutionControl extends AnimationTimer {
	/**
	 * An instant of the game board.
	 * @see GameBoard
	 */
	private GameBoard model;

	/**
	 *  An instant of a Canvas Drawer
	 *  @see CanvasDrawer
	 */
	private CanvasDrawer cd;

	/**
	 * Timestamp manages the time of the game.
	 */
	private long timeStamp = 0;

	/**
	 * Decides the time of each iteration, of the game.
	 */
	private long delay = (long) Math.pow(10, 9);

	/**
	 * Class constructor. Initializes the {@link #model} 
	 * and {@link #cd}.
	 * 
	 * @param model This is an object of {@link BitGameBoard BitGameBoard}
	 * @param cd This is an object of {@link CanvasDrawer CanvasDrawer}
	 */
	public ExecutionControl(GameBoard model, CanvasDrawer cd) {
		this.model = model;
		this.cd = cd;
	}


	/**
	 *Handle is called very time
	 *
	 * @param now the time of current time.
     */
	@Override
	public void handle(long now) {
		if (now - timeStamp > delay) {
			model.nextGenerationConcurrent();
			cd.drawNextGeneration();
			timeStamp = now;
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