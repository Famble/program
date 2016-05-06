package GameOfLife.Model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import GameOfLife.Model.GameBoard.BoardContainer;
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
	
    private static BitGameBoard bitGameBoard;
    
    
    public static BitGameBoard getInstance()
    {
      if (bitGameBoard == null)
          bitGameBoard = new BitGameBoard(2000, 2000);
      return bitGameBoard;
    }
    
    /**
     * Creates new arrays with specified width and height
     * All cells will be killed.
     * Used for unit testing.
     */
    public void setNewWidthAndHeight(int width, int height){
    	super.setWidth(width);
    	super.setHeight(height);
    	CurrGeneration = new long[width][(height / 64) + 1];
		nextGeneration = new long[width][(height / 64) + 1];
		activeCells = new long[width][(height / 64) + 1];
		newActiveCells = new long[width][(height / 64) + 1];
    }


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
	private BitGameBoard(int width, int height) {
		super(width, height);

		CurrGeneration = new long[width][(height / 64) + 1];
		nextGeneration = new long[width][(height / 64) + 1];
		activeCells = new long[width][(height / 64) + 1];
		newActiveCells = new long[width][(height / 64) + 1];
	}
	

    


	/**
	 * Loops trough all the cells in board and checks if they are active.
	 * If a cell is active then that cell and all its neighbors will be sent as arguments 
	 * to the countNeighbors function and then set the state based on the rules and alive neighbors.
	 * Since cells become active when they change state, any cell with no active neighbors will remain the same
	 * through the next generation, that is why we only check the active cells and their neighbors.
	 */
	@Override
	public void nextGeneration() {
		
		int aliveNeighbors = 0;

		int height = super.getHeight()/64 + 1;
		for (int x = 0; x < super.getWidth(); x++) {
			for (int y = 0; y < height; y++) {
				if (!(this.activeCells[x][y] == 0)) {
					int j = y * 64;
					for (int bit = 0; bit < 64; bit++) {
						//if a cell is active we loop through all of its neighbors including itself and count their neighbors and pass them as arguments
						//to setCellStateFromRules.
						if (getCellState(x, j + bit, BoardContainer.ACTIVEGENERATION)) {
							for (int i = -1; i <= 1; i++)
								for (int k = -1; k <= 1; k++) {
									
									int xToCheck = x + i;
									int yToCheck = j + bit + k;

									//if the x to check is one unit greater than the width of the game board, we set it to the left border of the game board
									//to make the cells continue through the borders
									//else if the x to check is one unit to the left of the left border, we set it the the end of the right border.
									if (xToCheck == super.getWidth()) {
										xToCheck = 0;
									} else if (xToCheck == -1) {
										xToCheck = super.getWidth() - 1;
									}
									//works the same way as described just above, but with the top and bottome border of the board.
									if (yToCheck >= super.getHeight()) {
										yToCheck = 0;
									} else if (yToCheck == -1) {
										yToCheck = super.getHeight() - 1;

									}
									//get the amount of living neighbors of the every cell that is active or has a neighbor that is active
									aliveNeighbors = countNeighbours(xToCheck, yToCheck);
									//sets the cell based on its neighbors and the rule set.
									setCellStateFromRules(xToCheck, yToCheck, aliveNeighbors);
								}

						}
					}
				}
			}
		}
			//copies the nextActiveCells to the activeCells and the nextGeneration to the CurrentGeneration.	
			updateGameBoard();
		
	}
	
	/**
	 * Copies the next generation on to the currentGeneration.
	 */
	private void updateGameBoard(){
		for (int x = 0; x < super.getWidth(); x++) {
			for (int y = 0; y < (super.getHeight() / 64) + 1; y++) {
				this.CurrGeneration[x][y] = this.nextGeneration[x][y];
				this.newActiveCells[x][y] = this.newActiveCells[x][y];
			}
		}
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
					//else if the cell given by the parameters is at the left border of the game board
					//then we set the horizontal left neighbor to right border.
					if (neighborX == super.getWidth()) {
						neighborX = 0;
					} else if (neighborX == -1) {
						neighborX = super.getWidth() - 1;
					}
					
					//works the same way as described above, but with the bottom and top border.
					if (neighborY == super.getHeight()) {
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
	 * Sets the state of the cell given through the (x,y) parameters based on its neighbors and the rule set.
	 * Also stores the cell in the active array if the cell changed stated from the previous generation.
	 * 
	 * 
	 * @param x the vertical position of the given cell
	 * @param y the horizontal position of the given cell
	 * @param aliveNeighbours the amount of living neighbors the cell has.
	 * 
	 */
	private void setCellStateFromRules(int x, int y, int aliveNeighbours) {
		//checks whether the cell is alive or not.
		boolean alive = getCellState(x, y, BoardContainer.CURRENTGENERATION); // (1)

		//current cell is dead
		if (!alive) {
			boolean birth = false;
			//the conditions for making a dead cell alive is stored as a one dimensional int array
			//The elements tells us how many living neighbors a dead cell has to have in order to be born.
			//if the living neighbors of the current cell matches any of these, then birth is set to true and the for loop ends.
			for (int l = 0; l < super.getRules().getBirthRules().length && birth == false; l++)// (2)
				if (aliveNeighbours == super.getRules().getBirthRules()[l])
					birth = true;

			
			//if the dead cell should be born the in the next generation, then the cell has changed its state,
			//and thus become active. Therefore we set the nextgeneration to be true, aswell as the nextActiveGeneration.
			if (birth) {
				setCellState(x, y, BoardContainer.NEXTACTIVEGENERATION, true);
				setCellState(x, y, BoardContainer.NEXTGENERATION, true);
				//if the dead cell is determined to be dead in the next generation as well, then we set its stste to inactive in the next gen
			} else {
				setCellState(x, y, BoardContainer.NEXTGENERATION, false);
				setCellState(x, y, BoardContainer.NEXTACTIVEGENERATION, false);
			}

		//else if current cell is alive
		} else {
			boolean survive = false;

			//if the living neighbors meets the condition set by survivalRules, then survive is set to true.
			for (int l = 0; l < super.getRules().getSurvivalRules().length && survive == false; l++)
				if (aliveNeighbours == super.getRules().getSurvivalRules()[l])
					survive = true;

			//if it dies, then it is set to active, since it changed its state.
			if (!survive) {
				setCellState(x, y, BoardContainer.NEXTGENERATION, false);
				setCellState(x, y, BoardContainer.NEXTACTIVEGENERATION, true);
			} else {
				//if it remains alive, it is not added to the next active generation.
				setCellState(x, y, BoardContainer.NEXTGENERATION, true);
				setCellState(x, y, BoardContainer.NEXTACTIVEGENERATION, false);
			}

		}
	}
	/**
	 * 
	 * @param board the 2d long array representing the game board. Used for testing.
	 * @see BitGameBoardTest
	 */
	public void setBoard(long board[][]) {

		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[i].length; j++) {
				this.activeCells[i][j] = board[i][j];
				this.CurrGeneration[i][j] = board[i][j];
			}

	}

	public String toString() {
		int cellsInLong = 64;
		String bitString = "";

		for (int y = 0; y < (super.getHeight() / 64) + 1; y++) {

			if (y == (super.getHeight() / 64) + 1 - 1)
				cellsInLong = this.getHeight()%64;

			for (int k = 0; k < cellsInLong; k++) {
				for (int x = 0; x < super.getWidth(); x++) {
					bitString += ((this.CurrGeneration[x][y] >> k) & 1);
				}
			}
		}

		//
		return bitString;
	}


	

	

	/**
	 * Return the two dimensional array specified by the boardContainer enum.
	 * @param boardContainer the enum holding the predefined constants used to determine which array to be returned.
	 * @return two dimensional long array
	 */
	private long[][] selectArray(BoardContainer boardContainer) {

		
		long[][] cells = null;
		switch (boardContainer) {
		case CURRENTGENERATION:
			cells = this.CurrGeneration;
			break;
		case NEXTGENERATION:
			cells = this.nextGeneration;
			break;
		case ACTIVEGENERATION:
			cells = this.activeCells;
			break;
		case NEXTACTIVEGENERATION:
			cells = this.newActiveCells;
			break;

		}

		return cells;
	}
	
	/**
	 * Returns the state of the cell specified by the parameters, in the array specified by boardContainer
	 * 
	 * @param x the horizontal position of the cell
	 * @param y the vertical position of the cell
	 * @param boardContainer the enum that determines the array that holds the cell.
	 * 
	 */
	@Override
	public boolean getCellState(int x, int y, BoardContainer boardContainer) {
		long[][] cells = selectArray(boardContainer);
		/*
		 * since the vertical position of a cell is its bit position relative to the first bit in the inner long array
		 * we divide y by 64 to get which long element the cell is in, and we perform y%64 that gives us which bit position 
		 * the cell is in in the relevant long element. So we shifted the long value y%64 position to the right, so that
		 * the cell we are interested in is the rightmost bit of the bitmask. We then perform the bitwise & on the shifted bitmask
		 * and a long binary digit with 1 as the rightmost bit. If the if the bitmask has 1 as the rightmost bit(alive) then the & operation
		 * returns 1, but if it's 0(dead) it returns 0.
		 */
		return (cells[x][y / 64] >> y % 64 & 1L) == 1; 

	}

	/**
	 * Sets the state of the cell in the array specified by BoardContainer to the state specified by the boolean parameter
	 * If the cell being set is outside of the game board, then an IndexOutOFBoundException is thrown.
	 * @param horizontal position of the cell
	 * @param vertical position of the cell
	 * @throws IndexOutOfBoundException When the cell's position is outside the border of the game.
	 */
	@Override
	public void setCellState(int x, int y, BoardContainer bc, boolean alive) throws IndexOutOfBoundsException{
		long[][] cells = selectArray(bc);
		
		
		//checks if either the x or y position is outside of the border
		if(x < 0 || y < 0 || x >= super.getWidth() || y >= super.getHeight()){
			throw new IndexOutOfBoundsException();
		}
		else
		{
			if (alive)
				cells[x][y / 64] |= (1L << y % 64); //set alive
			else
				cells[x][y / 64] &= ~(1L << y % 64); //set dead
		}
		
		
	}

	/**
	 * Kills all elements to zero, killing every cell.
	 */
	@Override
	public void resetGameBoard() {
		for (int x = 0; x < super.getWidth(); x++) {
			for (int y = 0; y < (super.getHeight() / 64) + 1; y++) {
				this.CurrGeneration[x][y] = 0;
				this.nextGeneration[x][y] = 0;
				this.activeCells[x][y] = 0;
				this.newActiveCells[x][y] = 0;
			}
		}

	}

	
	@Override
	public void transferPattern(int startX, int startY) {
		super.setSettingPattern(false);
		RLEPattern pattern = super.getPattern();

		for (int x = 0; x < pattern.getWidth(); x++) {
			for (int y = 0; y < pattern.getHeight(); y++) {
				{
					setCellState(x + startX , y + startY ,BoardContainer.CURRENTGENERATION, pattern.getPattern()[x][y]);
					setCellState(x + startX , y + startY ,BoardContainer.ACTIVEGENERATION, true);

				}

			}
		}

	}


	/**
	 * This function is not yet supported
	 */
	@Override
	public void nextGenerationConcurrent() {
		nextGeneration();
		
	}

}