import static org.junit.Assert.*;

import org.junit.Test;

import GameOfLife.Model.MatrixDynamic;

public class MatrixDynamicTest {

	@Test
	public void test() {
		MatrixDynamic matrix = new MatrixDynamic(100,100);
		matrix.setCellState(10, 10, matrix.getCurrGeneration(), true);
		boolean l = (matrix.getCellState(10, 10, matrix.getCurrGeneration()));
		org.junit.Assert.assertEquals("true", Boolean.toString(l));

	}

}
