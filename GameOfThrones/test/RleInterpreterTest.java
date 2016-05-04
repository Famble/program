import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.Test;

import GameOfLife.RleInterpreter;
import GameOfLife.Model.PatternFormatException;

public class RleInterpreterTest {
	String stringOfFile = "#N Gosper glider gun\n"
			+ "#C This was the first gun discovered.\n"
			+ "#C As its name suggests, it was discovered by Bill Gosper.\n"
			+ "x = 36, y = 9, rule = B3/S23\n"
			+ "24bo$22bobo$12b2o6b2o12b2o$11bo3bo4b2o12b2o$2o8bo5bo3b2o$2o8bo3bob2o4b\n"
			+ "obo$10bo5bo7bo$11bo3bo$12b2o!";


	@Test
	public void testRegexForRleName() throws IOException, PatternFormatException {

		String nameExpect = "Gosper glider gun";

		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);

		assertEquals(rleInt.getNameOfRle(), nameExpect);

	}

	@Test
	public void testRegexForRleAuthor() throws IOException, PatternFormatException {

		String authorExpect = null;

		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);

		assertEquals(rleInt.getAuthorOfRle(), authorExpect);

	}
	@Test
	public void testRegexForRleComment() throws PatternFormatException {
		String commentExpect = "This was the first gun discovered.\n"
				+ "As its name suggests, it was discovered by Bill Gosper.\n";

		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);

		assertEquals(rleInt.getCommentOfRle(), commentExpect);
	}

	@Test
	public void testRegexForRleWidth() throws PatternFormatException {
		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);

		assertEquals(rleInt.getWidth(),36);
	}

	@Test
	public void testRegexForRleHeight() throws PatternFormatException {
		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);

		assertEquals(rleInt.getHeight(),9);
	}

	@Test
	public void testRegexForRleBirth() throws PatternFormatException {
		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);

		assertEquals(rleInt.getBirthOfRle(),"3");
	}

	@Test
	public void testRegexForRleSurvival() throws PatternFormatException {
		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);

		assertEquals(rleInt.getSurvivalOfRle(),"23");
	}

	@Test
	public void testRegexForRleGameBoard() throws PatternFormatException {
		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);

		String gameBoardExpect = "24bo$22bobo$12b2o6b2o12b2o$11bo3bo4b2o12b2o$2o8bo5bo3b2o$2o8bo3bob2o4b"
				+ "obo$10bo5bo7bo$11bo3bo$12b2o!";

		assertEquals(rleInt.getTestGameBoard(), gameBoardExpect);
	}

	@Test
	public void testToString() throws PatternFormatException {
		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);

		String stringOfFile = "Gosper glider gun\n"
				+ "This was the first gun discovered.\n"
				+ "As its name suggests, it was discovered by Bill Gosper.\n"
				+ "x = 36, y = 9, rule = 3/23\n"
				+ "24bo$22bobo$12b2o6b2o12b2o$11bo3bo4b2o12b2o$2o8bo5bo3b2o$2o8bo3bob2o4b"
				+ "obo$10bo5bo7bo$11bo3bo$12b2o!";


		assertEquals(rleInt.toString(),stringOfFile);
	}
}
