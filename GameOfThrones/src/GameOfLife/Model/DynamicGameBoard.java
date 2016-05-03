package GameOfLife.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import GameOfLife.Model.GameBoard.BoardContainer;

/**
 * <p>
 * DynamicGameBoard represents a 2-dimensional Game of Life cellular automatom
 * that grows dynamically and use concurrent threads to determine each
 * subsequent generation. The initial size of the board is set to 100x100,
 * however when a cell is clicked outside the border, or the game evolves
 * outside the 100x100 grid, the grid grows(rectangularly) to encompass the
 * cells required. The data type used in the game board is an
 * ArrayList<ArrayList<Boolean>>. We use boolean cells to show that our
 * implementation of game of life both supports a "bit board" and a regular
 * boolean board. The optimalization using active cells is not used in this
 * class.
 * </p>
 * <p>
 * The x(horizontal) position of each cell is determined by index of the
 * arrayList element in the outer array, and the y(vertical) position is
 * determined by the index of the inner arrayList boolean value.
 * </p>
 * 
 * 
 * @author Markus Hellestveit
 *
 */
public class DynamicGameBoard extends GameBoard implements Cloneable {

	private ArrayList<ArrayList<Boolean>> currGeneration;
	private ArrayList<ArrayList<Boolean>> nextGeneration;

	/**
	 * Tracks the amount of COLUMNS added from the left of the original 100x100
	 * game board Whenever a cell is clicked outside of to the left the original
	 * border, or an alive cell evolves left of the border this varibale will be
	 * incremented to match the amount of columns added.
	 */
	private int insertedColumnsFromLeft = 0;
	/**
	 * Tracks the amount of rows added from the top of the original 100x100 game
	 * board. @see
	 */
	private int insertedRowsFromTop = 0;
	/**
	 * Amount of CPU cores the client running the program has.
	 */
	private final int CPUCORES;

	/**
	 * The arrayList holding the threads that determine each subsequent
	 * generation
	 */
	public ArrayList<Thread> workers = new ArrayList<Thread>();

	/**
	 * Constructor that sets the initial size of the game board to a 100x100
	 * grid and gets the available processors of the client to be used by
	 * threads.
	 * 
	 */
	public DynamicGameBoard() {
		super(100, 100);

		// Gets the available processor cores available on the client machine.
		CPUCORES = Runtime.getRuntime().availableProcessors();

		// initializes the ArrayLists representing the game board.
		currGeneration = new ArrayList<ArrayList<Boolean>>(super.getWidth());
		nextGeneration = new ArrayList<ArrayList<Boolean>>(super.getWidth());

		// Initializes the game board by adding 100 ArrayLists inside the main
		// ArrayList, to represents the current width of the board.
		for (int i = 0; i < super.getWidth(); i++) {
			currGeneration.add(new ArrayList<Boolean>());
			nextGeneration.add(new ArrayList<Boolean>());

			// for every element of the outer arrayList we add 100 boolean
			// values to represent the current height of the board
			currGeneration.get(i).addAll(Collections.nCopies(super.getHeight(), false));
			nextGeneration.get(i).addAll(Collections.nCopies(super.getHeight(), false));

		}
	}
	
