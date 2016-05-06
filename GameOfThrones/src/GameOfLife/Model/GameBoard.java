package GameOfLife.Model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.regex.Pattern;

import javafx.scene.paint.Color;

/**
 * Contains concrete and abstract methods that all subclasses of GameBoard shares.
 * By using this abstract class we can use polymorphism and treat subclass of GameBoard
 * as GameBoard allowing the instance to be determined at runtime.
 * @author Markus Hellestveit
 *
 */
public abstract class GameBoard implements Cloneable
{
	/**
	 * Stores the specify which container to use.
	 * @author Markus Hellestveit
	 *
	 */
	public enum BoardContainer{
		CURRENTGENERATION, NEXTGENERATION, ACTIVEGENERATION, NEXTACTIVEGENERATION
	}
	
	/**
	 * the width of the game board
	 */
	private int width;
	/**
	 * the height of the game board
	 */
	private int height;
	/**
	 * the rules that specify the way the game board evolves
	 */
	private Rules rules = new Rules();
	/**
	 * the color of each individual cell
	 */
	private Color cellColor = Color.web("#42dd50");
	/**
	 * A pattern that can be loaded from RLE.
	 */
	private RLEPattern pattern;
	/**
	 * This boolean is set to true whenever the user loads an rle file, and remain true until the user presses
	 * enter and stores the pattern in its current generation container.
	 */
	private boolean settingPattern = false;
	
	
	
	/**
	 * Sets the width and height of the game board.
	 * 
	 * @param width amount of cells in the horizontal direction
	 * @param height amount of cells in the vertical direction
	 */
	public GameBoard(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Determines the next generation of the game board.
	 */
	public abstract void nextGeneration();
	
	/**
	 * Determines the next generation of the game board concurrently by using threads.
	 */
	public abstract void nextGenerationConcurrent();
	
	/**
	 * Sets the pattern.
	 * @return
	 */
	public RLEPattern getPattern(){
		return this.pattern;
	}
	
	/**
	 * Sets the current game board to the board specified by the T element.
	 * @param element
	 */
	public <T> void setBoard(T element){
		
	}
	
	
	
	
	public abstract void transferPattern(int startX, int startY);
	
	/**
	 * Gets the state of the cells specified by the parameters.
	 * @param x horizontal position of cell to get the state of.
	 * @param y vertical position of cell to get the state of.
	 * @param bc the container the cell is within
	 * @return the state of the cell.
	 */
	public abstract boolean getCellState(int x, int y, BoardContainer container);
	
	/**
	 * Sets the state of the cell specified by the parameters.
	 * @param x the vertical position to the cell to be set
	 * @param y the horizontal position of the cell to bet set.
	 * @param bc the container holding the cell to be set
	 * @param alive the state the cell should be set to.
	 * 
	 */
	public abstract void setCellState(int x, int y, BoardContainer bc, boolean alive);

	/**
	 * Kill every cell.
	 */
	public abstract void resetGameBoard();
	
	/**
	 * counts the amount of living neighbors of the cell specified by the parameters
	 * @param x the vertical position of the cell
	 * @param y the horizontal position of the cell
	 */
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