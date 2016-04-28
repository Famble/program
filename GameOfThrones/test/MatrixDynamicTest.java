import static org.junit.Assert.*;

import org.junit.Test;

import GameOfLife.Model.DynamicGameBoard;
import GameOfLife.Model.GameBoard;
import GameOfLife.Model.Rules;
import GameOfLife.Model.GameBoard.BoardContainer;



public class MatrixDynamicTest {

	@Test
	public void TestEnlargeToLeft() {
		Rules rules = new Rules();
		DynamicGameBoard dynamicBoard = new DynamicGameBoard(4,4, rules);
		dynamicBoard.setExpansionPadding(0);
		
		boolean[][] board = {
				{false, false, false, false},
				{false, true, true, false},
				{false, true, true, false},
				{false, false, false, false}

		};
		
		
		String expectedResult = "000000"
				+				"000000"
				+ 				"001100"
				+ 				"001100"
				+ 				"000000"
				+               "000000";
		
		
		dynamicBoard.setBoard(board);
		
		dynamicBoard.nextGenerationConcurrent();
		
		System.out.println(dynamicBoard.toString());
		
		
		org.junit.Assert.assertEquals(dynamicBoard.toString(), expectedResult );

	}
	
	
}
