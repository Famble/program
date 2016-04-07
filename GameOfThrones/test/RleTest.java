import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import GameOfLife.RleInterpreter;

public class RleTest {

	@Test
	public void test() throws IOException {
		String s = "#N Gosper glider gun\n" +
				"#C This was the first gun discovered.\n"+
				"#C As its name suggests, it was discovered by Bill Gosper.\n"+
				"x = 36, y = 9, rule = B3/S23\n" +
				"24bo$22bobo$12b2o6b2o12b2o$11bo3bo4b2o12b2o$2o8bo5bo3b2o$2o8bo3bob2o4b\n"+
				"obo$10bo5bo7bo$11bo3bo$12b2o!";
		
		RleInterpreter rleInt = new RleInterpreter(s);
		
		System.out.println("-----------------------------------------------");
		org.junit.Assert.assertEquals(rleInt.toString(), s);
		
		
	}

}
