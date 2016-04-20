package GameOfLife.Controller;

import GameOfLife.Model.Matrix;
import GameOfLife.Model.DynamicMatrix;
import GameOfLife.Model.StaticMatrix;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

public class CanvasDrawer {
	private GraphicsContext gc;
	private Matrix model;
	private int cellSize = 5;
	private int canvasDisplacedX;
	private int canvasDisplacedY;
	private double windowWidth;
	private double windowHeight;
	private int patternPositionX;
	private int patternPositionY;

	public CanvasDrawer(Matrix model, GraphicsContext gc) {
		this.model = model;
		this.gc = gc;
		canvasDisplacedX = 0; // model.getX() * cellSize / 2;
		canvasDisplacedY = 0; // model.getRealY() * cellSize / 2;
		gc.setStroke(Color.GRAY);

		if (model instanceof StaticMatrix) // if static gameboard then we draw
											// border
			gc.strokeRect(-this.getCanvasDisplacedX(), -this.canvasDisplacedY, model.getWidth() * cellSize,
					model.getHeight() * cellSize);
	}

	public int getPatternPositionX() {
		return patternPositionX;
	}

	/*
	 * 
	 * public void setPatternPositionX(int patternPositionX) { if
	 * (patternPositionX >= 0 && patternPositionX <= model.getWidth() -
	 * model.getPatternWidth() && patternPositionX > 0) this.patternPositionX =
	 * patternPositionX; else if(patternPositionX > model.getWidth() -
	 * model.getPatternWidth()) { this.patternPositionX =
	 * model.getWidth()-model.getPatternWidth(); } else this.patternPositionX =
	 * 0;
	 * 
	 * }
	 * 
	 */

	public int getPatternPositionY() {
		return patternPositionY;
	}

	/*
	 * public void setPatternPositionY(int patternPositionY) { if
	 * (patternPositionY >= 0 && patternPositionY <= model.getHeight() -
	 * model.getPatternHeight() && patternPositionY > 0) this.patternPositionY =
	 * patternPositionY; else if(patternPositionY > model.getHeight() -
	 * model.getPatternHeight()) { this.patternPositionY = model.getHeight() -
	 * model.getPatternHeight(); } else this.patternPositionY = 0; }
	 */
	/*
	 * 
	 * public void drawPattern() { gc.setFill(Color.BROWN); for (int x = 0; x <
	 * model.getPatternWidth(); x++) for (int y = 0; y <
	 * model.getPatternHeight(); y++) { if (x * cellSize < canvasDisplacedX || x
	 * * cellSize > canvasDisplacedX + windowWidth || cellSize * (y + 64) <
	 * canvasDisplacedY || cellSize * (y) > canvasDisplacedY + windowHeight) {
	 * 
	 * } else if (model.getPatternCellState(x, y)) { gc.fillOval(cellSize * (x)
	 * - canvasDisplacedX, cellSize * (y) - canvasDisplacedY, cellSize,
	 * cellSize); }
	 * 
	 * } }
	 * 
	 */

	public void drawPattern(int width, int height) {

	}

	public void drawCell(int x, int y) {
		if ((x < 0 || x > model.getWidth() || y < 0 || y > model.getHeight()) && this.model instanceof StaticMatrix) {

		} else {
			
			int cellSize = this.getCellSize();
			int canvasDisplacedX = this.getCanvasDisplacedX();
			int canvasDisplacedY = this.getCanvasDisplacedY();

			x = (x + canvasDisplacedX) / cellSize;
			y = (y + canvasDisplacedY) / cellSize;

			boolean alive = !model.getCellState(x, y);

			model.setCellState(x, y, alive);

			if (alive) {
				gc.setFill(model.getColor());
				gc.fillOval(cellSize * (x) - canvasDisplacedX, cellSize * (y) - canvasDisplacedY, cellSize, cellSize);
			} else {
				gc.setFill(Color.BLACK);
				gc.fillOval(cellSize * (x) - canvasDisplacedX, cellSize * (y) - canvasDisplacedY, cellSize, cellSize);
			}
		}
	}

	public void drawCell(int x, int y, boolean dragDraw) {
		if ((x < 0 || x > model.getWidth() || y < 0 || y > model.getHeight()) && this.model instanceof StaticMatrix) {

		} else {
			int cellSize = this.getCellSize();

			int canvasDisplacedX = this.getCanvasDisplacedX();
			int canvasDisplacedY = this.getCanvasDisplacedY();

			x = (x + canvasDisplacedX) / cellSize;
			y = (y + canvasDisplacedY) / cellSize;

			if (dragDraw) {
				if (!model.getCellState(x, y)) {
					gc.setFill(model.getColor());
					model.setCellState(x, y, true);
					gc.fillOval(cellSize * (x) - canvasDisplacedX, cellSize * (y) - canvasDisplacedY, cellSize,
							cellSize);
				}

			} else {

				if (model.getCellState(x, y)) {
					gc.setFill(Color.BLACK);
					model.setCellState(x, y, false);
					gc.fillOval(cellSize * (x) - canvasDisplacedX, cellSize * (y) - canvasDisplacedY, cellSize,
							cellSize);
				}
			}
		}
	}

