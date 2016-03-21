package GameOfLife;

import java.util.Arrays;

import javafx.scene.paint.Color;

public class Matrix
{

    private long[][] CurrGeneration;
    private long[][] nextGeneration;
    private long[][] activeCells;
    private long[][] newActiveCells;
    private int y;
    private int x;
    private int yDiv64;
    private int yMod64;
    private Color color = Color.web("#42dd50");
    private Rules rules;

    public Matrix(int x, int y, Rules rules)
    {
	    this.rules = rules;
		this.y = y;
		this.x = x;
		this.yDiv64 = (y / 64) + 1;
		this.yMod64 = y % 64;

		CurrGeneration = new long[x][yDiv64];
		nextGeneration = new long[x][yDiv64];
		activeCells = new long[x][yDiv64];
		newActiveCells = new long[x][yDiv64];
    }

    public void setBoard(long board[][])
    {	
	
	for(int i = 0; i < board.length;i++)
	    for(int j = 0; j < board[i].length;j++)
	    {
		this.getActiveCells()[i][j] = board[i][j];
		this.getCurrentGeneration()[i][j] = board[i][j];
	    }
	    
    }

    public long[][] getActiveCells()
    {
	return activeCells;
    }

    public void setActiveCells(long[][] activeCells)
    {
	this.activeCells = activeCells;
    }

    public long[][] getNewActiveCells()
    {
	return newActiveCells;
    }

    public void setNewActiveCells(long[][] newActiveCells)
    {
	this.newActiveCells = newActiveCells;
    }

    public void setColor(Color color)
    {
	this.color = color;
    }

    public Color getColor()
    {
	return this.color;
    }

    public int getX()
    {
	return this.x;
    }

    public int getY()
    {
	return this.yDiv64;
    }

    public int getRealY()
    {
	return this.y;
    }

    public void startNextGeneration()
    {

	determineNextGeneration();


	//current blir next generation;
	//active cells blir nye active cells;
	for (int x = 0; x < this.x; x++)
	{
	    for (int y = 0; y < this.yDiv64; y++)
	    {
		this.getCurrentGeneration()[x][y] = this.getNextGeneration()[x][y];
		this.getActiveCells()[x][y] = this.getNewActiveCells()[x][y];
	    }
	};
    }

    /*
     * The GOL algorithm:
     *
     * A cell becomes active in two ways: 1. the cell is clicked by the user, 2.
     * The cell changed state in the previous call to determineNextGeneration()
     * We only need to check the active cells and its neighbors in order to
     * determine the next generation(next game board). The nested for loop in
     * determineNextGeneration() Loops the activeCells array to determine
     * whether each cell is alive or not. bit = 1 means active, and bit = 0
     * inactive. Since the cells in each long represents 64 cells going in the
     * vertical direction, we can simply check if each long equals zero. If the
     * long equals zero, then we know 64 of the cells are inactive, if not, then
     * we loop through every bit in the long value(same principle applies in the
     * nextGeneration method).
     * 
     * /* We need to be able to support an array of any size, but since we're
     * working with bit arrays it gets trickier(the vertical length of the board
     * becomes a multiple of 64) In the nested for loop below, we loop through
     * the array going column by column. When the cell height of the board is
     * not divisible by 64(e.g. 65, 12 125) the if sentence below the (j) for
     * loop ensures that we don't exceed the game board.
     * 
     * the if statement: if (((this.getActiveCells()[i][j] >> k) & 1L) == 1),
     * determines which cell is active. The left side of the boolean expression
     * is the long value that represents 64 cells. We get the value of every bit
     * by using two bitwise operations and an equality test. By shifting the
     * long pattern k positions to the right, the bit at position k will be the
     * rightmost bit(LSB). throughout the loop every bit will be the rightmost
     * bit once. We perform the logical AND operation on the bitshifted long
     * valued and a long equal to 1
     * 
     * If the cell is active(state was changed in the previous generation), then
     * we call countNeightbours(). We pass true as an argument because we also
     * want to count its neighbors(since the cell was changed last generation,
     * the change will affect all of the cells' neighbors by either making them
     * gain or lose one neighbor. We only need to change the neighbors of these
     * cells because the other cells will remain the same(their neighborhood
     * doesn't change).
     * 
     * 
     */

    private void determineNextGeneration()
    {
	int aliveNeighbours;
	int cellsInLong = 64;

	for (int x = 0; x < this.x; x++)
	{
	    for (int y = 0; y < this.yDiv64; y++)
	    {
		if (y == this.yDiv64 - 1)
		    cellsInLong = yMod64;
		else
		    cellsInLong = 64;

		if (!(this.getActiveCells()[x][y] == 0))
		    for (int bitPos = 0; bitPos < cellsInLong; bitPos++)
		    {
			if (((this.getActiveCells()[x][y] >> bitPos) & 1L) == 1)
			{
			    aliveNeighbours = countNeighbours(x, y, bitPos, true);
			    setCellState(x, y, bitPos, aliveNeighbours);
			}

		    }

	    }
	}

    }

    // (1) checks if the cell is on the border.
    // if it isnt then we start a nested for loop to count the neighbors
    // the i variables determines the horizontal position of the neighbors and
    // the j the vertical
    //
    // (3) ensures that we don't count the cell itself.

