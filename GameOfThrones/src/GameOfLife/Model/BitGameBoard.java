package GameOfLife.Model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;

/**
 * <p>
 * BitGameBoard represents game of life through a 'bit array', by using a
 * two-dimensional long array. The class provides functionality to determine
 * each subsequent generation of game of life, using rules set by the user. The
 * game board is static, meaning that the width and height is final. When a cell
 * evolves outside the border of the board, it goes to the opposite side of the
 * board. For example if a cell is set alive, during the running of
 * nextGeneration(), just outside the right border, it continues on the same
 * height from the left border.
 * </p>
 * <p>
 * The horizontal x position of a cell in the board is determined by the index
 * of the long array inside the outer long[][] array. For example, x = 4, refers
 * to the 4th boolean array inside the outer 2d long array. However, the
 * vertical y position is determined a little differently. Each every long value
 * has 64 individual bits, which represent individual y points in the game
 * board. For example if you want the to access a cell at 66 height, you get
 * there through the second long and the 2nd bit in that long value. Further
 * explenation will be provided in the relevant methods.
 * 
 * Using a 'bit game board' gives us two important benefits(and some
 * drawbacks):.
 * <h3>Benefits</h3>
 * <ul>
 * <li>It minimizes the amount of memory used by the program, allowing greater
 * board sizes.</li>
 * <li>Allows us to check if 64 cells are dead by simply checking if a long
 * value is equal to zero</li>
 * </ul>
 * <h3>Drawbacks</h3>
 * <ul>
 * <li>Makes the code more complicated, making it harder for students not
 * familiar with bit-operations to develop the code</li>
 * <li>Makes the code less readable</li> .
 * </ul>
 * We have compensated for these drawback by encapsulating bit functionality in
 * methods with descriptive names, and through the use of comments.
 * 
 * </p>
 * <p>
 * This class also uses an 'active cell' optimization that enables the next
 * generation of cells to be determined faster. The optimization takes advantage
 * of the fact that a cell can only change if either of its 8 neighbors changes
 * state The active array stores cell that has change its state in the previous
 * generation, or has been clicked on by the user. When we are determining the
 * next generation it is only necessary to count the neighbors of every active
 * cells and its neighbors, drastically lowering the amount of iterations if the
 * board contains many inactive cells(which they usually do).
 * </p>
 *
 * @author Markus Hellestveit
 * @param width
 *            amount of cells vertically.
 * @param height
 *            amount of cell horizontally.
 */
@Deprecated
public class BitGameBoard extends GameBoard {
	/**
	 * A two-dimensional array representing the current generation of the game
	 * board
	 */
	private long[][] CurrGeneration;
	/**
	 * A two-dimensional array that stores the next generation before its copies
	 * to currentGeneration
	 */
	private long[][] nextGeneration;
	/**
	 * A two-dimensional array that stores the active cells of the current
	 * generation
	 */
	private long[][] activeCells;
	/**
	 * A two-dimensional array that stores the next generation of activel cells
	 * before its copies to the acticeCells array
	 */
	private long[][] newActiveCells;
	public int yDiv64;
	private int yMod64;

	/**
	 * Sets the width and height of the BitGameBoard instance. the width and
	 * height are sent to the GameBoard superclass' constructor, where they
	 * initialize the width and height fields. Since each value in the inner
	 * long[][] array represents 64 cells(bits), we can set the 'height' of each
	 * 2dimensional grid to height/64.
	 * 
	 * @param width
	 *            the amount of vertical cells the board supports
	 * @param height
	 *            the amount of horizontal cells the board supports.
	 */
	public BitGameBoard(int width, int height) {
		super(width, height);

		this.yDiv64 = (height / 64) + 1;
		this.yMod64 = height % 64;
		CurrGeneration = new long[width][yDiv64];
		nextGeneration = new long[width][yDiv64];
		activeCells = new long[width][yDiv64];
		newActiveCells = new long[width][yDiv64];
	}

