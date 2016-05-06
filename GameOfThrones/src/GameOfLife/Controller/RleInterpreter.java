package GameOfLife.Controller;

import GameOfLife.Model.PatternFormatException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class decodes/parses a RLE file, and then stores an array for later use.
 *
 * @author Johnny Lam and Dusan Jakovic have done pair-programming, while Markus helped to impliment smaller method
 */
public class RleInterpreter {
    private Matcher matcher;
    private String nameOfRle;
    private String authorOfRle;
    private String birthOfRle;
    private String survivalOfRle;
    private int height;
    private int width;
    private String rleString;
    private StringBuilder commentOfRle = new StringBuilder();
    private boolean[][] initialRleGeneration;
    private int lastIndexOfHeader;
    private StringBuilder testHeader = new StringBuilder();
    private StringBuilder testDimensionAndRule = new StringBuilder();
    private StringBuilder testGameBoard = new StringBuilder();
    private int gameBoardWidth;
    private int gameBoardHeight;
    private boolean dynamic;

    /**
     * Constructor of RleInterpreter
     * Reads the file and the sets the Meta data and the game board logic. Sets the instance variable.
     *
     * @param rleFile         A string of a file that will be parsed.
     * @param gameBoardWidth  An int value of the <b>width</b> to the current game board.
     * @param gameBoardHeight An int value of the <b>height</b> to the current game board.
     * @param dynamic         Boolean value that tells the parses if the board if dynamic.
     * @throws PatternFormatException
     */
    public RleInterpreter(String rleFile, int gameBoardWidth, int gameBoardHeight, boolean dynamic)
            throws PatternFormatException {
        this.rleString = rleFile;
        this.gameBoardHeight = gameBoardHeight;
        this.gameBoardWidth = gameBoardWidth;
        this.dynamic = dynamic;
        readHeader();
        readDimensionAndRule();
        readGameBoard();

    }

    /**
     * Locates the meat data for the file, as Name and Comment of the file.
     *
     */
    private void readHeader() {

        Pattern regex = Pattern.compile("^#([N|C|O|P|R])\\s*(\\w.*)$", Pattern.MULTILINE);
        Matcher matcher = regex.matcher(this.rleString);

        while (matcher.find()) {
            String commentHolder;
            if (matcher.group(1).equalsIgnoreCase("N")) {
                nameOfRle = matcher.group(2);
                testHeader.append(nameOfRle).append("\n");
            } else if (matcher.group(1).equalsIgnoreCase("C")) {
                commentHolder = matcher.group(2);
                commentOfRle.append(commentHolder).append("\n");
                testHeader.append(commentHolder).append("\n");
            } else if (matcher.group(1).equalsIgnoreCase("O")) {
                authorOfRle = matcher.group(2);
                testHeader.append(authorOfRle).append("\n");
            }

        }
    }

    /**
     * Locates
     * and determinate the the dimension and rule of the pattern in the
     * file.
     *
     * @throws PatternFormatException checks if width and height of the pattern is less that width
     *                                and height of game board
     */
    private void readDimensionAndRule() throws PatternFormatException {

        Pattern regex = Pattern.compile("x=([0-9]+),y=([0-9]+)(,rule=(([A-Za-z])*([0-9]*)/([A-Za-z])*([0-9]*)))?",
                Pattern.MULTILINE);

        int amountOfSpaces = 0;
        for (int i = 0; i < this.rleString.length(); i++)
            if (rleString.charAt(i) == ' ')
                amountOfSpaces++;

        matcher = regex.matcher(this.rleString.replaceAll(" ", ""));

        while (matcher.find()) {
            this.width = Integer.parseInt(matcher.group(1));
            this.height = Integer.parseInt(matcher.group(2));

            if (this.width > gameBoardWidth && !dynamic) {
                throw new PatternFormatException(String.format(
                        "Max width of the gameboard is %d, the pattern provided" + "in the rle file has %d width",
                        this.gameBoardWidth, this.gameBoardHeight));
            } else if (this.height > this.gameBoardHeight && !dynamic) {
                throw new PatternFormatException(String.format(
                        "Max height of the gameboard is %d, the pattern provided" + "in the rle file has %d height",
                        this.gameBoardWidth, this.gameBoardHeight));
            }

            if(matcher.group(3)== null){ //Checks if there is set any rules, if not sets the default rules
                birthOfRle = "3";
                survivalOfRle = "23";

            }else {
                if (matcher.group(5).equalsIgnoreCase("B")) {
                    birthOfRle = matcher.group(6);
                } else if (matcher.group(5).equalsIgnoreCase("S")) {
                    survivalOfRle = matcher.group(6);
                }

                if (matcher.group(7).equalsIgnoreCase("B")) {
                    birthOfRle = matcher.group(8);

                } else if (matcher.group(7).equalsIgnoreCase("S")) {
                    survivalOfRle = matcher.group(8);

                }
            }


            lastIndexOfHeader = matcher.end() + amountOfSpaces;
        }

        testDimensionAndRule.append("x = ").append(this.width).append(", y = ")
                .append(this.height).append(", rule = ").append("B").append(birthOfRle)
                .append("/").append("S").append(survivalOfRle).append("\n");
    }

