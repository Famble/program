package GameOfLife.Model;

import java.io.IOException;

import javafx.scene.paint.Color;

public class StaticMatrix extends Matrix {// test
	private long[][] CurrGeneration;
	private long[][] nextGeneration;
	private long[][] activeCells;
	private long[][] newActiveCells;
	private int patternWidth;
	private int patternHeight;
	public int yDiv64;
	private int yMod64;
	private long[][] pattern;
	public boolean settingPattern = false;

	public StaticMatrix(int x, int y, Rules rules) {
		super(x, y, rules);
		
		this.yDiv64 = (y / 64) + 1;
		this.yMod64 = y % 64;
		CurrGeneration = new long[x][yDiv64];
		nextGeneration = new long[x][yDiv64];
		activeCells = new long[x][yDiv64];
		newActiveCells = new long[x][yDiv64];
	}

	public void setPattern(long[][] pattern) {
		settingPattern = true;
		this.pattern = pattern;
	}

	public long[][] getPattern() {
		return this.pattern;
	}
	
	public void setActiveCellState(int x, int y, boolean alive)
	{
		if(alive)
			this.getActiveCells()[x][y/64] |= (1L << y % 64);
		else
			this.getActiveCells()[x][y/64] &= ~(1L << y % 64);

	}
	
	public void setNextGenActiveCellState(int x, int y, boolean alive)
	{
		if(alive)
			this.getNewActiveCells()[x][y/64] |= (1L << y % 64);
		else
			this.getNewActiveCells()[x][y/64] &= ~(1L << y % 64);

	}
	
	public boolean getActiveCellState(int x, int y)
	{
		return (this.getActiveCells()[x][y/64] >> y%64 & 1) == 1;
	}

	public boolean getCellState(int x, int y) {
		return ((this.getCurrentGeneration()[x][y / 64] >> y % 64) & 1) == 1;
	}
	
	public boolean getPatternCellState(int x, int y) {
		return ((this.getCurrentGeneration()[x][y / 64] >> y % 64) & 1) == 1;
	}

	public void setCellState(int x, int y, boolean alive) {
		if (alive){			
			if(this.getCellState(x, y)){
				this.getCurrentGeneration()[x][y / 64] |= (1L << y % 64);
				this.setActiveCellState(x, y, false);
			}
			else
			{
				this.getCurrentGeneration()[x][y / 64] |= (1L << y % 64);
				this.setActiveCellState(x, y, true);

			}
		}
		else //dead
		{
			
			if(this.getCellState(x, y)){
				this.getCurrentGeneration()[x][y / 64] &= ~(1L << y % 64);
				this.setActiveCellState(x, y, true);
			}
			else{
				this.getCurrentGeneration()[x][y / 64] &= ~(1L << y % 64);
				this.setActiveCellState(x, y, false);
			}
		}
	}
	
	public void setNextGenCellState(int x, int y, boolean alive) {
		if (alive){			
			if(this.getCellState(x, y)){
				this.getNextGeneration()[x][y / 64] |= (1L << y % 64);
				this.setNextGenActiveCellState(x, y, false);
			}
			else
			{
				this.getNextGeneration()[x][y / 64] |= (1L << y % 64);
				this.setNextGenActiveCellState(x, y, true);

			}
		}
		else //dead
		{
			
			if(this.getCellState(x, y)){
				this.getNextGeneration()[x][y / 64] &= ~(1L << y % 64);
				this.setNextGenActiveCellState(x, y, true);
			}
			else{
				this.getNextGeneration()[x][y / 64] &= ~(1L << y % 64);
				this.setNextGenActiveCellState(x, y, false);
			}
		}
	}

	public void startNextGeneration() {
		determineNextGeneration();

		for (int x = 0; x < super.getWidth(); x++) {
			for (int y = 0; y < this.yDiv64; y++) {
				this.getCurrentGeneration()[x][y] = this.getNextGeneration()[x][y];
				this.getActiveCells()[x][y] = this.getNewActiveCells()[x][y];
			}
		}
	}

	public void transferPattern(int startX, int startY) throws IOException {
		settingPattern = false;
		for (int x = 0; x < patternWidth; x++) {
			for (int y = 0; y < patternHeight; y++) {
				if (getPatternCellState(x, y)) {
					setCellState(x+startX, y+startY, true);
				} else {
					setCellState(x+startX, y+startY, false);

				}
			}

		}
		
		
		
		
	}

	public void determineNextGeneration() {

		int aliveNeighbours;

		for (int x = 0; x < super.getHeight(); x++) {
			for (int y = 0; y < this.yDiv64; y++) {

				if (!(this.getActiveCells()[x][y] == 0)) {
					int j = y*64;
					for(int bit = 0; bit<64; bit++)
					{			
						if (getActiveCellState(x, j + bit)) {
							aliveNeighbours = countNeighbours(x, j+bit, true);
							setCellStateFromRules(x, j+bit, aliveNeighbours);
						}
					}
				}
			}
		}

	}

	public int countNeighbours(int x, int y, boolean countNeighbors) {
		int aliveNeighbours = 0;
		int neighborX;
		int neighborY;

		for (int i = -1; i <= 1; i++) // (2)
			for (int j = -1; j <= 1; j++) {
				if (!(i == 0 && j == 0))// (3)
				{
					neighborX = x + i;
					neighborY = y + j;

					if (neighborX == super.getWidth()) {
						neighborX %= super.getWidth();
					} else if (neighborX == -1) {
						neighborX = super.getWidth() - 1;
					}
					if (neighborY == super.getHeight()) {
						neighborY %= super.getHeight();
					} else if (neighborY == -1) {
						neighborY = super.getHeight() - 1;

					}

					if (getCellState(neighborX, neighborY)) {
						aliveNeighbours++;
					}
					if (countNeighbors) {
						setCellStateFromRules(neighborX, neighborY, countNeighbours(neighborX, neighborY, false));
					}

				}
			}

		return aliveNeighbours;
	}

	private void setCellStateFromRules(int x, int y, int aliveNeighbours) {
		boolean alive = getCellState(x, y); // (1)

		if (!alive) {
			boolean birth = false;

			for (int l = 0; l < super.getRules().getBirthRules().length && birth == false; l++)// (2)
				if (aliveNeighbours == super.getRules().getBirthRules()[l])
					birth = true;

			setNextGenCellState(x, y, birth);
			
		} else {
			boolean survive = false;

			for (int l = 0; l < super.getRules().getSurvivalRules().length && survive == false; l++)
				if (aliveNeighbours == super.getRules().getSurvivalRules()[l])
					survive = true;

		
			setNextGenCellState(x, y, survive);
			
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
			else
				cellsInLong = 64;

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

	@Override
	public boolean getCellState(int x, int y, BoardContainer bc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCellState(int x, int y, BoardContainer bc, boolean alive) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetGameBoard() {
		// TODO Auto-generated method stub
		
	}

}