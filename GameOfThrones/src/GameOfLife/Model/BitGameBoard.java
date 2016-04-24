package GameOfLife.Model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;

@Deprecated
public class BitGameBoard extends GameBoard {// test
	private long[][] CurrGeneration;
	private long[][] nextGeneration;
	private long[][] activeCells;
	private long[][] newActiveCells;
	public int yDiv64;
	private int yMod64;
	Map<String,boolean[][]>hmap = new HashMap <>();
	
	
	public BitGameBoard(int x, int y, Rules rules) {
		super(x, y, rules);
		
		this.yDiv64 = (y / 64) + 1;
		this.yMod64 = y % 64;
		CurrGeneration = new long[x][yDiv64];
		nextGeneration = new long[x][yDiv64];
		activeCells = new long[x][yDiv64];
		newActiveCells = new long[x][yDiv64];

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
	



	public void startNextGeneration() {
		determineNextGeneration();

		for (int x = 0; x < super.getWidth(); x++) {
			for (int y = 0; y < this.yDiv64; y++) {
				this.getCurrentGeneration()[x][y] = this.getNextGeneration()[x][y];
				this.getActiveCells()[x][y] = this.getNewActiveCells()[x][y];
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
						if (getCellState(x, j + bit, BoardContainer.ACTIVEGENERATION)) {
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

					if (getCellState(neighborX, neighborY, BoardContainer.CURRENTGENERATION)) {
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


	@Override
	public boolean getCellState(int x, int y, BoardContainer bc) {
		long[][]cells = null;
		
		switch(bc){
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
		
		return (cells[x][y/64] >> y%64 & 1) == 1;

		
		
	}
	
	private long[][] selectArray(BoardContainer bc){
		
		long[][]cells = null;
		switch(bc){
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
		long[][]cells = selectArray(bc);
		
		if(alive)
			cells[x][y/64] |= (1L << y % 64); 
		else
			cells[x][y/64] &= ~(1L << y % 64);
		}
		


	@Override
	public void resetGameBoard() {
		for(int x = 0; x < super.getWidth(); x++){
			for(int y = 0; y < super.getHeight(); y++){
				this.getCurrentGeneration()[x][y] = 0;
				this.getNextGeneration()[x][y] = 0;
				this.getActiveCells()[x][y] = 0;
				this.getNewActiveCells()[x][y] = 0;
			}
		}
		
	}





	@Override
	public void transferPattern(int startX, int startY) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void createPattern() {
		// TODO Auto-generated method stub
		
	}





	@Override
	public int countNeighbours(int x, int y) {
		// TODO Auto-generated method stub
		return 0;
	}

}