package GameOfLife.Controller;

import GameOfLife.Model.PatternFormatException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class decodes/parses a RLE file, and then stores an array for later use.
 *
 * @author Johnny Lam, Markus Hellestveit
 */
public class RleInterpreter {
    private Matcher matcher;
    private String nameOfRle;
    private String authorOfRle;
    private String birthOfRle;
    private String survivalOfRle;
    private int height;
    private int width;
    private String rule;
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
     * @throws PatternFormatException
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
            } else if (matcher.group(1).equalsIgnoreCase("c")) {
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
     * Locates and determinate the the dimension and rule of the pattern in the
     * file.
     *
     * @throws PatternFormatException checks if width and height of the pattern is less that width
     *                                and height of game board
     */
    private void readDimensionAndRule() throws PatternFormatException {

        Pattern regex = Pattern.compile("x=([0-9]+),y=([0-9]+)(,rule=(([A-Za-z])*([0-9]*)/([A-Za-z])*([0-9]*)))",
                Pattern.MULTILINE);

        // System.out.println(this.rleString);
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

            if (matcher.group(5).equalsIgnoreCase("B")){
                birthOfRle = matcher.group(6);
                System.out.println("sadfe");
            }else if(matcher.group(5).equalsIgnoreCase("S")){
                survivalOfRle = matcher.group(6);
            }

            if (matcher.group(7).equalsIgnoreCase("B")){
                birthOfRle = matcher.group(8);
                System.out.println(matcher.group(7));
                System.out.println(birthOfRle);

            }else if(matcher.group(7).equalsIgnoreCase("S")){
                survivalOfRle = matcher.group(8);
                System.out.println("Survival:");
                System.out.println(matcher.group(7));

            }

            this.rule = matcher.group(3).replaceAll("[^/0-9]", "");
            lastIndexOfHeader = matcher.end() + amountOfSpaces;
        }

        testDimensionAndRule.append("x = ").append(this.width).append(", y = ").append(this.height).append(", rule = ")
                .append(this.rule).append("\n");
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
        // System.out.println(this.rleString.substring(lastIndexOfHeader));
        int aliveCells;
        int deadCells;
        int x = 0;
        String deadSymbol; // dead Cell
        String aliveSymbol; // alive Cell

        if (this.getHeight() < rlePattern.length)
            throw new PatternFormatException("Mismatch between given height dimension and actual height in pattern");

        for (int y = 0; y < rlePattern.length; y++) {
            Pattern regex = Pattern.compile("([0-9]*)([A-Za-z])");
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

            } //end of while((matcher.find())

            x = 0;
            if ((rlePattern[y].charAt(rlePattern[y].length() - 1) == '!')) {
                testGameBoard.append("!");
                break;
            } else {
                testGameBoard.append("$");

            }

        } // end of for-loop

    } // end of readBoard

    public String getTestHeader() {
        return testHeader.toString();
    }

    public String getTestDimensionAndRule() {
        return testDimensionAndRule.toString();
    }

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

    public int getCap() {
        return testGameBoard.length();
    }

    public String getNameOfRle() {
        return nameOfRle;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean[][] getInitialRleGeneration() {
        return initialRleGeneration;
    }

    public String getAuthorOfRle() {
        return authorOfRle;
    }

    public String getRule() {
        return rule;
    }

    public String getSurvivalOfRle() {
        return survivalOfRle;
    }

    public String getBirthOfRle() {
        return birthOfRle;
    }

    public String getCommentOfRle() {
        return commentOfRle.toString();
    }
}