import static org.junit.Assert.*;

import org.junit.Test;

import GameOfLife.Model.DynamicGameBoard;
import GameOfLife.Model.GameBoard;
import GameOfLife.Model.Rules;
import GameOfLife.Model.GameBoard.BoardContainer;



public class DynamicGameBoardTest {
/*
	
	@Test
	public void enlargeRightAndLeft(){
		Rules rules = new Rules();
		DynamicGameBoard dynamicBoard = new DynamicGameBoard(7,3, rules);
		dynamicBoard.setExpansionPadding(0);
		
		//glider going left and right
		boolean[][] board = { 
				{true, true, true},
				{true, false, false},
				{false, true, false},
				{false, false, false},
				{false, true, false},
				{false, false, true},
				{true, true, true}
		};
		
		String expectedResult = "000000000011000100110000011001000110000000000";	
		
		
		dynamicBoard.setBoard(board);
		dynamicBoard.nextGenerationConcurrent();
		
		System.out.println();

		org.junit.Assert.assertEquals(dynamicBoard.toString(), expectedResult);
	}
	
	
	
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
		
		
		String expectedResult =
								"0000"
				+ 				"0110"
				+ 				"0110"
				+ 				"0000";
		
		
		dynamicBoard.setBoard(board);
		
		dynamicBoard.nextGenerationConcurrent();
		
		System.out.println(dynamicBoard.toString());
		
		
		org.junit.Assert.assertEquals(dynamicBoard.toString(), expectedResult );

	}
	
	*/
	
	
	
}
