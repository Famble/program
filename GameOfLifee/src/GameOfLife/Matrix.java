package GameOfLife;

import java.util.Arrays;

import javafx.scene.paint.Color;

public class Matrix
{

    private long[][] CurrGeneration;
    private long[][] newGeneration;
    private long[][] activeCells; // is 0 if cell is active and 1 if its
				  // inactive(every cell starts as active)!
    private long[][] newActiveCells; // is 0 if cell is active and 1 if its
				     // inactive(every cell starts as active)!
    private int y;
    private int x;
    private int yDiv64;
    private int yMod64;
    private Color color = Color.web("#42dd50");
    private Rules rules;
    private int aliveCells = 0;

    public Matrix(int x, int y, Rules rules)
    {
	this.y = y;
	this.rules = rules;
	this.x = x;
	this.yDiv64 = (y / 64) + 1;
	this.yMod64 = y % 64;

	CurrGeneration = new long[x][yDiv64];
	newGeneration = new long[x][yDiv64];
	activeCells = new long[x][yDiv64];
	newActiveCells = new long[x][yDiv64];
    }

    public void setBoard(long board[][])
    {
	activeCells = board;
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

	for (int i = 0; i < this.x; i++)
	{
	    for (int j = 0; j < this.yDiv64; j++)
	    {
		this.getCurrentGeneration()[i][j] = this.getNewGeneration()[i][j];
		this.getActiveCells()[i][j] = this.getNewActiveCells()[i][j];
	    }
	}

    }

   


    /* The GOL algorithm:
     *
     * A cell becomes active in two ways: 1. the cell is clicked by the user, 2. The cell changed state in the previous call to determineNextGeneration()
     * We only need to check the active cells and its neighbors in order to determine the next generation(next game board). The nested for loop in determineNextGeneration()
     * Loops the activeCells array to determine whether each cell is alive or not. bit = 1 means active, and bit = 0 inactive. 
     * Since the cells in each long represents 64 cells going in the vertical direction, we can simply check if each long equals zero.
     * If the long equals zero, then we know 64 of the cells are inactive, if not, then we loop through every bit in the long value(same principle applies in the nextGeneration method).
     * 
     * 
    */private void determineNextGeneration()
    {
	int aliveNeighbours;
	int cellsInLong = 64;

	/*
	We need to be able to support an array of any size, but since we're working with bit arrays it gets trickier(the vertical length of the board becomes a multiple of 64)
	In the nested for loop below, we loop through the array going column by column. When the cell height of the board is not divisible by 64(e.g. 65, 12 125) 
	the if sentence below the (j) for loop ensures that we don't exceed the game board.
	
	the if statement: if (((this.getActiveCells()[i][j] >> k) & 1L) == 1), determines which cell is active. The left side of the boolean expression is the long value
	that represents 64 cells. We get the value of every bit by using two bitwise operations and an equality test. 
	By shifting the long pattern k positions to the right, the  bit at position k will be the rightmost bit(LSB). 
	throughout the loop every bit will be the rightmost bit once. We perform the logical AND operation on the bitshifted long valued and a
	long equal to 1. Since the long equal to 1 has a pattern with only zeros followed by one bit
	
	
	
	*/
	

	for (int i = 0; i < this.x; i++)
	{
	    for (int j = 0; j < this.yDiv64; j++)
	    {
		if (j == this.yDiv64 - 1) 
		    cellsInLong = yMod64;
		else
		    cellsInLong = 64;

		if (!(this.getActiveCells()[i][j] == 0))// if long is zero then
							// no need to check
		    for (int k = 0; k < cellsInLong; k++)
		    {
			if (((this.getActiveCells()[i][j] >> k) & 1L) == 1)
			{
			    aliveNeighbours = countNeighbours(i, j, k, true);
			    setCell(i, j, k, aliveNeighbours);
			}

		    }

	    }
	}

    }
   
