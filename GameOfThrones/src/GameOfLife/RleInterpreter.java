package GameOfLife;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RleInterpreter {
	private Pattern pattern;
	private Matcher matcher;
	private String name;
	private String comment;
	private int height;
	private int width;
	private String ruleString;
	private String rleString;
	private StringBuilder description = new StringBuilder();
	private long[][] startGeneration;
	private int lastIndexOfHeader;
	private StringBuilder testHeader = new StringBuilder();
	private StringBuilder testDimensionAndRule = new StringBuilder();
	private StringBuilder testGameboard = new StringBuilder();

	
	public String getTestHeader() {
		return testHeader.toString();
	}
	
	public String getTestDimensionAndRule() {
		return testDimensionAndRule.toString();
	}

	public String getTestGameboard() {
		return testGameboard.toString();
	}

	@Override
	public String toString() {

		String rleText = testHeader.toString() + testDimensionAndRule.toString() + testGameboard.toString();
		return rleText;
	}

	public int getCap() {
		return testGameboard.length();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public long[][] getStartGeneration() {
		return startGeneration;
	}

	public void setStartGeneration(long[][] startGeneration) {
		this.startGeneration = startGeneration;
	}

	public RleInterpreter(String rleFile) throws PatternFormatException{
		this.rleString = rleFile;
		readHeader();
		readDimensionAndRule();
		readGameboard();

		int cellsInLong = 64;
		for (int j = 0; j < this.height / 64 + 1; j++) {
			if (j == this.height / 64)
				cellsInLong = height % 64;

			for (int bitPos = 0; bitPos < cellsInLong; bitPos++) {
				for (int i = 0; i < this.width; i++) {
				}
			}
		}

	}

	private void readHeader() throws PatternFormatException {

		Pattern regex = Pattern.compile("^#([N|C|O|P|R])\\s*(\\w.*)$", Pattern.MULTILINE);
		Matcher matcher = regex.matcher(this.rleString);

		while (matcher.find()) {
			if (matcher.group(1).equalsIgnoreCase("N")) {
				name = matcher.group(2);
				testHeader.append("#N " + name + "\n");
				// System.out.println(name);
			} else if (matcher.group(1).equalsIgnoreCase("C")) {
				comment = matcher.group(2);
				description.append(comment + "\n");
				testHeader.append("#C " + comment + "\n");
				// System.out.println(description.toString());
			}

		}
	}

	private void readDimensionAndRule() throws PatternFormatException{

		Pattern regex = Pattern.compile("x=([0-9]+),y=([0-9]+)(,rule=([A-Za-z]*[0-9]*/[A-Za-z]*[0-9]*))",
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
			this.ruleString = matcher.group(3).replaceAll("[^/0-9]", "");
			lastIndexOfHeader = matcher.end() + amountOfSpaces;
		}

		testDimensionAndRule
				.append("x = " + this.width + ", y = " + this.height + ", rule = " + this.ruleString + "\n");

		// System.out.printf("width, height:(%d,%d)\n", this.width,
		// this.height);
		// System.out.println(this.ruleString);

	}

	private void readGameboard() throws PatternFormatException{
		this.startGeneration = new long[width][height / 64 + 1];

		String rlePattern[] = this.rleString.substring(lastIndexOfHeader).split("\\$");
		// System.out.println(this.rleString.substring(lastIndexOfHeader));
		int aliveCells = 0;
		int deadCells = 0;
		int y = 0;
		int x = 0;
		int bitPos = 0;
		int yDiv64 = 0;
		String dCell = "b"; // for testing
		String aCell = "o"; // for testing

		for (String hey : rlePattern) {
			pattern = Pattern.compile("([0-9]*)([A-Za-z])");
			matcher = pattern.matcher(hey);
			//Pattern endPattern = Pattern.compile("([!])");
			//Matcher endMatcher = endPattern.matcher(String.valueOf(hey.charAt(hey.length() - 1)));
			while (matcher.find()) {

				if (matcher.group(2).equals("b")) // dead cells
				{
					if (matcher.group(1).matches("[0-9]+")) {
						dCell = matcher.group(1);
						deadCells = Integer.parseInt(dCell);
						testGameboard.append(dCell);
					} else {
						deadCells = 1;
					}

					if (!(testGameboard.charAt(testGameboard.length() - 1) == 'b')) {
						testGameboard.append("b");
					}

					for (int i = 0; i < deadCells; i++) {
						this.startGeneration[x + i][yDiv64] &= ~(1L << bitPos);
					}

					x += deadCells;

				} else if (matcher.group(2).equals("o")) {

					if (matcher.group(1).matches("[0-9]+")) {
						aCell = matcher.group(1);
						aliveCells = Integer.parseInt(aCell);
						testGameboard.append(aCell);
					} else {
						aliveCells = 1;
					}

					if (!(testGameboard.charAt(testGameboard.length() - 1) == 'o')) {
						testGameboard.append("o");
					}

					for (int i = 0; i < aliveCells; i++) {
						this.startGeneration[x + i][yDiv64] |= (1L << bitPos);
					}

					x += aliveCells;
				}

				if (x != width) {
					int remainingCells = width - x;
					for (int i = 0; i < remainingCells; i++)
						this.startGeneration[x + i][yDiv64] &= ~(1L << bitPos);

				}

			}
			y++;
			x = 0;
			yDiv64 = y / 64;
			bitPos = y % 64;

			if (hey.charAt(hey.length() - 1) == '!') {
				System.out.println("test");
				testGameboard.append("!");
				break;
			}

			testGameboard.append("$");

		}
	}
}
