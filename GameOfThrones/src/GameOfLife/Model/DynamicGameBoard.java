package GameOfLife.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DynamicGameBoard extends GameBoard implements Cloneable {

	public enum ExpansionDirection{
		LEFT, RIGHT, TOP, BOTTOM
	}

	private ArrayList<ArrayList<Boolean>> currGeneration;
	private ArrayList<ArrayList<Boolean>> nextGeneration;
	private ArrayList<ArrayList<Boolean>> activeCells;
	private ArrayList<ArrayList<Boolean>> nextActiveCells;
	private int shiftedRightwards = 0;
	private int expansionPadding = 0; //extra rows or columns added whenever an expansion occurs;
	private int shiftedDownwards = 0;
	private int boardSector = -1;
	private final int CPUCORES;
	int itHappens = 0;
	int expansionCheckCounter = 0;
	public ArrayList<Thread> workers = new ArrayList<Thread>();
	
	
	@Override
	public String toString(){
		String currentGen = "";
		
		for(int y = -shiftedDownwards; y < super.getHeight()-shiftedDownwards; y++){
			for(int x = -shiftedRightwards; x < super.getWidth()-shiftedRightwards; x++){
				if(getCellState(x, y, BoardContainer.CURRENTGENERATION))
					currentGen += "1";
				else
					currentGen += "0";
					
			}
		}	
		return currentGen;
	}
	
	
	public DynamicGameBoard(int x, int y, Rules rules) {
		super(x, y, rules);
		
		CPUCORES = Runtime.getRuntime().availableProcessors();

		currGeneration = new ArrayList<ArrayList<Boolean>>(x);
		nextGeneration = new ArrayList<ArrayList<Boolean>>(x);
		activeCells = new ArrayList<ArrayList<Boolean>>(x);
		nextActiveCells = new ArrayList<ArrayList<Boolean>>(x);

		for (int i = 0; i < super.getWidth(); i++) {
			currGeneration.add(new ArrayList<Boolean>(y));
			nextGeneration.add(new ArrayList<Boolean>(y));
			activeCells.add(new ArrayList<Boolean>(y));
			nextActiveCells.add(new ArrayList<Boolean>(y));

			for (int j = 0; j < super.getHeight(); j++) {
				currGeneration.get(i).add(j, false);
				nextGeneration.get(i).add(j, false);
				activeCells.get(i).add(j, false);
				nextActiveCells.get(i).add(j, false);
			}
		}
	}

	public void nextGenerationConcurrent() {

		long start = System.currentTimeMillis();

		
		
		
		createWorkers();
		
		/*
		try {
			runWorkers();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		workers.clear();
		
		*/
		
		boardSector = -1;
		//nextGenerationPrintPerformance(start, System.currentTimeMillis());
	//	determineNextGenerationConcurrent(0, super.getWidth());
	
		for (int i = 0; i < super.getWidth(); i++) {
			for (int j = 0; j < super.getHeight(); j++) {
				this.getCurrGeneration().get(i).set(j, this.getNextGeneration().get(i).get(j));
				this.getActiveCells().get(i).set(j, this.getNextActiveCells().get(i).get(j));

			}
		}
	}
	
	@Override
	public void createPattern() {

	}

	public void createWorkers() {
		
		for (int i = 0; i < CPUCORES; i++) {
			determineNextGenerationConcurrent(i);

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
	public void determineNextGenerationConcurrent(int sector) {
		int aliveNeighbors;
		int start = 0;
		int end = 0;
		int widthOfSector = super.getWidth()/CPUCORES;
		if(sector == CPUCORES -1){
			//start = widthOfSector * sector;
			//end = super.getWidth();
			
			for (int x = super.getWidth()/CPUCORES*sector-shiftedRightwards; x < super.getWidth()-shiftedRightwards; x++) {
				for (int y = -shiftedDownwards; y < super.getHeight() - shiftedDownwards; y++) {
					if (this.getCellState(x, y, BoardContainer.ACTIVEGENERATION)) {
						for (int i = -1; i <= 1; i++)
							for (int j = -1; j <= 1; j++) {
								int xToCheck = x + i;
								int yToCheck = y + j;
								aliveNeighbors = countNeighbours(xToCheck, yToCheck);
								setCellStateFromRules(xToCheck, yToCheck, aliveNeighbors);
							}

					}
				}
			}
		}
		else
		{
	
			
			for (int x = super.getWidth()/CPUCORES*sector-shiftedRightwards; x < super.getWidth()/CPUCORES*(sector+1)-shiftedRightwards; x++) {
				for (int y = -shiftedDownwards; y < super.getHeight() - shiftedDownwards; y++) {
					if (this.getCellState(x, y, BoardContainer.ACTIVEGENERATION)) {
						for (int i = -1; i <= 1; i++)
							for (int j = -1; j <= 1; j++) {
								int xToCheck = x + i;
								int yToCheck = y + j;
								aliveNeighbors = countNeighbours(xToCheck, yToCheck);
								setCellStateFromRules(xToCheck, yToCheck, aliveNeighbors);
							}

					}
				}
			}
		}
		
		
		

	}

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
				setCellState(x, y, BoardContainer.NEXTACTIVEGENERATION, true);
			} else {
				setCellState(x, y, BoardContainer.NEXTGENERATION, true);
				setCellState(x, y, BoardContainer.NEXTACTIVEGENERATION, false);
			}
		} else {
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
		}
	}

	//assumes board has the same dimensions as gameboard
	public <T>void setBoard(boolean[][] element) {
		boolean[][] board = element;
		for(int x = 0; x < super.getWidth(); x++){
			for(int y = 0; y < super.getHeight(); y++){
				if(board[x][y]){
					setCellState(x-shiftedRightwards, y-shiftedRightwards, BoardContainer.CURRENTGENERATION, true);
					setCellState(x-shiftedDownwards, y-shiftedDownwards, BoardContainer.ACTIVEGENERATION, true);
					
				}
			}
		}
	}

	@Override
	public void setCellState(int x, int y, BoardContainer container, boolean alive) {

		if (x + shiftedRightwards >= this.getWidth()) {
			extendBorderFromRight(( 1 + x + shiftedRightwards)-this.getWidth());
		}
		
		if (x + shiftedRightwards < 0) {
			extendBorderFromLeft(Math.abs(x+shiftedRightwards));
		}

		if (y + shiftedDownwards < 0) {
			extendBorderFromTop(Math.abs(y + shiftedDownwards));
		}

		if (y + shiftedDownwards >= this.getHeight()) {
			extendBorderFromBottom((1 + y + shiftedDownwards) - this.getHeight());
		}

		ArrayList<ArrayList<Boolean>> cells = getArrayList(container);

		cells.get(x + shiftedRightwards).set(y + shiftedDownwards, alive);
	}

	public void nextGenerationPrintPerformance(long start, long end) {
		//System.out.printf("Time elapsed(ms): %d)\n", (end - start));
	}

	public void nextGenerationConcurrentPrintPerformance() {
	}

	@Override
	public boolean getCellState(int x, int y, BoardContainer container) {
		{
			if (x + shiftedRightwards >= this.getWidth()) {
				return false; //hvis du er utenfor brettet er den alltid false;
			}
			
			if (x + shiftedRightwards < 0) {
				return false;
			}


			if (y + shiftedDownwards < 0) {
				return false;
			}

			if (y + shiftedDownwards >= this.getHeight()) {
				return false;
			}

			ArrayList<ArrayList<Boolean>> cells = getArrayList(container);

			return cells.get(x + shiftedRightwards).get(y + shiftedDownwards);

		}
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

	public ArrayList<ArrayList<Boolean>> getActiveCells() {
		return activeCells;
	}

	public void setActiveCells(ArrayList<ArrayList<Boolean>> activeCells) {
		this.activeCells = activeCells;
	}

	public ArrayList<ArrayList<Boolean>> getNextActiveCells() {
		return nextActiveCells;
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
		case ACTIVEGENERATION:
			cells = this.getActiveCells();
			break;
		case NEXTACTIVEGENERATION:
			cells = this.getNextActiveCells();
			break;

		}

		return cells;

	}

	@Override
	public void resetGameBoard() {
		for (int i = 0; i < super.getWidth(); i++) {
			for (int j = 0; j < super.getHeight(); j++) {
				setCellState(i - shiftedRightwards, j - shiftedDownwards, BoardContainer.CURRENTGENERATION, false);
				setCellState(i - shiftedRightwards, j - shiftedDownwards, BoardContainer.NEXTGENERATION, false);
				setCellState(i - shiftedRightwards, j - shiftedDownwards, BoardContainer.ACTIVEGENERATION, false);
				setCellState(i - shiftedRightwards, j - shiftedDownwards, BoardContainer.NEXTACTIVEGENERATION, false);
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
				setCellState(x + startX, y + startY, BoardContainer.ACTIVEGENERATION, pattern.getPattern()[x][y]);

			}
		}

	}

	public int getShiftedDownwards() {
		return shiftedDownwards;
	}

	public void setShiftedDownwards(int shiftedDownwards) {
		this.shiftedDownwards = shiftedDownwards;
	}

	public int getShiftedRightwards() {
		return shiftedRightwards;
	}

	public void setShiftedRightwads(int shiftedRightwards) {
		this.shiftedRightwards = shiftedRightwards;
	}

	@Override
	public void nextGeneration() {
		// TODO Auto-generated method stub
		
	}
	
	public int getExpansionPadding() {
		return expansionPadding;
	}


	public void setExpansionPadding(int expansionPadding) {
		this.expansionPadding = expansionPadding;
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
				activeGenCopy.get(i).add(j, this.getActiveCells().get(i).get(j));
				NextActiveGenCopy.get(i).add(j, this.getNextActiveCells().get(i).get(j));

			}
		}

		DynamicGameBoardCopy.currGeneration = currGenCopy;
		DynamicGameBoardCopy.nextGeneration = nextGenCopy;
		DynamicGameBoardCopy.activeCells = activeGenCopy;
		DynamicGameBoardCopy.nextActiveCells = NextActiveGenCopy;

		return DynamicGameBoardCopy;
	}
	
	// rounds up rowsRequired by 100 and stores it in rowsToInsert
		// when you insert an element into an index that already has an element
		// the new element pushes the original one index to the right
		public void extendBorderFromLeft(int columnsRequired) {
			
			int columnsToInsert = columnsRequired + expansionPadding;

			for (int i = 0; i < columnsToInsert; i++) {
				currGeneration.add(i, new ArrayList<Boolean>(100));
				nextGeneration.add(i, new ArrayList<Boolean>(100));
				activeCells.add(i, new ArrayList<Boolean>(100));
				nextActiveCells.add(i, new ArrayList<Boolean>(100));

				for (int j = 0; j < super.getHeight(); j++) {
					currGeneration.get(i).add(j, false);
					nextGeneration.get(i).add(j, false);
					activeCells.get(i).add(j, false);
					nextActiveCells.get(i).add(j, false);
				}

				
			}
			this.shiftedRightwards += columnsToInsert;
			super.setWidth(super.getWidth() + columnsToInsert);
			System.out.println("LEFT + " + columnsToInsert);
			itHappens++;
		}
		

		public void extendBorderFromTop(int rowsRequired) {
			int rowsToInsert = rowsRequired + expansionPadding;
			
			for (int i = 0; i < super.getWidth(); i++) {

				for (int j = 0; j < rowsToInsert; j++) {
					currGeneration.get(i).add(j, false);
					nextGeneration.get(i).add(j, false);
					activeCells.get(i).add(j, false);
					nextActiveCells.get(i).add(j, false);
				}

			}
			this.shiftedDownwards += rowsToInsert;
			super.setHeight(rowsToInsert + super.getHeight());
			System.out.println("TOP + " + rowsToInsert);
			itHappens++;
		}

		

		public void extendBorderFromRight(int columnsRequired) {
			
			int columnsToInsert = columnsRequired + expansionPadding;

			for (int i = this.getWidth(); i < this.getWidth() + columnsToInsert; i++) {
				currGeneration.add(i, new ArrayList<Boolean>());
				nextGeneration.add(i, new ArrayList<Boolean>());
				activeCells.add(i, new ArrayList<Boolean>());
				nextActiveCells.add(i, new ArrayList<Boolean>());

				for (int j = 0; j < this.getHeight(); j++) {
					currGeneration.get(i).add(j, false);
					nextGeneration.get(i).add(j, false);
					activeCells.get(i).add(j, false);
					nextActiveCells.get(i).add(j, false);

				}
				
			}

			super.setWidth(super.getWidth() + columnsToInsert);
			System.out.println("Right: " + columnsToInsert);
			itHappens++;

		}

		public void extendBorderFromBottom(int rowsRequired) {
			int rowsToInsert = rowsRequired + expansionPadding;
			
			for (int i = 0; i < this.getWidth(); i++) {

				for (int j = this.getHeight(); j < rowsToInsert + this.getHeight(); j++) {
					currGeneration.get(i).add(j, false);
					nextGeneration.get(i).add(j, false);
					activeCells.get(i).add(j, false);
					nextActiveCells.get(i).add(j, false);
				}
			}

			super.setHeight(super.getHeight() + rowsToInsert);

		    System.out.println("BOT + " + rowsToInsert);
			itHappens++;
		}

}