    private int countNeighbours(int i, int j, int k, boolean checkForInactive)
    {
	int aliveNeighbours = 0;

	if (i == 0 || i == this.x - 1 || (j == 0 && k == 0) || ((j == this.yDiv64 - 1) && k == this.yMod64)) // // border
	{
	    this.getNewGeneration()[i][j] &= ~(1L << k); // kill bit
	    this.getNewActiveCells()[i][j] |= (1L << k); // make active
	    return 0;
	} else // not on the border
	{

	    for (int x = -1; x <= 1; x++)
		for (int y = -1; y <= 1; y++)
		{
		    if (y == -1 && k == 0)
		    {

			if (((this.getCurrentGeneration()[i + x][j - 1] >> (63)) & 1L) == 1)
			{
			    aliveNeighbours++;
			}
			if (checkForInactive)
			{
			    // finds the amount of nieghbours to the cell and
			    // changes the state of the cell if the rules are
			    // satisfied
			    setCell(i + x, j - 1, 63, countNeighbours((i + x), j - 1, (63), false));
			}
		    } else if (y == 1 && k == 63)
		    {

			if (((this.getCurrentGeneration()[i + x][j + 1] >> (0)) & 1L) == 1)
			{
			    aliveNeighbours++;
			}
			if (checkForInactive)
			{
			    // finds the amount of neighbours to the cell and
			    // changes the state of the cell if the rules are
			    // satisfied
			    setCell(i + x, j + 1, 0, countNeighbours((i + x), j + 1, (0), false));
			}

		    } else
		    {
			if (!(x == 0 && y == 0))
			{
			    if (((this.getCurrentGeneration()[i + x][j] >> (y + k)) & 1L) == 1)
			    {
				aliveNeighbours++;
			    }
			    if (checkForInactive)
			    {
				// finds the amount of neighbours to the cell
				// and changes the state of the cell if the
				// rules are satisfied
				setCell(i + x, j, y + k, countNeighbours((i + x), j, (y + k), false));
			    }
			}
		    }
		}

	}

	return aliveNeighbours;

    }

    // is 0 if cell is active and 1 if its inactive(every cell starts as
    // active)!
    private void setCell(int i, int j, int k, int aliveNeighbours)
    {
	boolean alive = (((this.getCurrentGeneration()[i][j] >> k) & 1) == 1);

	if (!alive) // if cell is dead
	{

	    boolean birth = false;

	    for (int l = 0; l < rules.getBirthRules().length && birth == false; l++)
		if (aliveNeighbours == rules.getBirthRules()[l])
		    birth = true;

	    if (birth)
	    {
		aliveCells++;
		this.getNewActiveCells()[i][j] |= (1L << k); // make
		// to 0)
		this.getNewGeneration()[i][j] |= (1L << k);
	    } else
	    {
		this.getNewActiveCells()[i][j] &= ~(1L << k); // add as
							      // inactive
		this.getNewGeneration()[i][j] &= ~(1L << k);
	    }
	} else // if cell is alive
	{
	    boolean survive = false;

	    for (int l = 0; l < rules.getSurvivalRules().length && survive == false; l++)
		if (aliveNeighbours == rules.getSurvivalRules()[l])
		    survive = true;

	    if (!survive)
	    {
		aliveCells--;
		this.getNewGeneration()[i][j] &= ~(1L << k); // kill bit*/
		this.getNewActiveCells()[i][j] |= (1L << k); // add to active
	    } else
	    {
		this.getNewGeneration()[i][j] |= (1L << k);
		this.getNewActiveCells()[i][j] &= ~(1L << k); // add as
							      // inactive

	    }

	}
    }

    public int getAliveCells()
    {
        return aliveCells;
    }

    public void setAliveCells(int aliveCells)
    {
        this.aliveCells = aliveCells;
    }

    public long[][] getCurrentGeneration()
    {
	return this.CurrGeneration;
    }

    public long[][] getNewGeneration()
    {
	return this.newGeneration;
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
		    // System.out.printf("x, y, k: %d,%d,%d\n", x, y, k);
		    bitString += ((this.getCurrentGeneration()[x][y] >> k) & 1);
		}
	    }
	}

	//
	return bitString;
    }

}
