package GameOfLife.Model;

import javafx.scene.paint.Color;

public abstract class Matrix 
{
	public enum BoardContainer{
		CURRENTGENERATION, NEXTGENERATION, ACTIVEGENERATION, NEXTACTIVEGENERATION
	}
	
	private int width;
	private int height;
	private Rules rules;
	private Color cellColor = Color.web("#42dd50");
	int counter = 0;
	
	
	public Matrix(int width, int height, Rules rules)
	{
		this.width = width;
		this.height = height;
		this.rules = rules;
	}
	
	
	public abstract void startNextGeneration();
	
	public abstract boolean getCellState(int x, int y, BoardContainer bc);
	public abstract void determineNextGeneration();
	
	public abstract void setCellState(int x, int y, BoardContainer bc, boolean alive);
	
	public abstract void resetGameBoard();
	
	public abstract int countNeighbours(int x, int y, boolean countNeighbours);
	
	public Rules getRules(){
		return this.rules;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		counter++;
		if(counter == 2){
			System.out.println("k");
		}
		System.out.println("NEW WIDTH IS: " + width);
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public Color getColor() {
		return this.cellColor;
	}
	
	public void setColor(Color color) {
		this.cellColor = color;
	}

	
	
	
}
