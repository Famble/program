import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import GameOfLife.Matrix;
import GameOfLife.Rules;

public class MatrixTest
{

    @Test
    public void testDetermineNextGeneration()
    {
	
	
	long[][] board = {{0L}, {6L}, {2L}, {0L}};
	Rules rules = new Rules();
	Matrix model = new Matrix(4, 4, rules);
	model.setBoard(board);

	model.startNextGeneration();
	
	
	org.junit.Assert.assertEquals(model.toString(), "0000011001100000");
	
			
    }
}
