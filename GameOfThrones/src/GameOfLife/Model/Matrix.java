package GameOfLife.Model;

import javafx.scene.Cursor;
import javafx.scene.paint.Color;

public abstract class Matrix 
{
	private int width;
	private int height;
	private Rules rules;
	private Color cellColor = Color.web("#42dd50");
	
	public Matrix(int width, int height, Rules rules)
	{
		this.width = width;
		this.height = height;
		this.rules = rules;
	}
	
	public abstract void determineNextGeneration();
	
	public Rules getRules(){
		return this.rules;
	}

	public int getWidth() {
		return width;
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

	
	
	
}
