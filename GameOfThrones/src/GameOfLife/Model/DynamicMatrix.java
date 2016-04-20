package GameOfLife.Model;

import java.util.ArrayList;
import java.util.List;

public class DynamicMatrix extends Matrix {
		
	private List<List<Boolean>> currGeneration;
	private List<List<Boolean>> nextGeneration;
	private List<List<Boolean>> activeCells;
	private List<List<Boolean>> nextActiveCells;
	private int shiftedRightward = 0;
	private int shiftedDownwards = 0;
	

	public int getShiftedDownwards() {
		return shiftedDownwards;
	}

	public void setShiftedDownwards(int shiftedDownwards) {
		this.shiftedDownwards = shiftedDownwards;
	}

	public int getShiftedToRight() {
		return shiftedRightward;
	}

	public void setShiftedToRight(int shiftedToRight) {
		this.shiftedRightward = shiftedToRight;
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
			currGeneration.add(i, new ArrayList<Boolean>(y));
			nextGeneration.add(i, new ArrayList<Boolean>(y));
			activeCells.add(i, new ArrayList<Boolean>(y));
			nextActiveCells.add(i, new ArrayList<Boolean>(y));
			
			for(int j = 0; j <= super.getHeight(); j++){
				currGeneration.get(i).add(j,  false);
				nextGeneration.get(i).add(j, false);
				activeCells.get(i).add(j, false);
				nextActiveCells.get(i).add(j, false);
			}
		}
		
	}
	public void extendBorderFromLeft(int columnsRequired)
	{
		 //rounds up rowsRequired by 100 and stores it in rowsToInsert
		int columnsToInsert = columnsRequired - (columnsRequired % 100) + 100;
		System.out.println("columns to insert: " + columnsToInsert);
		this.shiftedRightward += columnsToInsert;
		
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
		System.out.println("Rows added: " + rowsToInsert);
		super.setHeight(rowsToInsert + super.getHeight());
		System.out.println("Current Height: " + super.getHeight());

		
	}
	
	public void extendBorderFromBottom(int y){
		int rowsRequired = y - super.getHeight();
		int rowsToInsert = rowsRequired - (rowsRequired % 100) + 100;
		System.out.println("ROWS TO INERT " +rowsToInsert);
		for(int i = 0; i < this.getWidth(); i++){
						
			for(int j = this.getHeight(); j < rowsToInsert + this.getHeight(); j++){
				currGeneration.get(i).add(j, false);
				nextGeneration.get(i).add(j, false);
				activeCells.get(i).add(j, false);
				nextActiveCells.get(i).add(j, false);
			}
		}
		
		super.setHeight(super.getHeight() + rowsToInsert);
		System.out.println(super.getHeight());
		
	}
	
	

	public void setCellState(int x, int y, boolean alive)
	{		
		if(x + shiftedRightward < 0){
			extendBorderFromLeft(Math.abs(x+shiftedRightward));
		}
		
		
		if( x+shiftedRightward >= this.getWidth()){
			extendBorderFromRight(x+shiftedRightward);
		}
		
		if(y + shiftedDownwards < 0){
			extendBorderFromTop(Math.abs(y + shiftedDownwards));
		}
		
		if(y + shiftedDownwards >= this.getHeight()){
			extendBorderFromBottom(y + shiftedDownwards);
		}
		System.out.printf("(x y): (%d,%d)\n", x + shiftedRightward, y+shiftedDownwards);
		
		currGeneration.get(x + this.shiftedRightward).set(y+ shiftedDownwards, alive);
	}
	
	public void setActiveCellState(int x, int y, boolean alive)
	{	
		currGeneration.get(x).set(y, alive);
	}
	
	public boolean getCellState(int x, int y){
		{			
			if(x < 0 || x >= super.getWidth() || y < 0 || y >= super.getHeight())
			{
				return false;
			}
			else
			{
				return currGeneration.get(x).get(y);		
			}
		}
	}

	public boolean getActiveCellState(int x, int y){
		return this.activeCells.get(x).get(y);
	}

	@Override
	public void startNextGeneration() {
		determineNextGeneration();
		
	}

	@Override
	public void determineNextGeneration() {
		for(int x = 0; x < super.getHeight(); x++){
			for(int y = 0; y < super.getHeight(); y++){
				
			}
		}
		
	}

	@Override
	public int countNeighbours(int x, int y, boolean countNeighbours) {
		// TODO Auto-generated method stub
		return 0;
	}

	

	

	

}
