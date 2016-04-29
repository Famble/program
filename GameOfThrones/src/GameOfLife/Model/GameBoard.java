package GameOfLife.Model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.regex.Pattern;

import javafx.scene.paint.Color;

public abstract class GameBoard implements Cloneable
{
	public enum BoardContainer{
		CURRENTGENERATION, NEXTGENERATION, ACTIVEGENERATION, NEXTACTIVEGENERATION
	}
	
	private int width;
	private int height;
	private Rules rules;
	private Color cellColor = Color.web("#42dd50");
	private RLEPattern pattern;
	private boolean settingPattern = false;
	
	
	
	
	public GameBoard(int width, int height, Rules rules)
	{
		this.width = width;
		this.height = height;
		this.rules = rules;
	}
	
	
	
	public abstract void nextGeneration();
	public abstract void nextGenerationConcurrent();
	
	public RLEPattern getPattern(){
		return this.pattern;
	}
	
	public <T> void setBoard(T element){
		
	}
	
	public abstract void createPattern();
	
	public abstract void transferPattern(int startX, int startY);
	
	public abstract boolean getCellState(int x, int y, BoardContainer bc);
	public abstract void determineNextGenerationConcurrent(int x);
	
	public abstract void setCellState(int x, int y, BoardContainer bc, boolean alive);
	
	public abstract void resetGameBoard();
	
	public abstract int countNeighbours(int x, int y);
	
	public Rules getRules(){
		return this.rules;
	}

	public int getWidth() {
		return width;
	}
	
	public void setPattern(RLEPattern pattern){
		this.pattern = pattern;
	}
	
	public void setSettingPattern(boolean settingPattern) {
		this.settingPattern = settingPattern;
	}
	
	public boolean getSettingPattern(){
		return this.settingPattern;
	}

	public void setWidth(int width) {
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
	
	@Override
	public Object clone(){
		GameBoard gameBoardCopy;
		try{
			gameBoardCopy = (GameBoard)super.clone();
			gameBoardCopy.rules = (Rules)rules.clone();
			gameBoardCopy.cellColor = this.getColor();
			//gameBoardCopy.pattern = (RLEPattern)pattern.clone();
			
			
			
		}catch(CloneNotSupportedException e){
			System.out.println(e.getMessage());
			return null;
		}
		
		return gameBoardCopy;
	}
	
	public String toString(){
		return String.format("Width: %d\nHeight: %d\n", this.getWidth(), this.getHeight());
	}
	
	

	
	
	
}