	public void nextGenerationConcurrent() {
		
		int aliveNeighbors = 0;

		for (int x = 0; x < super.getHeight(); x++) {
			for (int y = 0; y < this.yDiv64; y++) {
				if (!(this.getActiveCells()[x][y] == 0)) {
					int j = y * 64;
					for (int bit = 0; bit < 64; bit++) {
						if (getCellState(x, j + bit, BoardContainer.ACTIVEGENERATION)) {
							for (int i = -1; i <= 1; i++)
								for (int k = -1; k <= 1; k++) {
									int xToCheck = x + i;
									int yToCheck = j + bit + k;

									if (xToCheck >= super.getWidth()) {
										xToCheck %= super.getWidth();
									} else if (xToCheck == -1) {
										xToCheck = super.getWidth() - 1;
									}
									if (yToCheck >= super.getHeight()) {
										yToCheck %= super.getHeight();
									} else if (yToCheck == -1) {
										yToCheck = super.getHeight() - 1;

									}
									aliveNeighbors = countNeighbours(xToCheck, yToCheck);
									setCellStateFromRules(xToCheck, yToCheck, aliveNeighbors);
								}

						}
					}
				}
			}
		}

		for (int x = 0; x < super.getWidth(); x++) {
			for (int y = 0; y < this.yDiv64; y++) {
				this.getCurrentGeneration()[x][y] = this.getNextGeneration()[x][y];
				this.getActiveCells()[x][y] = this.getNewActiveCells()[x][y];
			}
		}
	}

	public void determineNextGeneration() {

	}

	/**
	 * Counts the amount of neighbors cell given by the parameters (x,y) has.
	 * The neighbors are the cells next to the given cell. Cells have a total of 8 neighbors.
	 * 
	 * 
	 */
	public int countNeighbours(int x, int y) {
		int aliveNeighbours = 0;
		int neighborX;
		int neighborY;

		if (x >= super.getWidth() || y >= super.getHeight()) { 
			return 0;
		}
		
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++) {
				//ensures that the we don't current cell itself.
				if (!(i == 0 && j == 0))
				{
					
					//add the i index to get the cell's horizontal neighbors.
					neighborX = x + i;
					//adds the j index to get the cell's vertical neighbors.
					neighborY = y + j;

					//if the cell given by the parameter is at the right border of the game board
					//then we set the right neighbor of that cell to the left border of the board(to enable cells to move through the border and to the opposite side)
					if (neighborX >= super.getWidth()) {
						neighborX = 0;
					
					//if the cell given by the parameters is at the left border of the game board
					//then we set the horizontal left neighbor to right border.
					} else if (neighborX == -1) {
						neighborX = super.getWidth() - 1;
					}
					
					//works the same way as described above, but with the bottom and top border.
					if (neighborY >= super.getHeight()) {
						neighborY = 0;
					} else if (neighborY == -1) {
						neighborY = super.getHeight() - 1;

					}

					//if the neighbor is alive, it increments the aliveNeighbor variable
					if (getCellState(neighborX, neighborY, BoardContainer.CURRENTGENERATION)) {
						aliveNeighbours++;
					}
				}
			}
		//when all 8 neighbors have been counted, we return the amount of living neighbors.
		return aliveNeighbours;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param aliveNeighbours
	 */
	private void setCellStateFromRules(int x, int y, int aliveNeighbours) {
		boolean alive = getCellState(x, y, BoardContainer.CURRENTGENERATION); // (1)

		if (!alive) {
			boolean birth = false;

			for (int l = 0; l < super.getRules().getBirthRules().length && birth == false; l++)// (2)
				if (aliveNeighbours == super.getRules().getBirthRules()[l])
					birth = true;

			if (birth) {
				setCellState(x, y, BoardContainer.NEXTACTIVEGENERATION, true);
				setCellState(x, y, BoardContainer.NEXTGENERATION, true);
			} else {
				setCellState(x, y, BoardContainer.NEXTGENERATION, false);
				setCellState(x, y, BoardContainer.NEXTACTIVEGENERATION, false);
			}

		} else {
			boolean survive = false;

			for (int l = 0; l < super.getRules().getSurvivalRules().length && survive == false; l++)
				if (aliveNeighbours == super.getRules().getSurvivalRules()[l])
					survive = true;

			if (!survive) {
				setCellState(x, y, BoardContainer.NEXTGENERATION, false);
				setCellState(x, y, BoardContainer.NEXTACTIVEGENERATION, true);
			} else {
				setCellState(x, y, BoardContainer.NEXTGENERATION, true);
				setCellState(x, y, BoardContainer.NEXTACTIVEGENERATION, false);
			}

		}
	}

