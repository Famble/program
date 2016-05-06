package GameOfLife.Model;

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
            System.arraycopy(this.pattern[x + lowestX], lowestY, this.pattern[x], 0, this.height);
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

    public void setAuthorOfPattern(String authorOfPattern) {
        this.authorOfPattern = authorOfPattern;
    }

    public void setNameOfPattern(String nameOfPattern) {
        this.nameOfPattern = nameOfPattern;
    }

    public String getNameOfPattern() {
        return nameOfPattern;
    }

    public String getAuthorOfPattern() {
        return authorOfPattern;
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
        return authorOfPattern != null ;
    }
    public boolean hasComment(){
        return commentOfPattern != null ;
    }
}
