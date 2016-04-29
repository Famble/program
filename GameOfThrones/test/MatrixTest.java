import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import GameOfLife.Model.Rules;
import GameOfLife.Model.BitGameBoard;

public class MatrixTest
{

    @Test
    public void testStartNextGeneration()
    {
	
	
	long[][] board = {{0L}, {6L}, {2L}, {0L}};
	Rules rules = new Rules();
		BitGameBoard model = new BitGameBoard(4, 4, rules);
	model.setBoard(board);

	model.startNextGeneration();
	
	
	org.junit.Assert.assertEquals(model.toString(), "0000011001100000");
	
			
    }
}
