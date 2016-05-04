package GameOfLife.Controller;

import GameOfLife.Model.GameBoard;

import com.sun.javafx.scene.BoundsAccessor;

import GameOfLife.Model.DynamicGameBoard;
import GameOfLife.Model.BitGameBoard;
import GameOfLife.Model.GameBoard.BoardContainer;
import GameOfLife.Model.RLEPattern;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

public class CanvasDrawer {
	private GraphicsContext gc;
	protected GameBoard model;
	private int cellSize = 5;
	private int canvasDisplacedX;
	private int canvasDisplacedY;
	private double windowWidth;
	private double windowHeight;
	private RLEPattern pattern;

	public CanvasDrawer(GameBoard model, GraphicsContext gc) {
		this.model = model;
		this.gc = gc;
		canvasDisplacedX = 0;
		canvasDisplacedY = 0;
		gc.setStroke(Color.GRAY);

		
	}
	
	public int getInsertedColumnsFromLeft(){
		int insertedColumnsFromLeft = 0;
		if(this.model instanceof DynamicGameBoard){
			insertedColumnsFromLeft  = ((DynamicGameBoard) this.model).getInsertedColumnsFromLeft();
		}
		
		return insertedColumnsFromLeft;
	}
	
	public int getInsertedRowsFromTop(){
		int insertedRowsFromTop = 0;
		if(this.model instanceof DynamicGameBoard){
			insertedRowsFromTop  = ((DynamicGameBoard) this.model).getInsertedRowsFromTop();
		}
		
		return insertedRowsFromTop;
	}

	public void setGameBoard(GameBoard board) {
		this.model = board;
	}

	public void setRLEPattern(RLEPattern pattern) {
		this.pattern = pattern;
	}

	public void drawPattern(int width, int height) {

	}

	public int getCorrepondingXArrayIndex(int x) {
		return (x + getCanvasDisplacedX()) / cellSize;
	}

	public int getCorrepondingYArrayIndex(int y) {
		return (y + getCanvasDisplacedY()) / cellSize;
	}

	public void drawCell(int mouseClickX, int mouseClickY) {
		int x = getCorrepondingXArrayIndex(mouseClickX);
		int y = getCorrepondingYArrayIndex(mouseClickY);
		
		int posX = x * cellSize - canvasDisplacedX;
		int posY = y * cellSize - canvasDisplacedY;
		
		boolean alive = !model.getCellState(x+getInsertedColumnsFromLeft(), y+getInsertedRowsFromTop(), BoardContainer.CURRENTGENERATION);
		model.setCellState(x+getInsertedColumnsFromLeft(), y+getInsertedRowsFromTop(), BoardContainer.CURRENTGENERATION, alive);
		
		if(this.model instanceof BitGameBoard)
			model.setCellState(x, y, BoardContainer.ACTIVEGENERATION, alive);

		if (alive) {
			gc.setFill(model.getColor());
			gc.fillOval(posX, posY, cellSize, cellSize);
		} else {
			gc.setFill(Color.BLACK);
			gc.fillOval(posX, posY, cellSize, cellSize);
		}

	}

