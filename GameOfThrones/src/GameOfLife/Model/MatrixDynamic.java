package GameOfLife.Model;

import java.util.ArrayList;
import java.util.List;

public class MatrixDynamic extends Matrix {
	private List<List<Long>> currGeneration;
	private List<List<Long>> nextGeneration;

	public MatrixDynamic(int x, int y, Rules rules) 
	{
		super(x, y, rules);
		currGeneration = new ArrayList<List<Long>>(x);
		nextGeneration = new ArrayList<List<Long>>(x);

		
		for(int i = 0; i < x; i++)
		{
			currGeneration.add(i, new ArrayList<Long>(y));
			for(int j = 0; j < y; j++){
				currGeneration.get(i).add(0L);
			}
		}

		
	}
	
	public void setCellState(int x, int y, List<List<Long>> array, boolean value)
	{
		long lng = array.get(x).get(y/64);
		
		if(value){
			lng |= (1L << y%64);
			array.get(x).set(y/64, lng);
		}
		else{
			lng &= (1L << y%64);
			array.get(x).set(y/64, lng);
		}
	}
	
	public boolean getCellState(int x, int y, List<List<Long>> array)
	{
		return (array.get(x).get(y/64) >> y%64 & 1) == 1 ;
	}
	
	public List<List<Long>> getCurrGeneration() {
		return currGeneration;
	}

	public void setCurrGeneration(List<List<Long>> currGeneration) {
		this.currGeneration = currGeneration;
	}

	public List<List<Long>> getNextGeneration() {
		return nextGeneration;
	}

	public void setNextGeneration(List<List<Long>> nextGeneration) {
		this.nextGeneration = nextGeneration;
	}

	@Override
	public void determineNextGeneration() {
		// TODO Auto-generated method stub
		
	}

}
