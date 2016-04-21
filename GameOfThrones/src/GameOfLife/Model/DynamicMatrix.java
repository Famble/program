package GameOfLife.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DynamicMatrix extends Matrix {
		
	private List<List<Boolean>> currGeneration;
	private List<List<Boolean>> nextGeneration;
	private List<List<Boolean>> activeCells;
	private List<List<Boolean>> nextActiveCells;
	private int shiftedRightwards = 0;
	private int shiftedDownwards = 0;
	int count = 0;
	

	public int getShiftedDownwards() {
		return shiftedDownwards;
	}

	public void setShiftedDownwards(int shiftedDownwards) {
		this.shiftedDownwards = shiftedDownwards;
	}

	public int getShiftedRightwards() {
		return shiftedRightwards;
	}

	public void setShiftedToRight(int shiftedToRight) {
		this.shiftedRightwards = shiftedToRight;
	}

	public DynamicMatrix(int x, int y, Rules rules) 
	{
		super(x, y, rules);

		
		currGeneration = new ArrayList<List<Boolean>>(x);
		nextGeneration = new ArrayList<List<Boolean>>(x);
		activeCells = new ArrayList<List<Boolean>>(x);
		nextActiveCells = new ArrayList<List<Boolean>>(x);
		
		for(int i = 0; i < super.getWidth(); i++)
		{
			currGeneration.add(new ArrayList<Boolean>(y));
			nextGeneration.add(new ArrayList<Boolean>(y));
			activeCells.add(new ArrayList<Boolean>(y));
			nextActiveCells.add(new ArrayList<Boolean>(y));
			
			for(int j = 0; j <= super.getHeight(); j++){
				currGeneration.get(i).add(j,  false);
				nextGeneration.get(i).add(j, false);
				activeCells.get(i).add(j, false);
				nextActiveCells.get(i).add(j, false);
			}
		}
		
	}
	
	public void startNextGeneration(){
		determineNextGeneration();
		for(int i = 0; i < super.getWidth(); i++){
			for(int j = 0; j < super.getHeight(); j++){
				this.getCurrGeneration().get(i).set(j, this.getNextGeneration().get(i).get(j));
				this.getActiveCells().get(i).set(j, this.getNextActiveCells().get(i).get(j));
				
			}
		}
	}
	
	@Override
	public void determineNextGeneration() {
		int aliveNeighbors = 0;
		for(int x = 0; x < super.getWidth(); x++){
			for(int y = 0; y < super.getHeight(); y++){
				int xx = x - shiftedRightwards;
				int yy = y - shiftedDownwards;
				if(this.getCellState(xx, yy, BoardContainer.ACTIVEGENERATION)){
					aliveNeighbors = countNeighbours(xx, yy, true);
					setCellStateFromRules(xx, yy, aliveNeighbors);
				}
			}
		}
		
	}
	
	@Override
	public int countNeighbours(int x, int y, boolean countNeighbours) {
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
					
					if(countNeighbours){
						setCellStateFromRules(neighborX, neighborY, countNeighbours(neighborX, neighborY, false));
					}
				}
				
			}
	
		return aliveNeighbours;
	}

	private void setCellStateFromRules(int x, int y, int aliveNeighbours){
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
/*
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[i].length; j++) {
				this.getActiveCells()[i][j] = board[i][j];
				this.getCurrentGeneration()[i][j] = board[i][j];
			}
			
			*/

	}

	
	public void extendBorderFromLeft(int columnsRequired)
	{
		 //rounds up rowsRequired by 100 and stores it in rowsToInsert
		int columnsToInsert = columnsRequired - (columnsRequired % 100) + 100;
		this.shiftedRightwards += columnsToInsert;
		
		System.out.println("HAR EXTENDED FRA LEFT");
		System.out.println("COLUMNS TO INSER: " + columnsToInsert);

		
		//when you insert an element into an index that already has an element
		//the new element pushes the original one index to the right
		
		for(int i = 0; i < columnsToInsert; i++){
			currGeneration.add(i, new ArrayList<Boolean>());
			nextGeneration.add(i, new ArrayList<Boolean>() );
			activeCells.add(i, new ArrayList<Boolean>());
			nextActiveCells.add(i, new ArrayList<Boolean>());
			
			for(int j = 0; j < this.getHeight(); j++){
				currGeneration.get(i).add(j, false);
				nextGeneration.get(i).add(j, false);
				activeCells.get(i).add(j, false);
				nextActiveCells.get(i).add(j, false);
			}
			
		}
		super.setWidth(super.getWidth() + columnsToInsert);

	}
	
	public void extendBorderFromRight(int x)
	{
		
		int columnsRequired = x - super.getWidth();
		int columnsToInsert = columnsRequired - (columnsRequired % 100) + 100;

		for(int i = this.getWidth(); i < this.getWidth() +columnsToInsert; i++){
			currGeneration.add(i, new ArrayList<Boolean>());
			nextGeneration.add(i, new ArrayList<Boolean>() );
			activeCells.add(i, new ArrayList<Boolean>());
			nextActiveCells.add(i, new ArrayList<Boolean>());
			
			for(int j = 0; j < this.getHeight(); j++){
				currGeneration.get(i).add(j, false);
				nextGeneration.get(i).add(j, false);
				activeCells.get(i).add(j, false);
				nextActiveCells.get(i).add(j, false);
				
			}
		}
		
	
		super.setWidth(super.getWidth() + columnsToInsert);
		
		
	}
	
	public void extendBorderFromTop(int rowsRequired){
		int rowsToInsert = rowsRequired - (rowsRequired % 100) + 100;
		for(int i = 0; i < super.getWidth(); i++){
			
		for(int j = 0; j < rowsToInsert; j++){
			currGeneration.get(i).add(j, false);
			nextGeneration.get(i).add(j, false);
			activeCells.get(i).add(j, false);
			nextActiveCells.get(i).add(j, false);
		}

	}
		this.shiftedDownwards += rowsToInsert;
		super.setHeight(rowsToInsert + super.getHeight());

		
	}
	
	public void extendBorderFromBottom(int y){
		int rowsRequired = y - super.getHeight();
		int rowsToInsert = rowsRequired - (rowsRequired % 100) + 100;
		for(int i = 0; i < this.getWidth(); i++){
						
			for(int j = this.getHeight(); j < rowsToInsert + this.getHeight(); j++){
				currGeneration.get(i).add(j, false);
				nextGeneration.get(i).add(j, false);
				activeCells.get(i).add(j, false);
				nextActiveCells.get(i).add(j, false);
			}
		}
		
		super.setHeight(super.getHeight() + rowsToInsert);
		
	}
	
	
	@Override
	public void setCellState(int x, int y, BoardContainer container, boolean alive)
	{		

		
		if(x + shiftedRightwards < 0){
			extendBorderFromLeft(Math.abs(x+shiftedRightwards));
		}
		
		
		if( x+shiftedRightwards >= this.getWidth()){
			extendBorderFromRight(x+shiftedRightwards);
		}
		
		if(y + shiftedDownwards < 0){
			extendBorderFromTop(Math.abs(y + shiftedDownwards));
		}
		
		if(y + shiftedDownwards >= this.getHeight()){
			extendBorderFromBottom(y + shiftedDownwards);
		}
		
		
		
	    List<List<Boolean>> cells = null;
	    
	    switch(container){
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
	    
	    
		cells.get(x+shiftedRightwards).set(y+ shiftedDownwards, alive);
	}
	
	public List<List<Boolean>> getCurrGeneration() {
		return currGeneration;
	}

	public void setCurrGeneration(List<List<Boolean>> currGeneration) {
		this.currGeneration = currGeneration;
	}

	public List<List<Boolean>> getNextGeneration() {
		return nextGeneration;
	}

	public void setNextGeneration(List<List<Boolean>> nextGeneration) {
		this.nextGeneration = nextGeneration;
	}

	public List<List<Boolean>> getActiveCells() {
		return activeCells;
	}

	public void setActiveCells(List<List<Boolean>> activeCells) {
		this.activeCells = activeCells;
	}

	public List<List<Boolean>> getNextActiveCells() {
		return nextActiveCells;
	}



	
	@Override
	public boolean getCellState(int x, int y, BoardContainer container){
		{			

			//System.out.printf("X VS shiftRightwards: (%d vs %d)%n", x, shiftedRightwards);
			
			if(x + shiftedRightwards < 0){
				extendBorderFromLeft(Math.abs((x+shiftedRightwards)));
			}
			
			
			if( x+shiftedRightwards >= this.getWidth()){
				extendBorderFromRight(x+shiftedRightwards);
			}
			
			if(y + shiftedDownwards < 0){
				extendBorderFromTop(Math.abs(y + shiftedDownwards));
			}
			
			if(y + shiftedDownwards >= this.getHeight()){
				extendBorderFromBottom(y + shiftedDownwards);
			}
			
				List<List<Boolean>> cells = null;
				
				switch(container){
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
				
				//System.out.printf("(x y): (%d,%d)\n", x + shiftedRightwards, y+shiftedDownwards);

				return cells.get(x + shiftedRightwards).get(y + shiftedDownwards);		
			
		}
	}

	@Override
	public void resetGameBoard() {
		for(int i = 0; i < super.getWidth(); i++){
			for(int j = 0; j < super.getHeight(); j++){
				setCellState(i-shiftedRightwards, j-shiftedDownwards, BoardContainer.CURRENTGENERATION, false);
			}
		}
		
		
		
	}

	@Override
	public void transferPattern(int startX, int startY) {
		super.setSettingPattern(false);
		RLEPattern pattern = super.getPattern();
		for(int x = 0; x < pattern.getPatternWidth(); x++){
			for(int y = 0; y < pattern.getPatternHeight(); y++){
				setCellState(x+ startX, y+startY, BoardContainer.CURRENTGENERATION, pattern.getPattern()[x][y]);
				setCellState(x+ startX, y+startY, BoardContainer.ACTIVEGENERATION, pattern.getPattern()[x][y]);

			}
		}
		
	}

	


	




	

	

	

}