    /*
     * (4) checks if the bit is at the first position in the long AND y = 1. if
     * that is the case then we check the last bit of the long that comes before
     * it. (5) we shift 63 positions to the right to get the bit we want. if the
     * bit is one then we increment aliveNeighbours (6) since the method is
     * called from determineNextGeneration(), countNeighbours will be true, So
     * we make a "recursive call" to countNeighbours again to with false passed
     * as an argument(So we won't call countNeighbbors on that cells neighbors
     * as well) when the call returns we pass it as an argument to the setCell
     * method, that adds it to the currentGeneration array if it satisfies the
     * rules. this happens for every neighbor, and finally we call the setCell
     * method on the cell we originally counted
     * 
     * 
     */
    private int countNeighbours(int x, int y, int bitPos, boolean countNeighbors)
    {
	int aliveNeighbours = 0;

	if (x == 0 || x == this.x - 1 || (y == 0 && bitPos == 0) || ((y == this.yDiv64 - 1) && bitPos == this.yMod64)) // (1)
	{
	    this.getNextGeneration()[x][y] &= ~(1L << bitPos); // kill bit
	    this.getNewActiveCells()[x][y] &= ~(1L << bitPos); // make active
	    return 1;
	} else // not on the border
	{

	    for (int i = -1; i <= 1; i++) // (2)
		for (int j = -1; j <= 1; j++)
		{
		    if (!(i == 0 && j == 0))// (3)
		    {	
			if (j == -1 && bitPos == 0) // (4)
			{

			    if (((this.getCurrentGeneration()[x + i][y - 1] >> (63)) & 1L) == 1) // (5)
			    {
				aliveNeighbours++;
			    }
			    if (countNeighbors)// (6)
			    {
				setCellState(i + x, y - 1, 63, countNeighbours((i + x), y - 1, (63), false));
			    }
			} else if (j == 1 && bitPos == 63)
			{

			    if (((this.getCurrentGeneration()[x + i][y + 1] >> (0)) & 1L) == 1)
			    {
				aliveNeighbours++;
			    }
			    if (countNeighbors)
			    {
				setCellState(i + x, y + 1, 0, countNeighbours((i + x), y + 1, (0), false));
			    }

			} else
			{

		    if (((this.getCurrentGeneration()[i + x][y] >> (j + bitPos)) & 1L) == 1)
			    {
				aliveNeighbours++;
			    }
			    if (countNeighbors)
			    {
				setCellState(i + x, y, j + bitPos, countNeighbours((i + x), y, (j + bitPos), false));
			    }

			}
		    }
		}

	}

	return aliveNeighbours;

    }

    // set cell checks if the cell should be dead or alive
    // (1) checks if alive
    // Case 1: cell is alive
    // if cell satisfies the birth rules it will be added to the active cells
    // and the next generation(bit set to 1)
    // if not it will be set as inactive
    // Case 2: cell is dead
    // see case 1
    /*
     * 
     */
    private void setCellState(int x, int y, int bitPos, int aliveNeighbours)
    {
	boolean alive = (((this.getCurrentGeneration()[x][y] >> bitPos) & 1) == 1); // (1)

	if (!alive) // if cell is dead
	{
	    boolean birth = false;

	    for (int l = 0; l < rules.getBirthRules().length && birth == false; l++)// (2)
		if (aliveNeighbours == rules.getBirthRules()[l])
		    birth = true;

	    if (birth)
	    {
		this.getNewActiveCells()[x][y] |= (1L << bitPos); // set active
		this.getNextGeneration()[x][y] |= (1L << bitPos); // set alive
	    } else
	    {
		this.getNewActiveCells()[x][y] &= ~(1L << bitPos); // set
								   // inactive
		// this.getActiveCells()[x][y] &= ~(1L << bitPos); //set as
		// inactive so it wont be counter again
	    }
	} else // if cell is alive
	{
	    boolean survive = false;

	    for (int l = 0; l < rules.getSurvivalRules().length && survive == false; l++)
		if (aliveNeighbours == rules.getSurvivalRules()[l])
		    survive = true;

	    if (!survive)
	    {
		this.getNextGeneration()[x][y] &= ~(1L << bitPos); // set alive
		this.getNewActiveCells()[x][y] |= (1L << bitPos); // set active
	    }else
	    {
		this.getNextGeneration()[x][y] |= (1L << bitPos);
		this.getNewActiveCells()[x][y] &= ~(1L << bitPos); // set
								   // inactive

	    }

	}
    }

    public void setCurrentGeneration(long[][] currGen)
    {
	this.CurrGeneration = currGen;
    }

    public long[][] getCurrentGeneration()
    {
	return this.CurrGeneration;
    }

    public long[][] getNextGeneration()
    {
	return this.nextGeneration;
    }

    public String toString()
    {
	int cellsInLong = 64;
	String bitString = "";

	for (int y = 0; y < this.yDiv64; y++)
	{

	    if (y == this.yDiv64 - 1)
		cellsInLong = this.yMod64;
	    else
		cellsInLong = 64;

	    for (int k = 0; k < cellsInLong; k++)
	    {
		for (int x = 0; x < this.x; x++)
		{
		    bitString += ((this.getCurrentGeneration()[x][y] >> k) & 1);
		}
	    }
	}

	//
	return bitString;
    }

}
