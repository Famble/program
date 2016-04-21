package GameOfLife.Controller;

import GameOfLife.Model.Matrix;

import com.sun.javafx.scene.BoundsAccessor;

import GameOfLife.Model.DynamicMatrix;
import GameOfLife.Model.StaticMatrix;
import GameOfLife.Model.Matrix.BoardContainer;
import GameOfLife.Model.RLEPattern;
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
	private RLEPattern pattern;

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

	public void setRLEPattern(RLEPattern pattern) {
		this.pattern = pattern;
	}

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

			boolean alive = !model.getCellState(x, y, BoardContainer.CURRENTGENERATION);

			model.setCellState(x, y, BoardContainer.CURRENTGENERATION, alive);
			model.setCellState(x, y, BoardContainer.ACTIVEGENERATION, alive);

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

			// System.out.printf("(x,y) = (%d,%d)\n", x, y);

			if (dragDraw) {
				if (!model.getCellState(x, y, BoardContainer.CURRENTGENERATION)) {
					gc.setFill(model.getColor());
					model.setCellState(x, y, BoardContainer.CURRENTGENERATION, true);
					model.setCellState(x, y, BoardContainer.ACTIVEGENERATION, true);
					gc.fillOval(cellSize * (x) - canvasDisplacedX, cellSize * (y) - canvasDisplacedY, cellSize,
							cellSize);
				}

			} else {

				if (model.getCellState(x, y, BoardContainer.CURRENTGENERATION)) {
					gc.setFill(Color.BLACK);
					model.setCellState(x, y, BoardContainer.CURRENTGENERATION, false);
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
		int shiftedRightwards = 0;
		int shiftedDownwards = 0;
		if (model instanceof DynamicMatrix) {
			shiftedRightwards = ((DynamicMatrix) this.model).getShiftedRightwards();
			shiftedDownwards = ((DynamicMatrix) this.model).getShiftedDownwards();
		}

		gc.setFill(model.getColor());
		for (int x = 0; x < model.getWidth(); x++)
			for (int y = 0; y < model.getHeight(); y++) {
				if (model.getCellState(x - shiftedRightwards, y - shiftedDownwards, BoardContainer.CURRENTGENERATION)) {
					gc.fillOval(cellSize * (x - shiftedRightwards) - canvasDisplacedX,
							cellSize * (y - shiftedDownwards) - canvasDisplacedY, cellSize, cellSize);
				}

			}

		if (model.getSettingPattern()) {
			
			gc.setStroke(Color.WHITE);
			gc.strokeRect(-this.canvasDisplacedX + pattern.getPatternStartPositionX() * cellSize,
					-this.canvasDisplacedY + pattern.getPatternStartPositionY() * cellSize,
					pattern.getPatternWidth() * cellSize, pattern.getPatternHeight() * cellSize);

			for (int x = 0; x < pattern.getPatternWidth(); x++) {
				for (int y = 0; y < pattern.getPatternHeight(); y++) {
					if (pattern.getPattern()[x][y]) {
						gc.fillOval(cellSize * (x + pattern.getPatternStartPositionX()) - canvasDisplacedX,
								cellSize * (y + pattern.getPatternStartPositionY()) - canvasDisplacedY, cellSize, cellSize);
					}
					
				}
			}

		}
		{

		}

	}

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