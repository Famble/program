package GameOfLife.Model;

import java.io.IOException;

public class RLEPattern {
	
	private int patternWidth;
	private int patternHeight;
	private boolean[][] pattern;
	public boolean settingPattern = false;
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

	public RLEPattern(){
		
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

	
}
