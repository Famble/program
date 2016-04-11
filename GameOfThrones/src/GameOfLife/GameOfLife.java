package GameOfLife;

import javafx.animation.AnimationTimer;

/**
 * This Class is the part of the speed handling for canvas 
 * of the game. 
 * <p>
 * <b>Note:</b> This class can change the speed, stop and start
 * the CancasDrawer
 * 
 * @author 
 * @see CanvasDrawer
 *
 */
public class GameOfLife extends AnimationTimer {
	private Matrix model;
	private CanvasDrawer cd;
	private long nowHolder = System.nanoTime();
	private long delay = (long) Math.pow(10, 9);

	public GameOfLife(Matrix model, CanvasDrawer cd) {
		this.model = model;
		this.cd = cd;
	}

	@Override
	public void handle(long now) {
		if (now - nowHolder > delay) {
			model.startNextGeneration();
			cd.drawNextGeneration();
			nowHolder = now;
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