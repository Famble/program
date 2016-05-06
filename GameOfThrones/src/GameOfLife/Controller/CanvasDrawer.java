package GameOfLife.Controller;

import GameOfLife.Model.GameBoard;
import GameOfLife.Model.GameBoardFactorySingleTon;

import com.sun.javafx.scene.BoundsAccessor;

import GameOfLife.Model.DynamicGameBoard;
import GameOfLife.Model.BitGameBoard;
import GameOfLife.Model.GameBoard.BoardContainer;
import GameOfLife.Model.RLEPattern;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

/**
 * Performs methods that alter the canvas. CanvasDrawer draws cells and changes their state if the user clicks on them or
 * draws entire generation when a new generation has been determined. It also offers functionality for zooming the gameboard and manouvering
 * around the game board.
 * 
 * @author Markus Hellestveit
 *
 */
public class CanvasDrawer {
	private GraphicsContext graphicsContext;
	protected GameBoard model;
	private int cellSize = 5;
	/**
	 * The amount the user has dragged the canvas in the horizontal direction
	 */
	private int canvasDisplacedX;
	/**
	 * the amount the user has dragged the canvas in the vertical direction
	 */
	private int canvasDisplacedY;
	/**
	 * Width of visible canvas windows
	 */
	private double windowWidth;
	/**
	 * height of visible canvas window
	 */
	private double windowHeight;
	/**
	 * 
	 */
	boolean showBorder = false;
	private RLEPattern pattern;

	/**
	 * Responsible for drawing onto the game board.
	 * 
	 * 
	 * @param graphicsContext
	 *            object associated with the canvas that displays the game.
	 */
	public CanvasDrawer(GraphicsContext graphicsContext) {
		this.model = GameBoardFactorySingleTon.getInstance();
		this.graphicsContext = graphicsContext;
		canvasDisplacedX = 0;
		canvasDisplacedY = 0;

		graphicsContext.setStroke(Color.GRAY);

	}

	/**
	 * if the current game board is an instance of DynamicGameBoard, then we
	 * return the amount of columns that has been inserted to the left of the
	 * board to account for the rightward shift in the drawing. If the board not
	 * an instance of DynamicGameBoard then the method returns 0.
	 * 
	 * @return the amount of columns that has been added to the left of the
	 *         board.
	 */
	public int getInsertedColumnsFromLeft() {
		int insertedColumnsFromLeft = 0;
		if (this.model instanceof DynamicGameBoard) {
			insertedColumnsFromLeft = ((DynamicGameBoard) this.model).getInsertedColumnsFromLeft();
		}

		return insertedColumnsFromLeft;
	}

