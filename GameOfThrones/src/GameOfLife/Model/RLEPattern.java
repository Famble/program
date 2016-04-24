package GameOfLife.Model;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RLEPattern implements Cloneable {
	
	private int width;
	private int height;
	private boolean[][] pattern;
	public boolean settingPattern = false;
	private int patternTranslationX;
	private int patternTranslationY;
	
	public int getPatternTranslationX() {
		return patternTranslationX;
	}

	public void setPatternTranslationX(int patternStartPositionX) {
		this.patternTranslationX = patternStartPositionX;
	}

	public int getPatternTranslationY() {
		return patternTranslationY;
	}

	public void setPatternTranslationY(int patternStartPositionY) {
		this.patternTranslationY = patternStartPositionY;
	}
	
	public void trim(){
		int lowestX = getWidth();
		int lowestY = getHeight();
		int highestX = 0;
		int highestY = 0;

		
		for(int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				if (getPattern()[x][y]) {
					if (x < lowestX)
						lowestX = x;
					if (y < lowestY)
						lowestY = y;
					if (x > highestX)
						highestX = x;
					if (y > highestY) {
						highestY = y;
					}
				}
			}
		}
		System.out.printf("lowestX, lowestY, highestX, highestY: (%d,%d,%d,%d) \n",lowestX, lowestY, highestX, highestY);
		
		this.width = highestX - lowestX + 1;
		this.height = highestY - lowestY+ 1;
		
		for(int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				this.pattern[x][y] = this.pattern[x + lowestX][y + lowestY];

			}
		}
	}



	public RLEPattern(){
		
	}
	
	public RLEPattern(int width, int height){
		this.width = width;
		this.height = height;
		this.pattern = new boolean[width][height];
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int patternWidth) {
		this.width = patternWidth;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int patternHeight) {
		this.height = patternHeight;
	}

	public boolean[][] getPattern() {
		return this.pattern;
	}

	public void setPattern(boolean[][] pattern) {
		this.pattern = pattern;
	}

	public boolean isSettingPattern() {
		return settingPattern;
	}
	
	public Object clone(){
		RLEPattern patternCopy =null;
		try{
			patternCopy = (RLEPattern)super.clone();
			patternCopy.pattern = Arrays.copyOf(this.pattern, pattern.length);
		}catch(CloneNotSupportedException e){
			return null;
		}
		
		return patternCopy;
	}

	
}