    /**
     * Locates and determines the <b>pattern</b> of the game, inside the file.
     *
     * @throws PatternFormatException checks if it's a mismatch between height/width of the game
     *                                board and with the width/height to the given pattern.
     */
    private void readGameBoard() throws PatternFormatException {
        this.initialRleGeneration = new boolean[width][height];

        String rlePattern[] = this.rleString.substring(lastIndexOfHeader).split("\\$");
        int aliveCells;
        int deadCells;
        int x = 0;
        String deadSymbol; // dead Cell
        String aliveSymbol; // alive Cell

        if (this.getHeight() < rlePattern.length)
            throw new PatternFormatException("Mismatch between given height dimension and actual height in pattern");


        boolean ending = false;
        for (int y = 0; y < rlePattern.length; y++) {

            Pattern regex = Pattern.compile("([0-9]*)([A-Za-z])([!])?");
            matcher = regex.matcher(rlePattern[y]);
            while (matcher.find()) {

                if (matcher.group(2).equals("b")) { // dead cells
                    if (matcher.group(1).matches("[0-9]+")) {
                        deadSymbol = matcher.group(1);
                        deadCells = Integer.parseInt(deadSymbol);
                        testGameBoard.append(deadSymbol).append("b");// test
                    } else {
                        deadCells = 1;
                        testGameBoard.append("b");
                    }

                    if (x + deadCells > this.getWidth())
                        throw new PatternFormatException(
                                "Mismatch between given width dimension and actual width in pattern");

                    for (int i = 0; i < deadCells; i++) {
                        this.initialRleGeneration[x + i][y] = false;
                    }

                    x += deadCells;

                } else if (matcher.group(2).equals("o")) {

                    if (matcher.group(1).matches("[0-9]+")) {
                        aliveSymbol = matcher.group(1);
                        aliveCells = Integer.parseInt(aliveSymbol);
                        testGameBoard.append(aliveSymbol).append("o");
                    } else {
                        aliveCells = 1;
                        testGameBoard.append("o");
                    }

                    if (x + aliveCells > this.getWidth())
                        throw new PatternFormatException(
                                "Mismatch between given width dimension and actual width in pattern");

                    for (int i = 0; i < aliveCells; i++) {
                        this.initialRleGeneration[x + i][y] = true;
                    }

                    x += aliveCells;
                }

                if (x < width) {
                    int remainingCells = width - x;
                    for (int i = 0; i < remainingCells; i++)
                        this.initialRleGeneration[x + i][y] = false;
                }

                if(matcher.group(3) != null && (matcher.group(3).equals("!"))){
                    ending = true;
                    testGameBoard.append("!");
                    break;
                }


            } //end of while((matcher.find())

            x = 0;

            if(ending){
                break;
            }else{
                testGameBoard.append("$");
            }
        } // end of for-loop

    } // end of readBoard

    /**
     * This method is mainly in use in RleInterpreterTest, and the
     * @return String of the gameboard that is read in the pattern
     */
    public String getTestGameBoard() {
        return testGameBoard.toString();
    }

    /**
     * Returns a String of the whole file.
     */
    @Override
    public String toString() {

        return testHeader.toString() + testDimensionAndRule.toString() + testGameBoard.toString();
    }

    /**
     * Gets the name of the pattern
     * @return pattern name
     */
    public String getNameOfRle() {
        return nameOfRle;
    }

    /**
     * Gets the high that is provided in the pattern
     * @return high of the pattern
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the width that is provided in the pattern
     * @return width of the pattern
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gives a pattern as an boolean array, that is interpret from the pattern given
     * @return pattern of
     */
    public boolean[][] getInitialRleGeneration() {
        return initialRleGeneration;
    }

    /**
     * Gets the author and date of the pattern, if there are any.
     * @return Author and/or date of pattern
     */
    public String getAuthorOfRle() {
        return authorOfRle;
    }

    /**
     * gets the survival rule of the pattern
     *
     * @return value of when a cell should survive
     */
    public String getSurvivalOfRle() {
        return survivalOfRle;
    }

    /**
     * gets the birth rule of the pattern
     *
     * @return value of when a cell should birth
     */
    public String getBirthOfRle() {
        return birthOfRle;
    }

    /**
     * gets the comment rule of the pattern, if there are any.
     *
     * @return comment that is provided in pattern.
     */
    public String getCommentOfRle() {
        return commentOfRle.toString();
    }
}