	public void drawCell(int mouseClickX, int mouseClickY, boolean dragDraw) {
		int x = getCorrepondingXArrayIndex(mouseClickX);
		int y = getCorrepondingYArrayIndex(mouseClickY);

		int posX = x * cellSize - canvasDisplacedX;
		int posY = y * cellSize - canvasDisplacedY;

		if (dragDraw) {
			if (!model.getCellState(x+getInsertedColumnsFromLeft(), y+getInsertedRowsFromTop(), BoardContainer.CURRENTGENERATION)) {
				model.setCellState(x+getInsertedColumnsFromLeft(), y+getInsertedRowsFromTop(), BoardContainer.CURRENTGENERATION, true);
				if(this.model instanceof BitGameBoard)
					model.setCellState(x, y, BoardContainer.ACTIVEGENERATION, true);
				gc.setFill(model.getColor());
				gc.fillOval(posX, posY, cellSize, cellSize);
			}

		} else {

			if (model.getCellState(x+getInsertedColumnsFromLeft(), y+getInsertedRowsFromTop(), BoardContainer.CURRENTGENERATION)) {
				model.setCellState(x, y, BoardContainer.CURRENTGENERATION, false);
				gc.setFill(Color.BLACK);
				gc.fillOval(posX, posY, cellSize, cellSize);
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
		int insertedColumnsFromLeft = 0;
		int insertedRowsFromTop = 0;
		if (model instanceof DynamicGameBoard) {
			insertedColumnsFromLeft = ((DynamicGameBoard) this.model).getInsertedColumnsFromLeft();
			insertedRowsFromTop = ((DynamicGameBoard) this.model).getInsertedRowsFromTop();
		}
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, windowWidth, windowHeight);
		gc.setStroke(Color.GRAY);

		

	}

	public void movePosition(int x, int y) {
		this.setCanvasDisplacedX(this.getCanvasDisplacedX() + x);
		this.setCanvasDisplacedY(this.getCanvasDisplacedY() + y);
		this.drawNextGeneration();
	}

	public void zoomOnCursor(int zoom, int mousePosX, int mousePosY) {
		if ((this.getCellSize() + zoom) > 0 && (this.getCellSize() + zoom) <= 35) {

			int x = getCorrepondingXArrayIndex(mousePosX);
			int y = getCorrepondingYArrayIndex(mousePosY);

			int cellSize = this.getCellSize();

			this.setCellSize(this.getCellSize() + zoom);

			this.setCanvasDisplacedX(
					x * (cellSize + zoom) - mousePosX + (mousePosX + this.getCanvasDisplacedX()) % cellSize);
			this.setCanvasDisplacedY(
					y * (cellSize + zoom) - mousePosY + (mousePosY + this.getCanvasDisplacedY()) % cellSize);

			this.drawNextGeneration();
		}

	}

	public void drawNextGeneration() {

		clearCanvas();
		int shiftedRightwards = getInsertedColumnsFromLeft();
		int shiftedDownwards = getInsertedRowsFromTop();
		

		gc.setStroke(Color.GRAY);
		gc.strokeRect(-this.getCanvasDisplacedX() - (shiftedRightwards * cellSize),
				-this.canvasDisplacedY - (shiftedDownwards * cellSize), model.getWidth() * cellSize,
				model.getHeight() * cellSize);

		
		gc.setFill(model.getColor());
		for (int x = 0; x < model.getWidth(); x++)
			for (int y = 0; y < model.getHeight(); y++) {
				if (model.getCellState(x, y, BoardContainer.CURRENTGENERATION)) {
					gc.fillOval(cellSize * (x-shiftedRightwards) - canvasDisplacedX, cellSize * (y-shiftedDownwards) - canvasDisplacedY, cellSize,
							cellSize);
				}

			}

		if (model.getSettingPattern())
			drawPattern();

	}

	public void drawPattern() {

		gc.setStroke(Color.WHITE);
		gc.strokeRect(-this.canvasDisplacedX + pattern.getPatternTranslationX() * cellSize,
				-this.canvasDisplacedY + pattern.getPatternTranslationY() * cellSize, pattern.getWidth() * cellSize,
				pattern.getHeight() * cellSize);

		Affine translateXY = new Affine();
		translateXY.setTx(pattern.getPatternTranslationX() * cellSize);
		translateXY.setTy(pattern.getPatternTranslationY() * cellSize);
		gc.setTransform(translateXY);
		gc.setFill(Color.RED);
		for (int x = 0; x < pattern.getWidth(); x++) {
			for (int y = 0; y < pattern.getHeight(); y++) {
				if (pattern.getPattern()[x][y]) {
					gc.fillOval(cellSize * (x) - canvasDisplacedX, cellSize * (y) - canvasDisplacedY, cellSize,
							cellSize);
				}

			}
		}

		translateXY.setTx(0.0);
		translateXY.setTy(0.0);
		gc.setTransform(translateXY);// reset
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