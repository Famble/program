import static org.junit.Assert.*;

import org.junit.Test;

import GameOfLife.Model.DynamicMatrix;
import GameOfLife.Model.Rules;

public class MatrixDynamicTest {

	@Test
	public void testEnlargeFunctionBoth() {
		Rules rules = new Rules();
		DynamicMatrix matrix = new DynamicMatrix(100,100, rules);
		matrix.setCellState(900, 500, true);
		boolean l = (matrix.getCellState(900, 500));
		org.junit.Assert.assertEquals("true", Boolean.toString(l));

	}
	
	
	public void testEnlargeFunctionWidth() {
		Rules rules = new Rules();
		DynamicMatrix matrix = new DynamicMatrix(100,100, rules);
		matrix.setCellState(900, 50, true);
		boolean l = (matrix.getCellState(900, 50));
		org.junit.Assert.assertEquals("true", Boolean.toString(l));

	}
	
	public void testEnlargeFunctionHeight() {
		Rules rules = new Rules();
		DynamicMatrix matrix = new DynamicMatrix(100,100, rules);
		matrix.setCellState(50, 900, true);
		boolean l = (matrix.getCellState(50, 900));
		org.junit.Assert.assertEquals("true", Boolean.toString(l));

	}
	
}
