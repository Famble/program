package GameOfLife.Model;

import java.util.Arrays;

/**
 * Pattern loaded from an RLE File.
 * Used to manipulate tha pattern and store the metadata of the pattern.
 * @author Johnny Lam, Markus Hellestveit.
 *
 */
public class RLEPattern implements Cloneable {

	/**
	 * Width of pattern
	 */
    private int width;
    /**
     * height of pattern
     */
    private int height;

    /**
     * The author of the pattern
     */
    private String authorOfPattern;
    /**
     * the rule set of the pattern
     */
    private String ruleOfPattern;
    private String nameOfPattern;
    /**
     * the description of the pattern.
     */
    private String commentOfPattern;

    private boolean[][] pattern;
    public boolean settingPattern = false;
    /**
     * How much the pattern should be moved in the vertical direction before being set.
     */
    private int patternTranslationX;

    /**
     * How much the pattern should be moved in the vertical direction before being set.
     */
    private int patternTranslationY;


    public RLEPattern() {

    }

    /**
     * Sets a new patter with the width and height specifies by the parameters
     * @param width of the pattern
     * @param height of the pattern.
     */
    public RLEPattern(int width, int height) {
        this.width = width;
        this.height = height;
        this.pattern = new boolean[width][height];
    }

    /**
     * Makes the pattern a small as a rectangle possible while still holding every living cell in the original pattern
 	*/
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