	public void zoom(int zoom) {
		int cellSize = this.getCellSize();

		int middleOfScreenX = (int) this.getWindowWidth() / 2;
		int middleOfScreenY = (int) this.getWindowHeight() / 2;

		int x = ((middleOfScreenX + this.getCanvasDisplacedX()) / cellSize);
		int y = ((middleOfScreenY + this.getCanvasDisplacedY()) / cellSize);

		this.setCanvasDisplacedX(x * (this.getCellSize() + zoom) - middleOfScreenX);
		this.setCanvasDisplacedY(y * (this.getCellSize() + zoom) - middleOfScreenY);

		this.setCellSize(this.getCellSize() + zoom);

		this.drawNextGeneration();

	}

	public void clearCanvas() {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, windowWidth, windowHeight);
		gc.setStroke(Color.GRAY);
		if (2 == 3)
			gc.strokeRect(-this.getCanvasDisplacedX(), -this.canvasDisplacedY, model.getWidth() * cellSize,
					model.getHeight() * cellSize);

	}

	public void movePosition(int x, int y) {
		this.setCanvasDisplacedX(this.getCanvasDisplacedX() + x);
		this.setCanvasDisplacedY(this.getCanvasDisplacedY() + y);
		this.drawNextGeneration();
	}

	public void zoom(int zoom, ScrollEvent event) {
		if ((this.getCellSize() + zoom) > 0 && (this.getCellSize() + zoom) <= 35) {
			int cellSize = this.getCellSize();

			this.setCellSize(this.getCellSize() + zoom);

			int x = (int) event.getX();
			int y = (int) event.getY();

			int xDivCell = (x + this.getCanvasDisplacedX()) / cellSize;
			int yDivCell = (y + this.getCanvasDisplacedY()) / cellSize;

			this.setCanvasDisplacedX(xDivCell * (cellSize + zoom) - x + (x + this.getCanvasDisplacedX()) % cellSize);
			this.setCanvasDisplacedY(yDivCell * (cellSize + zoom) - y + (y + this.getCanvasDisplacedY()) % cellSize);

			this.drawNextGeneration();
		}

	}

	public void drawNextGeneration() {
		clearCanvas();
		int shiftedToRight = 0;
		int shiftedDownwards = 0;
		if(model instanceof DynamicMatrix){
			shiftedToRight = ((DynamicMatrix) this.model).getShiftedToRight();
			shiftedDownwards =((DynamicMatrix)this.model).getShiftedDownwards();
		}

		gc.setFill(model.getColor());
		for (int x = 0; x < model.getWidth(); x++)
			for (int y = 0; y < model.getHeight(); y++) {
				if (model.getCellState(x, y)) {
					gc.fillOval(cellSize * (x-shiftedToRight) - canvasDisplacedX, cellSize * (y-shiftedDownwards) - canvasDisplacedY, cellSize,
							cellSize);
				}

			}
		
		System.out.printf("(width, height): (%d,%d)\n", model.getWidth(), model.getHeight());

	}

	/*
	 * if(model.settingPattern) { gc.setStroke(Color.WHITE);
	 * gc.strokeRect(-this.canvasDisplacedX + patternPositionX*cellSize,
	 * -this.canvasDisplacedY + patternPositionY*cellSize,
	 * model.getPatternWidth()*cellSize, model.getPatternHeight()*cellSize);
	 * 
	 * double lowest= 10000; gc.setFill(Color.RED); for (int x = 0; x <
	 * model.getPatternWidth(); x++){ for (int y = 0; y <
	 * model.getPatternHeight(); y++) { if (model.getPatternCellState(x, y)) {
	 * gc.fillOval(cellSize * (x+patternPositionX) - canvasDisplacedX , cellSize
	 * * (y+patternPositionY)- canvasDisplacedY , cellSize, cellSize); }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * }
	 */
	

	public double getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(double windowWidth) {
		this.windowWidth = windowWidth;
	}

	public double getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(double windowHeight) {
		this.windowHeight = windowHeight;
	}

	public int getCanvasDisplacedX() {
		return canvasDisplacedX;
	}

	public int getCanvasDisplacedY() {
		return canvasDisplacedY;
	}

	public void setCellSize(int size) {
		this.cellSize = size;
	}

	public int getCellSize() {
		return this.cellSize;
	}

	public void setCanvasDisplacedX(int x) {
		this.canvasDisplacedX = x;
	}

	public void setCanvasDisplacedY(int y) {
		this.canvasDisplacedY = y;
	}

}