	public void nextGenerationConcurrent() {

		long start = System.currentTimeMillis();

		int aliveNeighbors;
		createWorkers();

		try {
			runWorkers();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		workers.clear();

		int leftborderX = -insertedColumnsFromLeft;
		int rightBoderX = super.getWidth() - insertedColumnsFromLeft;
		int botBorderY = super.getHeight() - insertedRowsFromTop;
		int topBorderY = -insertedRowsFromTop;

		int x;
		int y;

		x = leftborderX - 1;
		for (y = 0; y < super.getHeight(); y++) {
			int yy = y - insertedRowsFromTop;
			aliveNeighbors = countNeighbours(x, yy);
			setCellStateFromRules(x, yy, aliveNeighbors);
		}

		y = topBorderY - 1;
		for (x = 0; x < super.getWidth(); x++) {
			int xx = x - insertedColumnsFromLeft;
			aliveNeighbors = countNeighbours(xx, y);
			setCellStateFromRules(xx, y, aliveNeighbors);
		}

		x = rightBoderX;
		for (y = 0; y < super.getHeight(); y++) {
			int yy = y - insertedRowsFromTop;
			aliveNeighbors = countNeighbours(x, yy);
			setCellStateFromRules(x, yy, aliveNeighbors);
		}

		y = botBorderY;
		for (x = 0; x < super.getWidth(); x++) {
			int xx = x - insertedColumnsFromLeft;
			aliveNeighbors = countNeighbours(xx, y);
			setCellStateFromRules(xx, y, aliveNeighbors);
		}

		updateCurrentGeneration();

		nextGenerationConcurrentPrintPerformance(start, System.currentTimeMillis());
	}


	@Override
	public void setCellState(int x, int y, BoardContainer container, boolean alive) {

		boolean inside = true;

		if (x + insertedColumnsFromLeft >= this.getWidth()) {
			if (alive)
				extendBorderFromRight((1 + x + insertedColumnsFromLeft) - this.getWidth());

			inside = false;
		}

		if (x + insertedColumnsFromLeft < 0) {
			if (alive)
				extendBorderFromLeft(Math.abs(x + insertedColumnsFromLeft));

			inside = false;
		}

		if (y + insertedRowsFromTop < 0) {
			if (alive)
				extendBorderFromTop(Math.abs(y + insertedRowsFromTop));

			inside = false;
		}

		if (y + insertedRowsFromTop >= this.getHeight()) {
			if (alive)
				extendBorderFromBottom((1 + y + insertedRowsFromTop) - this.getHeight());

			inside = false;
		}

		ArrayList<ArrayList<Boolean>> cells = getArrayList(container);

		if (inside || !inside && alive)
			cells.get(x + insertedColumnsFromLeft).set(y + insertedRowsFromTop, alive);
	}

	@Override
	public boolean getCellState(int x, int y, BoardContainer container) {
		{
			if (x + insertedColumnsFromLeft >= this.getWidth()) {
				return false; // hvis du er utenfor brettet er den alltid false;
			}

			if (x + insertedColumnsFromLeft < 0) {
				return false;
			}

			if (y + insertedRowsFromTop < 0) {
				return false;
			}

			if (y + insertedRowsFromTop >= this.getHeight()) {
				return false;
			}

			ArrayList<ArrayList<Boolean>> cells = getArrayList(container);

			return cells.get(x + insertedColumnsFromLeft).get(y + insertedRowsFromTop);

		}
	}


	/**
	 * Loops through the entire gameBoard
	 */
	@Override
	public void determineNextGeneration() {

	}

	/**
	 * When determining a new generation, the new generation is stored in the
	 * <code>nextGeneration</code> arraylist. After the entired next generation
	 * has been determine, the values from <code<NnextGeneration</code> are
	 * copies to currentGeneration, which is the container used for drawing the
	 * next generation.
	 */
	private void updateCurrentGeneration() {
		for (int i = 0; i < super.getWidth(); i++) {
			for (int j = 0; j < super.getHeight(); j++) {
				this.getCurrGeneration().get(i).set(j, this.getNextGeneration().get(i).get(j));

			}
		}
	}

	@Override
	public void createPattern() {

	}

	public void createWorkers() {

		for (int i = 0; i < CPUCORES; i++) {

			int starte;
			int slutte;
			if (i == CPUCORES - 1) {
				starte = (super.getWidth() / CPUCORES) * i - insertedColumnsFromLeft;
				slutte = super.getWidth() - insertedColumnsFromLeft;
			} else {
				starte = ((super.getWidth()) / CPUCORES) * i - insertedColumnsFromLeft;
				slutte = ((super.getWidth()) / CPUCORES) * (i + 1) - insertedColumnsFromLeft;
			}
			workers.add(new Thread(() -> {
				determineNextGenerationConcurrent(starte, slutte);
			}));
		}

	}

	public void runWorkers() throws InterruptedException {

		for (Thread t : workers) {
			t.start();
		}

		for (Thread t : workers) {
			t.join();
		}

	}

	@Override
	public void determineNextGenerationConcurrent(int starte, int slutte) {
		int aliveNeighbors;
		for (int x = starte; x < slutte; x++) {
			for (int y = -insertedRowsFromTop; y < super.getHeight() - insertedRowsFromTop; y++) {
				{
					aliveNeighbors = countNeighbours(x, y);
					setCellStateFromRules(x, y, aliveNeighbors);
				}

			}
		}

	}

	/**
	 * 
	 * 
	 * @param x the horizontal position of the current cell to count the neighbors of
	 * @param y the vertical position of the current cell to count the neighbors of
	 */
	@Override
	public int countNeighbours(int x, int y) {
		int aliveNeighbours = 0;
		int neighborX;
		int neighborY;

		for (int i = -1; i <= 1; i++) // (2)
			for (int j = -1; j <= 1; j++) {
				if (!(i == 0 && j == 0))// (3)
				{
					neighborX = x + i;
					neighborY = y + j;

					if (this.getCellState(neighborX, neighborY, BoardContainer.CURRENTGENERATION)) {
						aliveNeighbours++;
					}
				}
			}

		return aliveNeighbours;
	}

	private void setCellStateFromRules(int x, int y, int aliveNeighbours) {
		boolean alive = getCellState(x, y, BoardContainer.CURRENTGENERATION); // (1)

		if (alive) {
			boolean survive = false;

			for (int l = 0; l < super.getRules().getSurvivalRules().length && survive == false; l++)
				if (aliveNeighbours == super.getRules().getSurvivalRules()[l])
					survive = true;

			if (!survive) {
				setCellState(x, y, BoardContainer.NEXTGENERATION, false);
			} else {
				setCellState(x, y, BoardContainer.NEXTGENERATION, true);
			}
		} else {
			boolean birth = false;

			for (int l = 0; l < super.getRules().getBirthRules().length && birth == false; l++)// (2)
				if (aliveNeighbours == super.getRules().getBirthRules()[l])
					birth = true;

			if (birth) {
				setCellState(x, y, BoardContainer.NEXTGENERATION, true);
			} else {
				setCellState(x, y, BoardContainer.NEXTGENERATION, false);
			}
		}
	}

	// assumes board has the same dimensions as gameboard
	public <T> void setBoard(boolean[][] element) {
		boolean[][] board = element;
		for (int x = 0; x < super.getWidth(); x++) {
			for (int y = 0; y < super.getHeight(); y++) {
				if (board[x][y]) {
					setCellState(x - insertedColumnsFromLeft, y - insertedColumnsFromLeft,
							BoardContainer.CURRENTGENERATION, true);

				}
			}
		}
	}

	public void nextGenerationPrintPerformance(long start, long end) {
		System.out.printf("Time elapsed(ms): %d)\n", (end - start));
	}

	public void nextGenerationConcurrentPrintPerformance(long start, long end) {
		System.out.printf("Time elapsed(ms): %d)\n", (end - start));
	}

	public ArrayList<ArrayList<Boolean>> getCurrGeneration() {
		return currGeneration;
	}

	public void setCurrGeneration(ArrayList<ArrayList<Boolean>> currGeneration) {
		this.currGeneration = currGeneration;
	}

	public ArrayList<ArrayList<Boolean>> getNextGeneration() {
		return nextGeneration;
	}

	public void setNextGeneration(ArrayList<ArrayList<Boolean>> nextGeneration) {
		this.nextGeneration = nextGeneration;
	}

	private ArrayList<ArrayList<Boolean>> getArrayList(BoardContainer bc) {
		ArrayList<ArrayList<Boolean>> cells = null;

		switch (bc) {
		case CURRENTGENERATION:
			cells = this.getCurrGeneration();
			break;
		case NEXTGENERATION:
			cells = this.getNextGeneration();
			break;
		}

		return cells;

	}

	@Override
	public void resetGameBoard() {
		for (int i = 0; i < super.getWidth(); i++) {
			for (int j = 0; j < super.getHeight(); j++) {
				setCellState(i - insertedColumnsFromLeft, j - insertedRowsFromTop, BoardContainer.CURRENTGENERATION,
						false);
				setCellState(i - insertedColumnsFromLeft, j - insertedRowsFromTop, BoardContainer.NEXTGENERATION,
						false);
			}
		}

	}

	@Override
	public void transferPattern(int startX, int startY) {
		super.setSettingPattern(false);
		RLEPattern pattern = super.getPattern();
		for (int x = 0; x < pattern.getWidth(); x++) {
			for (int y = 0; y < pattern.getHeight(); y++) {
				setCellState(x + startX, y + startY, BoardContainer.CURRENTGENERATION, pattern.getPattern()[x][y]);

			}
		}

	}

	public int getInsertedRowsFromTop() {
		return insertedRowsFromTop;
	}

	public void setInsertedRowsFromTop(int insertedRowsFromTop) {
		this.insertedRowsFromTop = insertedRowsFromTop;
	}

	public int getInsertedColumnsFromLeft() {
		return insertedColumnsFromLeft;
	}

	public void setInsertedColumnsFromLeft(int insertedColumnsFromLeft) {
		this.insertedColumnsFromLeft = insertedColumnsFromLeft;
	}

	public Object clone() {
		DynamicGameBoard DynamicGameBoardCopy = null;
		DynamicGameBoardCopy = (DynamicGameBoard) super.clone();
		ArrayList<ArrayList<Boolean>> currGenCopy = new ArrayList<ArrayList<Boolean>>();
		ArrayList<ArrayList<Boolean>> nextGenCopy = new ArrayList<ArrayList<Boolean>>();
		ArrayList<ArrayList<Boolean>> activeGenCopy = new ArrayList<ArrayList<Boolean>>();
		ArrayList<ArrayList<Boolean>> NextActiveGenCopy = new ArrayList<ArrayList<Boolean>>();

		for (int i = 0; i < super.getWidth(); i++) {
			currGenCopy.add(i, new ArrayList<Boolean>());
			nextGenCopy.add(i, new ArrayList<Boolean>());
			activeGenCopy.add(i, new ArrayList<Boolean>());
			NextActiveGenCopy.add(i, new ArrayList<Boolean>());

			for (int j = 0; j < super.getHeight(); j++) {
				currGenCopy.get(i).add(j, this.getCurrGeneration().get(i).get(j));
				nextGenCopy.get(i).add(j, this.getNextGeneration().get(i).get(j));

			}
		}

		DynamicGameBoardCopy.currGeneration = currGenCopy;
		DynamicGameBoardCopy.nextGeneration = nextGenCopy;

		return DynamicGameBoardCopy;
	}

	// rounds up rowsRequired by 100 and stores it in rowsToInsert
	// when you insert an element into an index that already has an element
	// the new element pushes the original one index to the right
	public void extendBorderFromLeft(int columnsRequired) {

		int columnsToInsert = columnsRequired;

		for (int i = 0; i < columnsToInsert; i++) {
			currGeneration.add(i, new ArrayList<Boolean>(100));
			nextGeneration.add(i, new ArrayList<Boolean>(100));

			for (int j = 0; j < super.getHeight(); j++) {
				currGeneration.get(i).add(j, false);
				nextGeneration.get(i).add(j, false);
			}

		}
		this.insertedColumnsFromLeft += columnsToInsert;
		super.setWidth(super.getWidth() + columnsToInsert);
		System.out.println("LEFT + " + columnsToInsert);
	}

	public void extendBorderFromTop(int rowsRequired) {
		int rowsToInsert = rowsRequired;

		for (int i = 0; i < super.getWidth(); i++) {

			for (int j = 0; j < rowsToInsert; j++) {
				currGeneration.get(i).add(j, false);
				nextGeneration.get(i).add(j, false);
			}

		}
		this.insertedRowsFromTop += rowsToInsert;
		super.setHeight(rowsToInsert + super.getHeight());
		System.out.println("TOP + " + rowsToInsert);
	}

	public void extendBorderFromRight(int columnsRequired) {

		int columnsToInsert = columnsRequired;

		for (int i = this.getWidth(); i < this.getWidth() + columnsToInsert; i++) {
			currGeneration.add(i, new ArrayList<Boolean>());
			nextGeneration.add(i, new ArrayList<Boolean>());
			for (int j = 0; j < this.getHeight(); j++) {
				currGeneration.get(i).add(j, false);
				nextGeneration.get(i).add(j, false);

			}

		}

		super.setWidth(super.getWidth() + columnsToInsert);
		System.out.println("Right: " + columnsToInsert);

	}

	public void extendBorderFromBottom(int rowsRequired) {
		int rowsToInsert = rowsRequired;

		for (int i = 0; i < this.getWidth(); i++) {

			for (int j = this.getHeight(); j < rowsToInsert + this.getHeight(); j++) {
				currGeneration.get(i).add(j, false);
				nextGeneration.get(i).add(j, false);
			}
		}

		super.setHeight(super.getHeight() + rowsToInsert);

		System.out.println("BOT + " + rowsToInsert);
	}

		

	/**
	 * Determines the next generation of each game board and updates the current
	 * game board to the next generation using the private helper method @see
	 * updateCurrentGeneration().
	 * The for loop loops through every cell in the board and calls countNeighbors() in order to get the amount
	 * of living neighbors around that cell. Then that cells along with the amount of neighbours are sent as arguments
	 * to <code>setCellStateFromRules(x, y, aliveNieghbors)</code> which stores the new state of the cell, depending on the current rule set.
	 * <p>
	 * The we add subtract 1 from the start point of both for loops, and add 1 to the end of both end points.
	 * We do this so we are able to count the neighbors of cells one row/columns outside of the border. If a cell outside the
	 * main border has 3 dead neighbors(regular conway's life rules), then we extend the the game board one row or column and sets the
	 * state of that cell to alive. For example if a cell is one column right of the current border has 3 dead neighbors, then
	 * the method extendBorderFromRight is called and the main game board is expanded by one column to encompass that alive cell.
	 * However if all cells just outside the border are dead in the next generation as well, the game board will not be expanded.
	 * 
	 * 
	 * @see updateCurrentGeneration()
	 */
	@Override
	public void nextGeneration() {

		int aliveNeighbors;
		//loops through every cell in the board, including every cell just one unit outside of the border
		for (int x = -insertedColumnsFromLeft - 1; x < super.getWidth() - insertedColumnsFromLeft + 1; x++) {
			for (int y = -insertedRowsFromTop - 1; y < super.getHeight() - insertedRowsFromTop + 1; y++) {
				{
					//get the amount of living neighbors each cell has.
					aliveNeighbors = countNeighbours(x, y);
					//stores the cell's next state in the nextGeneration ArrayList.
					setCellStateFromRules(x, y, aliveNeighbors);
				}
			}
		}
		//copies the values of the nextGeneration onto the current generation ArrayList.
		updateCurrentGeneration();

	}

	@Override
	public String toString() {
		String currentGen = "";

		for (int y = -insertedRowsFromTop; y < super.getHeight() - insertedRowsFromTop; y++) {
			for (int x = -insertedColumnsFromLeft; x < super.getWidth() - insertedColumnsFromLeft; x++) {
				if (getCellState(x, y, BoardContainer.CURRENTGENERATION))
					currentGen += "1";
				else
					currentGen += "0";

			}
		}
		return currentGen;
	}

}