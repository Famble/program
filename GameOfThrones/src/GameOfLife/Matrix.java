package GameOfLife;

import java.io.IOException;

import javafx.scene.paint.Color;

public class Matrix {// test
	private long[][] CurrGeneration;
	private long[][] nextGeneration;
	private long[][] activeCells;
	private long[][] newActiveCells;
	private int patternWidth;
	private int patternHeight;
	private final int HEIGHT;
	private final int WIDTH;
	public int yDiv64;
	private int yMod64;
	private Color color = Color.web("#42dd50");
	private Rules rules;
	private long[][] pattern;
	boolean settingPattern = false;

	public Matrix(int x, int y, Rules rules) {
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

	public void setPattern(long[][] pattern) {
		settingPattern = true;
		this.pattern = pattern;
	}

	public long[][] getPattern() {
		return this.pattern;
	}

	public boolean cellIsAlive(int x, int y, long[][] cells) {
		return ((cells[x][y / 64] >> y % 64) & 1) == 1;
	}

	public void setCellState(int x, int y, long[][] cells, boolean alive) {
		if (alive)
			cells[x][y / 64] |= (1L << y % 64);
		else
			cells[x][y / 64] &= ~(1L << y % 64);
	}

	public void swapCellState(int x, int y, long[][] cells) {
		this.CurrGeneration[x][y / 64] ^= (1L << y % 64);
	}

	public void startNextGeneration() {
		determineNextGeneration();

		for (int x = 0; x < this.WIDTH; x++) {
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
				if (cellIsAlive(x, y, this.pattern)) {
					setCellState(x+startX, y+startY, this.CurrGeneration, true);
					setCellState(x, y, this.activeCells, true);
				} else {
					setCellState(x+startX, y+startY, this.CurrGeneration, false);
					setCellState(x+startX, y+startY, this.activeCells, true);
				}
			}

		}
		
		
		
		
	}

	private void determineNextGeneration() {

		int aliveNeighbours;

		for (int x = 0; x < this.WIDTH; x++) {
			for (int y = 0; y < this.yDiv64; y++) {

				if (!(this.getActiveCells()[x][y] == 0)) {
					int j = y*64;
					for(int bit = 0; bit<64; bit++)
					{					
						if (cellIsAlive(x, j+bit, this.activeCells)) {
							aliveNeighbours = countNeighbours(x, j+bit, true);
							setCellStateFromRules(x, j+bit, aliveNeighbours);
						}
					}
				}
			}
		}

	}

	private int countNeighbours(int x, int y, boolean countNeighbors) {
		int aliveNeighbours = 0;
		int neighborX;
		int neighborY;

		for (int i = -1; i <= 1; i++) // (2)
			for (int j = -1; j <= 1; j++) {
				if (!(i == 0 && j == 0))// (3)
				{
					neighborX = x + i;
					neighborY = y + j;

					if (neighborX == this.WIDTH) {
						neighborX %= this.WIDTH;
					} else if (neighborX == -1) {
						neighborX = this.WIDTH - 1;
					}
					if (neighborY == this.HEIGHT) {
						neighborY %= this.HEIGHT;
					} else if (neighborY == -1) {
						neighborY = this.HEIGHT - 1;

					}

					if (cellIsAlive(neighborX, neighborY, this.CurrGeneration)) {
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
		boolean alive = cellIsAlive(x, y, this.CurrGeneration); // (1)

		if (!alive) {
			boolean birth = false;

			for (int l = 0; l < rules.getBirthRules().length && birth == false; l++)// (2)
				if (aliveNeighbours == rules.getBirthRules()[l])
					birth = true;

			setCellState(x, y, this.newActiveCells, birth);
			setCellState(x, y, this.nextGeneration, birth);
			
		} else {
			boolean survive = false;

			for (int l = 0; l < rules.getSurvivalRules().length && survive == false; l++)
				if (aliveNeighbours == rules.getSurvivalRules()[l])
					survive = true;

		
			setCellState(x, y, this.getNextGeneration(), survive);
			setCellState(x, y, this.getNewActiveCells(), !survive);
			
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
				for (int x = 0; x < this.WIDTH; x++) {
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

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return this.color;
	}

	public int getWidth() {
		return this.WIDTH;
	}

	public int getY() {
		return this.yDiv64;
	}

	public int getHeight() {
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