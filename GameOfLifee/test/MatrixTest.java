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
	
	long[][] board = {{0L}, {6L}, {6L}, {0L}};
	Rules rules = new Rules();
	Matrix model = new Matrix(4, 4, rules);
	model.setBoard(board);
	
	System.out.println(model.toString());

	model.startNextGeneration();
	
	System.out.println(model.toString());	
	
	//org.junit.Assert.assertEquals(model.toString(), "0000011001100000");
	
			
    }
}
