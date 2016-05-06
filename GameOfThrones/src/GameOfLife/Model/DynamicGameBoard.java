package GameOfLife.Model;

import java.util.ArrayList;
import java.util.Arrays;
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
 * boolean board. The optimization using active cells is not used in this class.
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

	/**
	 * Holds the current generation of game of life.
	 */
	private ArrayList<ArrayList<Boolean>> currGeneration;
	/**
	 * Holdes the next generation of game of life.
	 */
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
	 * how many columns has been inserted from the left of the game board this generation
	 * It's either 1 or 0.
	 *
	 */
	public int currentInsertedColumnsFromLeft = 0;
	/**
	 * How many rows has been inserted from the top of the game board this generation
	 * it's either 1 or 0.
	 */
	public int currentInsertedRowsFromTop = 0;
	
	/**
	 * static instance of the dynamic game board.
	 */
	private static DynamicGameBoard dynamicGameBoard;


	/**
	 * Private constructor that sets the initial size of the game board to a 100x100
	 * grid and gets the available processors of the client to be used by
	 * threads.
	 * 
	 */
	private DynamicGameBoard() {
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
	
	
	/**
	 * Clears the current ArrayLists and creates new 2d arraylists
	 * based on the width and heigh specified by the parameters
	 * Used for testing.
	 * @param width of the game board
	 * @param height of the game board.
	 */
	public void setNewWidthAndHeight(int width, int height){
		currGeneration.clear();
		nextGeneration.clear();
		super.setWidth(width);
		super.setHeight(height);
		
		for (int i = 0; i < super.getWidth(); i++) {
			currGeneration.add(new ArrayList<Boolean>());
			nextGeneration.add(new ArrayList<Boolean>());

			currGeneration.get(i).addAll(Collections.nCopies(super.getHeight(), false));
			nextGeneration.get(i).addAll(Collections.nCopies(super.getHeight(), false));
		}
		
	}
	
	
	/**
	 * returns a single instance of the game board by envoking the private constructor if this class if
	 * not instance exists. If there exists an instance, then that instance will be returnd. Ensuring only
	 * one game board can be initializes.
	 * @return
	 */
	public static DynamicGameBoard getInstance(){
		if(dynamicGameBoard == null)
			dynamicGameBoard = new DynamicGameBoard();
		
		return dynamicGameBoard;
	}
	 
	
	/**
	 * Determines the next generation using threads and updates the game board.
	 */
	@Override
	public void nextGenerationConcurrent() {
		long start = System.currentTimeMillis();

		createThreads();

		/**
		 * runs each thread, that together determine the next generation of the
		 * board concurrently. The threads only determine and sets the next
		 * generation of the cell within the currently defined border. the for
		 * loops later in this method checks if any cells is alive one unit
		 * outside the border. This is done to avoid concurrency issues, where
		 * one thread extends the board, but the other already has operated on
		 * the board that is not extended.
		 */
		try {
			runThreads();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		/**
		 * clears the threads from the ArrayList(can't delete the because of
		 * garbage collection in java)
		 */
		workers.clear();

		nextGenerationOutsideLeftBorder();

		nextGenerationOutsideRightBorder();

		nextGenerationOutsideTopBorder();

		nextGenerationOutsideBottomBorder();

		// copies the next generation to the current generation
		updateCurrentGeneration();

		// print the amount of time the next generation method has taken.
		nextGenerationConcurrentPrintPerformance(start, System.currentTimeMillis());

		currentInsertedColumnsFromLeft = 0;
		currentInsertedRowsFromTop = 0;
	}
	
	/**
	 * Creates the threads used to determine each subsequent generation. The
	 * width of the game board is divided into sectors by the amount of CPUCORES
	 * the client computer has. Each thread is responsible for determining the
	 * next generation of its respective sector. The threads perform
	 * determineNextGenerationOfSector concurrently.
	 * 
	 */
	public void createThreads() {

		for (int sector = 0; sector < CPUCORES; sector++) {
			// the width of the sector each thread is responsible for(except the
			// last if super.getWidth() does not divide evenly into CPUCORES)
			int WidthOfSector = super.getWidth() / CPUCORES;
			int start;
			int end;
			// for the last sector we set end endpoint to super.getWidth();
			if (sector == CPUCORES - 1) {
				start = WidthOfSector * sector;
				end = super.getWidth();

			} else {
				start = WidthOfSector * sector;
				end = WidthOfSector * (sector + 1);
			}
			workers.add(new Thread(() -> {
				determineNextGenerationOfSector(start, end);
			}));
		}

	}
	
	/**
	 * t.start(); runs every thread concurrently, while t.join() waits for every
	 * thread to finish before continuing the main thread.
	 * 
	 * @throws InterruptedException
	 */
	public void runThreads() throws InterruptedException {

		for (Thread t : workers) {
			t.start();
		}

		// ensures that all threads are completed before
		for (Thread t : workers) {
			t.join();
		}

	}
	
	
	/**
	 * loops trough the sector specified by the start and end parameters, counts
	 * the neighbors and determines the next state of every cell within it.
	 * 
	 */
	private void determineNextGenerationOfSector(int start, int end) {
		int aliveNeighbors;
		for (int x = start; x < end; x++) {
			for (int y = 0; y < super.getHeight(); y++) {
				{
					aliveNeighbors = countNeighbours(x, y);
					setCellStateFromRules(x, y, aliveNeighbors);
				}

			}
		}

	}




	/**
	 * Checks the neighbors of each cell one unit left of the left border, if
	 * any cell meets the condition to become alive, then the board is extended
	 * one unit leftwards and currentInsertedFromLeft = 1. the column that
	 * previously was one unit left of the left border is now just inside the
	 * new extended borer, thus we add currentInsertFromLeft variable to keep
	 * checking the neighbors of that column.
	 * 
	 */
	private void nextGenerationOutsideLeftBorder() {
		int aliveNeighbors;
		int outSideLeftBorder = -1;
		for (int y = 0; y < super.getHeight(); y++) {
			aliveNeighbors = countNeighbours(outSideLeftBorder + currentInsertedColumnsFromLeft, y);
			setCellStateFromRules(outSideLeftBorder + currentInsertedColumnsFromLeft, y, aliveNeighbors);
		}
	}

	/**
	 * Checks the neighbors of each cell out unit right of the right border. If
	 * any cell meet the condition to become alive, then the board is extended
	 * one unit leftwards. Since adding an element at the end of an ArrayList
	 * does not alter any other element, we don't we don't need to keep track of
	 * how much the array has been displaced.
	 * 
	 */
	private void nextGenerationOutsideRightBorder() {

		int aliveNeighbors;
		int outsideRightBorder = super.getWidth();
		for (int y = 0; y < super.getHeight(); y++) {
			aliveNeighbors = countNeighbours(outsideRightBorder, y);
			setCellStateFromRules(outsideRightBorder, y, aliveNeighbors);
		}
	}

	/**
	 * Works the same way as nextGenerationOutsideLeftBorder, only with the top
	 * of the border.
	 * 
	 * @see #nextGenerationOutsideLeftBorder()
	 */
	private void nextGenerationOutsideTopBorder() {
		int aliveNeighbors;
		int outsideTopBorder = -1;
		for (int x = 0; x < super.getWidth(); x++) {
			aliveNeighbors = countNeighbours(x, outsideTopBorder + currentInsertedRowsFromTop);
			setCellStateFromRules(x, outsideTopBorder + currentInsertedRowsFromTop, aliveNeighbors);
		}

	}

	/**
	 * Works the same way as nextGenerationOutsideRightBorder, only with the
	 * bottom border
	 * 
	 * @see #nextGenerationOutsideRightBorder()
	 */
	private void nextGenerationOutsideBottomBorder() {
		int outsideBottomBorder = super.getHeight();
		int aliveNeighbors;
		for (int x = 0; x < super.getWidth(); x++) {
			aliveNeighbors = countNeighbours(x, outsideBottomBorder);
			setCellStateFromRules(x, outsideBottomBorder, aliveNeighbors);
		}
	}

	
	

	/**
	 * <p>
	 * Sets the cell specified by the x and y parameters to the state specified
	 * by the alive parameter. the BoardContainer parameter specifies which
	 * ArrayList should be altered(either nextGeneration or currentGeneration)
	 * in this class.
	 * </p>
	 * <p>
	 * If a cell state is set to be <b>alive</b> and the position of the cell is
	 * outside of the current game board, then the method will call either of
	 * the extendborderFrom.... methods, to enlarge the board, making it
	 * encompass the relevant cell. However if a cell outside the border is set
	 * to be dead, then no extension is made, since getCellState threats every
	 * cell outside the border as dead.
	 * </p>
	 * 
	 * <p>
	 * 
	 * 
	 * 
	 * @param x
	 *            the vertical position of the cell minus
	 *            <code>insertedColumnsFromLeft</code>
	 * @param y
	 *            the horizontal position of the cell minus
	 *            <code>insertedRowsFromTop</code>
	 * @param container
	 *            the Enum that maps to the array that will be altered by
	 *            setCellState
	 * @param alive
	 *            a boolean value that determines what state the cell should be
	 *            set to.
	 * @see #getCellState(int, int, BoardContainer)
	 */
	@Override
	public void setCellState(int x, int y, BoardContainer container, boolean alive) {

		/*
		 *  if x is greater than the width of the border and the cell is set
		 * to be alive, then we extend the border rightwards.
		 * 
		 */
		if (x >= this.getWidth()) {
			if (alive)
				extendBorderFromRight((1 + x) - this.getWidth());
			else
				return;
		}

		// if x is less than the left border of the game board and the cell is
		// set to be alive, then
		// we extend to border leftwards.
		// when we extend the border leftwards, the whole board is shifted
		// rightwards by Math.(x) positions.
		// we add currentInserted to x to account for this rightward shift of the game board.
		if (x < 0) {
			if (alive) {
				extendBorderFromLeft(Math.abs(x));
				x += currentInsertedColumnsFromLeft;
			} else
				return;
		}

		//works the as if(x<0) only with respect to the height of the game board
		if (y < 0) {
			if (alive) {
				extendBorderFromTop(Math.abs(y));
				y += currentInsertedRowsFromTop;
			} else
				return;
		}

		//works the same way as if(x >= this.getWidth()), but with respect to the height
		if (y >= this.getHeight()) {
			if (alive) {
				extendBorderFromBottom((1 + y) - this.getHeight());
			} else
				return;
		}

		ArrayList<ArrayList<Boolean>> cells = getArrayList(container);

		cells.get(x).set(y, alive);
	}

	/**
	 * gets state of the cell specified by the parameters and the BoardContainer enum.
	 * if the cell is beyond the border, the method with always return false.
	 */
	
	@Override
	public boolean getCellState(int x, int y, BoardContainer container) {
		{
			if (x >= this.getWidth()) {
				return false;
			}

			if (x < 0) {
				return false;
			}

			if (y < 0) {
				return false;
			}

			if (y >= this.getHeight()) {
				return false;
			}

			ArrayList<ArrayList<Boolean>> cells = getArrayList(container);

			return cells.get(x).get(y);

		}
	}

	/**
	 * When determining a new generation, the new generation is stored in the
	 * <code>nextGeneration</code> ArrayList. After the entire next generation
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

	/**
	 * Determines the next generation of each game board and updates the current
	 * game board to the next generation using the private helper method updateCurrentGeneration
	 * .The for loop loops through every cell in the
	 * board and calls countNeighbors() in order to get the amount of living
	 * neighbors around that cell. Then that cells along with the amount of
	 * neighbors are sent as arguments to
	 * <code>setCellStateFromRules(x, y, aliveNieghbors)</code> which stores the
	 * new state of the cell, depending on the current rule set.
	 * <p>
	 * The we subtract 1 from the start point of both for loops, and add 1
	 * to the end of both end points. We do this so we are able to count the
	 * neighbors of cells one row/columns outside of the border. If a cell
	 * outside the main border has 3 dead neighbors(regular conway's life
	 * rules), then we extend the the game board one row or column and sets the
	 * state of that cell to alive. For example if a cell  one column right of
	 * the current border has 3 dead neighbors, then the method
	 * extendBorderFromRight is called and the main game board is expanded by
	 * one column to encompass that alive cell. However if all cells just
	 * outside the border are dead in the next generation , the game
	 * board will not be expanded.
	 * 
	 * 
	 * @see #updateCurrentGeneration()
	 */
	@Override
	public void nextGeneration() {

		long start = System.currentTimeMillis();
		int aliveNeighbors;
		// loops through every cell in the board, including every cell just one
		// unit outside of the border
		for (int x = -1; x < super.getWidth() + 1; x++) {
			for (int y = -1; y < super.getHeight() + 1; y++) {
				{
					// get the amount of living neighbors each cell has.
					aliveNeighbors = countNeighbours(x + currentInsertedColumnsFromLeft, y + currentInsertedRowsFromTop);
					// stores the cell's next state in the nextGeneration
					// ArrayList.
					setCellStateFromRules(x + currentInsertedColumnsFromLeft, y + currentInsertedRowsFromTop, aliveNeighbors);
				}
			}
		}
		// copies the values of the nextGeneration onto the current generation
		// ArrayList.
		updateCurrentGeneration();
		currentInsertedColumnsFromLeft = 0;
		currentInsertedRowsFromTop = 0;

		// nextGenerationPrintPerformance(start, System.currentTimeMillis());

	}


	/**
	 * 
	 * 
	 * @param x
	 *            the horizontal position of the current cell to count the
	 *            neighbors of
	 * @param y
	 *            the vertical position of the current cell to count the
	 *            neighbors of
	 */
	@Override
	public int countNeighbours(int x, int y) {
		int aliveNeighbours = 0;
		int neighborX;
		int neighborY;

		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++) {
				if (!(i == 0 && j == 0)) {
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
		boolean alive = getCellState(x, y, BoardContainer.CURRENTGENERATION);

		if (alive) {
			boolean survive = false;

			for (int l = 0; l < super.getRules().getSurvivalRules().length && survive == false; l++)
				if (aliveNeighbours == super.getRules().getSurvivalRules()[l])
					survive = true;

			setCellState(x, y, BoardContainer.NEXTGENERATION, survive);

		} else {
			boolean birth = false;

			for (int l = 0; l < super.getRules().getBirthRules().length && birth == false; l++)// (2)
				if (aliveNeighbours == super.getRules().getBirthRules()[l])
					birth = true;

			setCellState(x, y, BoardContainer.NEXTGENERATION, birth);

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

	/**
	 * kills every cell
	 */
	@Override
	public void resetGameBoard() {
		for (int i = 0; i < super.getWidth(); i++) {
			for (int j = 0; j < super.getHeight(); j++) {
				setCellState(i, j, BoardContainer.CURRENTGENERATION, false);
				setCellState(i, j, BoardContainer.NEXTGENERATION, false);
			}
		}

	}

	/**
	 * Transfer the pattern from the superclass into the current generation And
	 * disables setting pattern sice the pattern is now stored in the current
	 * generation. Also makes every cell in the scope of the pattern active.
	 */
	@Override
	public void transferPattern(int startX, int startY) {
		// no longer setting the pattern
		super.setSettingPattern(false);
		RLEPattern pattern = super.getPattern();
		for (int x = 0; x < pattern.getWidth(); x++) {
			for (int y = 0; y < pattern.getHeight(); y++) {
				{
					setCellState(x + startX + insertedColumnsFromLeft, y + startY + insertedRowsFromTop,
							BoardContainer.CURRENTGENERATION, pattern.getPattern()[x][y]);
					currentInsertedColumnsFromLeft = 0;
					currentInsertedRowsFromTop = 0;
				}

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


	/**
	 * Adds the required columns of dead cells to the left of the gameBoard and
	 * pushes all the other columns to the right. adds the amount of
	 * columnsRequired to the insertedColumnsFromLeft variable to keep track of
	 * how much the game board has been shifted to the right.
	 * 
	 * @param columnsRequired
	 *            the amount of columns required to encompass the cell set by
	 *            setCellState
	 */
	private void extendBorderFromLeft(int columnsRequired) {

		// when we add an element to a position that already has an element
		// we push the original element, and every element to the right of it,
		// rightwards.
		for (int i = 0; i < columnsRequired; i++) {
			currGeneration.add(i, new ArrayList<Boolean>());
			nextGeneration.add(i, new ArrayList<Boolean>());

			currGeneration.get(i).addAll(Collections.nCopies(super.getHeight(), false));
			nextGeneration.get(i).addAll(Collections.nCopies(super.getHeight(), false));

		}

		// keeps track of how many columns we have inserted from the left.
		this.insertedColumnsFromLeft += columnsRequired;
		this.currentInsertedColumnsFromLeft += columnsRequired;
		// adds the required columns to the width of the game board.
		super.setWidth(super.getWidth() + columnsRequired);
	}

	/**
	 * Adds the required rows of dead cells to the top of the gameBoard and
	 * pushes all the other rows downwards. adds the amount of rowsRequired to
	 * the insertedColumnsFromLeft variable to keep track of how much the game
	 * board has been shifted downwards.
	 * 
	 * @param columnsRequired
	 *            the amount of rows required to encompass the cell set by
	 *            setCellState
	 */
	private void extendBorderFromTop(int rowsRequired) {

		for (int i = 0; i < super.getWidth(); i++) {
			for (int j = 0; j < rowsRequired; j++) {
				// when we add an element to a position that already has an
				// element
				// we push the original element, and every element to the below
				// it, downwards(when thinking about the ArrayList vertically.
				currGeneration.get(i).add(j, false);
				nextGeneration.get(i).add(j, false);
			}

		}

		this.currentInsertedRowsFromTop += rowsRequired;
		this.insertedRowsFromTop += rowsRequired;
		// adds the required rows to the height of the game board
		super.setHeight(rowsRequired + super.getHeight());
	}

	/**
	 * Adds the required columns at the right end of the board and adds
	 * <code>columnsRequried</code> to the <code>super.width</code> field.
	 * 
	 * @param columnsRequired
	 *            The amount of columns to encompass the living cell set by
	 *            setCellState()
	 * @see setCellState(int x, int y, BoardContainer bc, boolean alive)
	 */
	private void extendBorderFromRight(int columnsRequired) {

		for (int i = this.getWidth(); i < this.getWidth() + columnsRequired; i++) {
			currGeneration.add(i, new ArrayList<Boolean>());
			nextGeneration.add(i, new ArrayList<Boolean>());
			for (int j = 0; j < this.getHeight(); j++) {
				currGeneration.get(i).add(j, false);
				nextGeneration.get(i).add(j, false);

			}

		}

		super.setWidth(super.getWidth() + columnsRequired);

	}

	/**
	 * Adds the required rows at the bottom of the boar and adds
	 * <code>rowsRequired</code> to the <code>super.height</code>
	 * 
	 * @param rowsRequired
	 *            The amount of columns to encompass the living cell set by
	 *            setCellState()
	 * 
	 */
		private void extendBorderFromBottom(int rowsRequired) {
			int rowsToInsert = rowsRequired;
	
			for (int i = 0; i < this.getWidth(); i++) {
	
				for (int j = this.getHeight(); j < rowsToInsert + this.getHeight(); j++) {
					currGeneration.get(i).add(j, false);
					nextGeneration.get(i).add(j, false);
				}
			}
	
			super.setHeight(super.getHeight() + rowsToInsert);
	
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

	/**
	 * Converts the current generation to a one-dimensional string representation and returns it
	 * @return One dimensional string representation of the game board.
	 */
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