	/**
	 * If the current game board is an instance of DynamicGameBoard, then the
	 * method return the amount of rows that has been inserted to the top of the
	 * board to account for the downward shift in the drawing. If the board is
	 * not an instance of DynamicGameBoard then the method returns 0.
	 * 
	 * @return the amount of rows that has been added to the top of the board.
	 */
	public int getInsertedRowsFromTop() {
		int insertedRowsFromTop = 0;
		if (this.model instanceof DynamicGameBoard) {
			insertedRowsFromTop = ((DynamicGameBoard) this.model).getInsertedRowsFromTop();
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

	/**
	 * 
	 * @param mouseClickX
	 *            the horizontal position of the canvas the user clicked
	 * @return the arrayindex of either container that corresponds to the
	 *         horizontal position the user clicked.
	 */
	public int getCorrepondingXArrayIndex(int mouseClickX) {
		// we add canvasDisplacedX to the mouseClickX and divide the sum by
		// cellSize in order to get the x index
		// that corresponds with where the user clicked.
		return (mouseClickX + getCanvasDisplacedX()) / cellSize;
	}

	/**
	 *
	 * @return the Array index the container that corresponds to the vertical
	 *         position the user clicked.
	 */
	public int getCorrepondingYArrayIndex(int y) {
		// we add canvasDisplacedY to the mouseClickY and divide the sum by
		// cellSize in order to get the Y index
		// that corresponds with where the user clicked.
		return (y + getCanvasDisplacedY()) / cellSize;
	}

	/**
	 * Changes the state of the cell at the position the user clicked and
	 * updates the game board model to reflect this change.
	 * 
	 * @param mouseClickX
	 *            horizontal position where the user clicked
	 * @param mouseClickY
	 *            vertical position where the user clicked
	 * @param draw
	 *            specifies whether the cell should be drawn and state to be set
	 *            to true, or if the cell should be killed and state set to
	 *            false.
	 */
	public void drawCell(int mouseClickX, int mouseClickY, boolean draw) {
		int x = getCorrepondingXArrayIndex(mouseClickX);
		int y = getCorrepondingYArrayIndex(mouseClickY);

		int posX = x * cellSize - canvasDisplacedX;
		int posY = y * cellSize - canvasDisplacedY;

		try {
			if (draw) {
				if (!model.getCellState(x + getInsertedColumnsFromLeft(), y + getInsertedRowsFromTop(),
						BoardContainer.CURRENTGENERATION)) {
					model.setCellState(x + getInsertedColumnsFromLeft(), y + getInsertedRowsFromTop(),
							BoardContainer.CURRENTGENERATION, true);
					if (this.model instanceof BitGameBoard)
						model.setCellState(x, y, BoardContainer.ACTIVEGENERATION, true);
					graphicsContext.setFill(model.getColor());
					graphicsContext.fillOval(posX, posY, cellSize, cellSize);
				}

			} else {

				if (model.getCellState(x + getInsertedColumnsFromLeft(), y + getInsertedRowsFromTop(),
						BoardContainer.CURRENTGENERATION)) {
					model.setCellState(x, y, BoardContainer.CURRENTGENERATION, false);
					graphicsContext.setFill(Color.BLACK);
					graphicsContext.fillOval(posX, posY, cellSize, cellSize);
				}
			}

		} catch (IndexOutOfBoundsException e) {
			System.out.println("Clicked outside of the border");
		}

	}

	/**
	 * Zooms in the middle of the screen
	 * @param zoom adds this number to the cellSize.
	 */
	public void zoomInMiddleOfScreen(int zoom) {
		int cellSize = this.getCellSize();

		int middleOfScreenX = (int) this.getWindowWidth() / 2;
		int middleOfScreenY = (int) this.getWindowHeight() / 2;

		int x = ((middleOfScreenX + this.getCanvasDisplacedX()) / cellSize);
		int y = ((middleOfScreenY + this.getCanvasDisplacedY()) / cellSize);

		this.setCanvasDisplacedX(x * (this.getCellSize() + zoom) - middleOfScreenX);
		this.setCanvasDisplacedY(y * (this.getCellSize() + zoom) - middleOfScreenY);

		this.setCellSize(this.getCellSize() + zoom);

		this.drawBoard();

	}

	/**
	 * Clears the canvas by drawing the visible canvas black.
	 */
	private void clearCanvas() {
		graphicsContext.setFill(Color.BLACK);
		graphicsContext.fillRect(0, 0, windowWidth, windowHeight);

	}

	/**
	 * Moves displaces the canvas in the position set by the user
	 * @param x the horizontal displacement of the canvas
	 * @param y the vertical displacement of the canvas
	 */
	public void movePosition(int x, int y) {
		this.setCanvasDisplacedX(this.getCanvasDisplacedX() + x);
		this.setCanvasDisplacedY(this.getCanvasDisplacedY() + y);
		this.drawBoard();
	}

	/**
	 * Zooms in at the position the user currently hovers his cursor over
	 * 
	 * @param zoom
	 *            either 1 or -1, the amount we change the size of the cells by
	 * @param cursorPosX
	 *            the horizontal position of the cursor
	 * @param cursorPosY
	 *            the vertical position of the cursor
	 */
	public void zoomOnCursor(int zoom, int cursorPosX, int cursorPosY) {
		if ((this.getCellSize() + zoom) > 0 && (this.getCellSize() + zoom) <= 35) {

			int x = getCorrepondingXArrayIndex(cursorPosX);
			int y = getCorrepondingYArrayIndex(cursorPosY);

			int cellSize = this.getCellSize();

			this.setCellSize(this.getCellSize() + zoom);

			this.setCanvasDisplacedX(
					x * (cellSize + zoom) - cursorPosX + (cursorPosX + this.getCanvasDisplacedX()) % cellSize);
			this.setCanvasDisplacedY(
					y * (cellSize + zoom) - cursorPosY + (cursorPosY + this.getCanvasDisplacedY()) % cellSize);

			this.drawBoard();
		}

	}

	private void drawBorder() {
		graphicsContext.strokeRect(-this.getCanvasDisplacedX() - (getInsertedColumnsFromLeft() * cellSize),
				-this.canvasDisplacedY - (getInsertedRowsFromTop() * cellSize), model.getWidth() * cellSize,
				model.getHeight() * cellSize);
	}

	/**
	 * Draws the game board from the current generation of cells
	 * 
	 */
	public void drawBoard() {

		clearCanvas();

		if (showBorder)
			drawBorder();

		graphicsContext.setFill(model.getColor());
		for (int x = 0; x < model.getWidth(); x++)
			for (int y = 0; y < model.getHeight(); y++) {
				if (model.getCellState(x, y, BoardContainer.CURRENTGENERATION)) {
					graphicsContext.fillOval(cellSize * (x - getInsertedColumnsFromLeft()) - canvasDisplacedX,
							cellSize * (y - getInsertedRowsFromTop()) - canvasDisplacedY, cellSize, cellSize);
				}

			}

		/*if the user has loaded an RLE pattern and before
		 * the user clicks enter, this method is called in order
		 * to draw the pattern the user has selected.
		 * 
		 */
		if (model.getSettingPattern())
			drawPattern();

	}

	/**
	 * draws pattern before its set by the user clicking enter
	 */
	public void drawPattern() {

		graphicsContext.setStroke(Color.WHITE);

		// draws the border outlining the pattern.
		graphicsContext.strokeRect(-this.canvasDisplacedX + pattern.getPatternTranslationX() * cellSize,
				-this.canvasDisplacedY + pattern.getPatternTranslationY() * cellSize, pattern.getWidth() * cellSize,
				pattern.getHeight() * cellSize);

		//uses the affine object in order to move the pattern into the position set by the user through arrowkeys.
		
		Affine translateXY = new Affine();
		translateXY.setTx(pattern.getPatternTranslationX() * cellSize);
		translateXY.setTy(pattern.getPatternTranslationY() * cellSize);
		graphicsContext.setTransform(translateXY);
		graphicsContext.setFill(Color.RED);

		for (int x = 0; x < pattern.getWidth(); x++) {
			for (int y = 0; y < pattern.getHeight(); y++) {
				if (pattern.getPattern()[x][y]) {
					graphicsContext.fillOval(cellSize * (x) - canvasDisplacedX, cellSize * (y) - canvasDisplacedY,
							cellSize, cellSize);
				}

			}
		}

		translateXY.setTx(0.0);
		translateXY.setTy(0.0);
		graphicsContext.setTransform(translateXY);// reset the transformation.
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

	public void setShowBorder(boolean showBorder) {
		this.showBorder = showBorder;
		if(showBorder)
			drawBorder();
	}

}