import java.io.IOException;

import org.junit.Test;

import GameOfLife.RleInterpreter;
import GameOfLife.Model.PatternFormatException;

public class RleTest {

	@Test
	public void testRegexForRleInterpreter() throws IOException, PatternFormatException {
		String stringOfFile = "#N Gosper glider gun\n" + "#C This was the first gun discovered.\n"
				+ "#C As its name suggests, it was discovered by Bill Gosper.\n" + "x = 36, y = 9, rule = B3/S23\n"
				+ "24bo$22bobo$12b2o6b2o12b2o$11bo3bo4b2o12b2o$2o8bo5bo3b2o$2o8bo3bob2o4b\n"
				+ "obo$10bo5bo7bo$11bo3bo$12b2o!";

		String nameExpect = "Gosper glider gun";

		String commentExpect = "This was the first gun discovered.\n"
				+ "As its name suggests, it was discovered by Bill Gosper.\n";

		String dimensionAndRuleExpect = "x = 36, y = 9, rule = 3/23\n";

		String gameBoardExpect = "24bo$22bobo$12b2o6b2o12b2o$11bo3bo4b2o12b2o$2o8bo5bo3b2o$2o8bo3bob2o4b"
				+ "obo$10bo5bo7bo$11bo3bo$12b2o!";

		RleInterpreter rleInt = new RleInterpreter(stringOfFile, 100, 100, false);

		org.junit.Assert.assertEquals(rleInt.getNameOfRle(), nameExpect);
		org.junit.Assert.assertEquals(rleInt.getDescription(), commentExpect);
		org.junit.Assert.assertEquals(rleInt.getTestDimensionAndRule(), dimensionAndRuleExpect);
		org.junit.Assert.assertEquals(rleInt.getTestGameBoard(), gameBoardExpect);

	}

}
