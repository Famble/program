package GameOfLife.Model;

import java.util.Arrays;

public class RLEPattern implements Cloneable {

    private int width;
    private int height;


    private String authorOfPattern;
    private String ruleOfPattern;
    private String nameOfPattern;
    private String commentOfPattern;

    private boolean[][] pattern;
    public boolean settingPattern = false;
    private int patternTranslationX;


    private int patternTranslationY;


    public RLEPattern() {

    }

    public RLEPattern(int width, int height) {
        this.width = width;
        this.height = height;
        this.pattern = new boolean[width][height];
    }

    public void trim() {
        int lowestX = getWidth();
        int lowestY = getHeight();
        int highestX = 0;
        int highestY = 0;

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                if (getPattern()[x][y]) {
                	lowestX = Math.min(x,  lowestX);
                	lowestY = Math.min(y, lowestY);
                	highestX = Math.max(x, highestX);
                	highestY = Math.max(y, highestY);
                }
            }
        }
        System.out.printf("lowestX, lowestY, highestX, highestY: (%d,%d,%d,%d) \n", lowestX, lowestY, highestX, highestY);

        this.width = highestX - lowestX + 1;
        this.height = highestY - lowestY + 1;

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.pattern[x][y] = this.pattern[x + lowestX][y + lowestY];

            }
        }
    }

    public Object clone() {
        RLEPattern patternCopy = null;
        try {
            patternCopy = (RLEPattern) super.clone();
            patternCopy.pattern = Arrays.copyOf(this.pattern, pattern.length);
        } catch (CloneNotSupportedException e) {
            return null;
        }

        return patternCopy;
    }


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

    public void setAuthorOfPattern(String authorOfPattern) {
        this.authorOfPattern = authorOfPattern;
    }

    public void setRuleOfPattern(String ruleOfPattern) {
        this.ruleOfPattern = ruleOfPattern;
    }

    public void setNameOfPattern(String nameOfPattern) {
        this.nameOfPattern = nameOfPattern;
    }

    public String getAuthorOfPattern() {
        return authorOfPattern;
    }

    public String getRuleOfPattern() {
        return ruleOfPattern;
    }

    public String getNameOfPattern() {
        return nameOfPattern;
    }

    public String getCommentOfPattern() {
        return commentOfPattern;
    }

    public void setCommentOfPattern(String commentOfPattern) {
    	this.commentOfPattern = commentOfPattern;
    }

    public boolean hasName(){
        return nameOfPattern != null;
    }
    public boolean hasAuthor(){
        return authorOfPattern != null || authorOfPattern != "";
    }
    public boolean hasComment(){
        return commentOfPattern != null || commentOfPattern != "";
    }
}
