package GameOfLife;

import java.io.IOException;

import javafx.scene.paint.Color;

public class Matrix
{//test
    private long[][] CurrGeneration;
    private long[][] nextGeneration;
    private long[][] activeCells;
    private long[][] newActiveCells;
    private int patternWidth;
    private int patternHeight;
	private final int HEIGHT;
    private final int WIDTH;
    private int yDiv64;
    private int yMod64;
    private Color color = Color.web("#42dd50");
    private Rules rules;
    private RleInterpreter rle;

    public Matrix(int x, int y, Rules rules)
    {
	    this.rules = rules;
		this.HEIGHT = y;
		this.WIDTH = x;
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

   

    public void startNextGeneration()
    {

    	determineNextGeneration();


		for (int x = 0; x < this.WIDTH; x++)
		{
		    for (int y = 0; y < this.yDiv64; y++)
		    {
		    	this.getCurrentGeneration()[x][y] = this.getNextGeneration()[x][y];
		    	this.getActiveCells()[x][y] = this.getNewActiveCells()[x][y];
		    }
		}
    }
    
    public void setPatternFromRle(String rleString) throws IOException
    {
    	rle = new RleInterpreter(rleString);
    	
    	this.setPatternWidth(rle.getWidth());
    	this.setPatternHeight(rle.getHeight());
    	
    	for(int x = 0; x < rle.getWidth(); x++)
    	{
    		for(int y = 0; y < rle.getHeight(); y++)
    		{    				
    			if(cellIsAlive(x,y, rle.getStartGeneration()))
    			{
    				setCellState(x+20, y+20, this.CurrGeneration, true);
    				setCellState(x+20, y+20, this.activeCells, true);
    			}
    			else
    			{
    				setCellState(x+20, y+20, this.CurrGeneration, false);
    				setCellState(x+20, y+20, this.activeCells, true);
    			}   							
    		}
    		
    	}
    }
    
    private void determineNextGeneration()
    {
    	
	int aliveNeighbours;

	for (int x = 0; x < this.WIDTH; x++)
	{
	    for (int y = 0; y < this.HEIGHT; y++)
	    {
	
		if (!(this.getActiveCells()[x][y/64] == 0))
			if (cellIsAlive(x, y, this.activeCells))
			{
			    aliveNeighbours = countNeighbours(x, y, true);
			    setCellStateFromRules(x, y, aliveNeighbours);
			}
	    }
	}

    }
    
    public boolean cellIsAlive(int x, int y, long[][] cells)
    {
    	return ((cells[x][y/64] >> y%64) & 1) == 1;
    }
    
    public void setCellState(int x, int y, long[][] cells, boolean alive)
    {
    	if(alive)
    		cells[x][y/64] |= (1L << y%64);
    	else
    		cells[x][y/64] &= ~(1L << y%64);
    }
    
    public void swapCellState(int x, int y, long[][] cells)
    {
    	this.CurrGeneration[x][y/64] ^= (1L << y%64);
    }
    
    

    private int countNeighbours(int x, int y, boolean countNeighbors)
    {
	int aliveNeighbours = 0;

	if (x == 0 || x == this.WIDTH - 1 || (y == 0 && y%64 == 0) || ((y == this.yDiv64 - 1) && y%64 == this.yMod64)) // (1)
	{
		setCellState(x, y, this.nextGeneration, false);
		setCellState(x, y, this.newActiveCells, true);
	    return 1;
	} 
	else
	{

	    for (int i = -1; i <= 1; i++) // (2)
		for (int j = -1; j <= 1; j++)
		{
		    if (!(i == 0 && j == 0))// (3)
		    {			
	
			    if (cellIsAlive(x + i, y + j, this.CurrGeneration))
			    {
					aliveNeighbours++;
			    }
			    if (countNeighbors)
			    {
			    	setCellStateFromRules(x + i, y + j, countNeighbours(x + i, y + j, false));
				}

		    }
		 }

	}

	return aliveNeighbours;
    }

    private void setCellStateFromRules(int x, int y, int aliveNeighbours)
    {
	boolean alive = cellIsAlive(x, y, this.CurrGeneration); // (1)

	if (!alive)
	{
	    boolean birth = false;



	    for (int l = 0; l < rules.getBirthRules().length && birth == false; l++)// (2)
		if (aliveNeighbours == rules.getBirthRules()[l])
		    birth = true;

	    if (birth)
	    {
	    	setCellState(x, y, this.newActiveCells, true);
	    	setCellState(x, y, this.nextGeneration, true);
	    } 
	    else
	    {
	    	setCellState(x, y, this.nextGeneration, false);
	    	setCellState(x, y, this.newActiveCells, false);
	    }
	} else 
	{
	    boolean survive = false;


	    for (int l = 0; l < rules.getSurvivalRules().length && survive == false; l++)
		if (aliveNeighbours == rules.getSurvivalRules()[l])
		    survive = true;

	    if (!survive)
	    {
	    	setCellState(x, y, this.getNextGeneration(), false);
	    	setCellState(x, y, this.getNewActiveCells(), true);
	    }
	    else
	    {
	    	setCellState(x, y, this.getNextGeneration(), true);
	    	setCellState(x, y, this.getNewActiveCells(), false);
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
			for (int x = 0; x < this.WIDTH; x++)
			{
			    bitString += ((this.getCurrentGeneration()[x][y] >> k) & 1);
			}
	    }
	}

	//
	return bitString;
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

    public int getWidth()
    {
	return this.WIDTH;
    }

    public int getY()
    {
	return this.yDiv64;
    }

    public int getHeight()
    {
	return this.HEIGHT;
    }
    
    public int getPatternWidth() {
		return patternWidth;
	}

	public void setPatternWidth(int patternWidth) {
		this.patternWidth = patternWidth;
	}

	public int getPatternHeight() {
		return patternHeight;
	}

	public void setPatternHeight(int patternHeight) {
		this.patternHeight = patternHeight;
	}

}