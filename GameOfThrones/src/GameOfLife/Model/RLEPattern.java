package GameOfLife.Model;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RLEPattern implements Cloneable {
	
	private int patternWidth;
	private int patternHeight;
	private boolean[][] pattern;
	public boolean settingPattern = false;
	private int trimmedWidth;

	public int getTrimmedHeight() {
		return trimmedHeight;
	}

	public void setTrimmedHeight(int trimmedHeight) {
		this.trimmedHeight = trimmedHeight;
	}

	public int getTrimmedWidth() {
		return trimmedWidth;
	}

	public void setTrimmedWidth(int trimmedWidth) {
		this.trimmedWidth = trimmedWidth;
	}

	private int trimmedHeight;
	private int patternStartPositionX;
	private int patternStartPositionY;
	
	
	public int getPatternStartPositionX() {
		return patternStartPositionX;
	}

	public void setPatternStartPositionX(int patternStartPositionX) {
		this.patternStartPositionX = patternStartPositionX;
	}

	public int getPatternStartPositionY() {
		return patternStartPositionY;
	}

	public void setPatternStartPositionY(int patternStartPositionY) {
		this.patternStartPositionY = patternStartPositionY;
	}
	
	public void trim(){
		int lowestX = getPatternWidth();
		int lowestY = getPatternHeight();
		int highestX = 0;
		int highestY = 0;
		System.out.println("DET FUNKEt");

		
		for(int x = 0; x < getPatternWidth(); x++) {
			for (int y = 0; y < getPatternHeight(); y++) {
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
		
		this.patternWidth = highestX - lowestX + 1;
		this.patternHeight = highestY - lowestY+ 1;
		
		for(int x = 0; x < this.patternWidth; x++) {
			for (int y = 0; y < this.patternHeight; y++) {
				this.pattern[x][y] = this.pattern[x + lowestX][y + lowestY];

			}
		}
	}



	public RLEPattern(){
		
	}
	
	public RLEPattern(int width, int height){
		this.patternWidth = width;
		this.patternHeight = height;
		this.pattern = new boolean[width][height];
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
