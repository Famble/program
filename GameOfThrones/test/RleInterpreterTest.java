import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.Test;

import GameOfLife.Controller.RleInterpreter;
import GameOfLife.Model.PatternFormatException;

public class RleInterpreterTest {
	String stringOfFile = "#N Gosper glider gun\n"
			+ "#C This was the first gun discovered.\n"
			+ "#C As its name suggests, it was discovered by Bill Gosper.\n"
			+ "x = 36, y = 9, rule = S23/B3\n"
			+ "24bo$22bobo$12b2o6b2o12b2o$11bo3bo4b2o12b2o$2o8bo5bo3b2o$2o8bo3bob2o4b\n"
			+ "obo$10bo5bo7bo$11bo3bo$12b2o!";

	String stringOfFile2 = "#C This is a glider. \n"
			+ "#O unknown\n"
			+ "x = 3, y = 3\n"
			+ "bo$2bo$3o!";

	String stringOfFile3 = "#N This is a test\n"
			+ "#O Tester Johnny 2016/05/06\n"
			+ "x = 4, y = 3, rule = s3/b23\n"
			+ "bo$2bo$3o!thisShallNotBeRead";

	@Test
	public void testRegexForRleName() throws IOException, PatternFormatException {

		String nameExpect = "Gosper glider gun";

		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);
		assertEquals(rleInt.getNameOfRle(), nameExpect);

		RleInterpreter rleInt2 = new RleInterpreter(stringOfFile2, 10, 10, false);
		assertEquals(rleInt2.getNameOfRle(), null);

		RleInterpreter rleInt3 = new RleInterpreter(stringOfFile3, 10, 10, false);
		assertEquals(rleInt3.getNameOfRle(), "This is a test");

	}

	@Test
	public void testRegexForRleAuthor() throws IOException, PatternFormatException {

		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);
		assertEquals(rleInt.getAuthorOfRle(), null);

		RleInterpreter rleInt2 = new RleInterpreter(stringOfFile2, 10, 10, false);
		assertEquals(rleInt2.getAuthorOfRle(), "unknown");

		RleInterpreter rleInt3 = new RleInterpreter(stringOfFile3, 10, 10, false);
		assertEquals(rleInt3.getAuthorOfRle(), "Tester Johnny 2016/05/06");




	}
	@Test
	public void testRegexForRleComment() throws PatternFormatException {
		String commentExpect = "This was the first gun discovered.\n"
				+ "As its name suggests, it was discovered by Bill Gosper.\n";

		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);
		assertEquals(rleInt.getCommentOfRle(), commentExpect);

		RleInterpreter rleInt2 = new RleInterpreter(stringOfFile2, 10, 10, false);
		assertEquals(rleInt2.getCommentOfRle(), "This is a glider. \n");
	}

	@Test
	public void testRegexForRleWidth() throws PatternFormatException {
		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);
		assertEquals(rleInt.getWidth(),36);

		RleInterpreter rleInt2 = new RleInterpreter(stringOfFile2, 10, 10, false);
		assertEquals(rleInt2.getWidth(), 3);

		RleInterpreter rleInt3 = new RleInterpreter(stringOfFile3, 10, 10, false);
		assertEquals(rleInt3.getWidth(), 4);

	}

	@Test
	public void testRegexForRleHeight() throws PatternFormatException {
		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);
		assertEquals(rleInt.getHeight(),9);

		RleInterpreter rleInt2 = new RleInterpreter(stringOfFile2, 10, 10, false);
		assertEquals(rleInt2.getHeight(), 3);

		RleInterpreter rleInt3 = new RleInterpreter(stringOfFile3, 10, 10, false);
		assertEquals(rleInt3.getHeight(), 3);
	}

	@Test
	public void testRegexForRleBirth() throws PatternFormatException {
		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);
		assertEquals(rleInt.getBirthOfRle(),"3");

		RleInterpreter rleInt2 = new RleInterpreter(stringOfFile2, 10, 10, false);
		assertEquals(rleInt2.getBirthOfRle(), "3");

		RleInterpreter rleInt3 = new RleInterpreter(stringOfFile3, 10, 10, false);
		assertEquals(rleInt3.getBirthOfRle(), "23");
	}

	@Test
	public void testRegexForRleSurvival() throws PatternFormatException {
		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);
		assertEquals(rleInt.getSurvivalOfRle(),"23");

		RleInterpreter rleInt2 = new RleInterpreter(stringOfFile2, 10, 10, false);
		assertEquals(rleInt2.getSurvivalOfRle(), "23");

		RleInterpreter rleInt3 = new RleInterpreter(stringOfFile3, 10, 10, false);
		assertEquals(rleInt3.getSurvivalOfRle(), "3");
	}

	@Test
	public void testRegexForRleGameBoard() throws PatternFormatException {
		String gameBoardExpect = "24bo$22bobo$12b2o6b2o12b2o$11bo3bo4b2o12b2o$2o8bo5bo3b2o$2o8bo3bob2o4b"
				+ "obo$10bo5bo7bo$11bo3bo$12b2o!";

		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);
		assertEquals(rleInt.getTestGameBoard(), gameBoardExpect);

		RleInterpreter rleInt2 = new RleInterpreter(stringOfFile2, 10, 10, false);
		assertEquals(rleInt2.getTestGameBoard(), "bo$2bo$3o!");

		RleInterpreter rleInt3 = new RleInterpreter(stringOfFile3, 10, 10, false);
		assertEquals(rleInt3.getTestGameBoard(), "bo$2bo$3o!");



	}

	@Test
	public void testToString() throws PatternFormatException {
		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 50, 50, false);
		RleInterpreter rleInt2 = new RleInterpreter(stringOfFile2, 50, 50, false);

		String stringOfFile = "Gosper glider gun\n"
				+ "This was the first gun discovered.\n"
				+ "As its name suggests, it was discovered by Bill Gosper.\n"
				+ "x = 36, y = 9, rule = 23/3\n"
				+ "24bo$22bobo$12b2o6b2o12b2o$11bo3bo4b2o12b2o$2o8bo5bo3b2o$2o8bo3bob2o4b"
				+ "obo$10bo5bo7bo$11bo3bo$12b2o!";

		String stringOfFile2 = "This is a glider. \n"
				+ "unknown\n"
				+ "x = 3, y = 3, rule = null\n"
				+ "bo$2bo$3o!";


		assertEquals(rleInt.toString(),stringOfFile);
		assertEquals(rleInt2.toString(), stringOfFile2);
	}
}