	public void setBoard(long board[][]) {

		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[i].length; j++) {
				this.getActiveCells()[i][j] = board[i][j];
				this.getCurrentGeneration()[i][j] = board[i][j];
			}

	}

	public String toString() {
		int cellsInLong = 64;
		String bitString = "";

		for (int y = 0; y < this.yDiv64; y++) {

			if (y == this.yDiv64 - 1)
				cellsInLong = this.yMod64;

			for (int k = 0; k < cellsInLong; k++) {
				for (int x = 0; x < super.getWidth(); x++) {
					bitString += ((this.getCurrentGeneration()[x][y] >> k) & 1);
				}
			}
		}

		//
		return bitString;
	}

	public void setCurrentGeneration(long[][] currGen) {
		this.CurrGeneration = currGen;
	}

	public long[][] getCurrentGeneration() {
		return this.CurrGeneration;
	}

	public long[][] getNextGeneration() {
		return this.nextGeneration;
	}

	public long[][] getActiveCells() {
		return activeCells;
	}

	public void setActiveCells(long[][] activeCells) {
		this.activeCells = activeCells;
	}

	public long[][] getNewActiveCells() {
		return newActiveCells;
	}

	public void setNewActiveCells(long[][] newActiveCells) {
		this.newActiveCells = newActiveCells;
	}

	public int getY() {
		return this.yDiv64;
	}

	@Override
	public boolean getCellState(int x, int y, BoardContainer bc) {
		long[][] cells = null;

		switch (bc) {
		case CURRENTGENERATION:
			cells = this.getCurrentGeneration();
			break;
		case NEXTGENERATION:
			cells = this.getNextGeneration();
			break;
		case ACTIVEGENERATION:
			cells = this.getActiveCells();
			break;
		case NEXTACTIVEGENERATION:
			cells = this.getNewActiveCells();
			break;
		}

		return (cells[x][y / 64] >>> y % 64 & 1) == 1;

	}

	private long[][] selectArray(BoardContainer bc) {

		long[][] cells = null;
		switch (bc) {
		case CURRENTGENERATION:
			cells = this.getCurrentGeneration();
			break;
		case NEXTGENERATION:
			cells = this.getNextGeneration();
			break;
		case ACTIVEGENERATION:
			cells = this.getActiveCells();
			break;
		case NEXTACTIVEGENERATION:
			cells = this.getNewActiveCells();
			break;

		}

		return cells;
	}

	@Override
	public void setCellState(int x, int y, BoardContainer bc, boolean alive) {
		long[][] cells = selectArray(bc);

		if (alive)
			cells[x][y / 64] |= (1L << y % 64);
		else
			cells[x][y / 64] &= ~(1L << y % 64);
	}

	@Override
	public void resetGameBoard() {
		for (int x = 0; x < super.getWidth(); x++) {
			for (int y = 0; y < (super.getHeight() / 64) + 1; y++) {
				this.getCurrentGeneration()[x][y] = 0;
				this.getNextGeneration()[x][y] = 0;
				this.getActiveCells()[x][y] = 0;
				this.getNewActiveCells()[x][y] = 0;
			}
		}

	}

	@Override
	public void transferPattern(int X, int startY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createPattern() {
		// TODO Auto-generated method stub

	}

	@Override
	public void nextGeneration() {
		nextGenerationConcurrent();

	}

	/*
	 * @Override public void determineNextGenerationConcurrent(int x, int y) {
	 * // TODO Auto-generated method stub
	 * 
	 * }
	 */

	@Override
	public void determineNextGenerationOfSector(int start, int end) {
		// TODO Auto-generated method stub

	}

}