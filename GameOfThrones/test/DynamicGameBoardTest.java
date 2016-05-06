import static org.junit.Assert.*;

import org.junit.Test;

import GameOfLife.Model.DynamicGameBoard;
import GameOfLife.Model.GameBoard;
import GameOfLife.Model.Rules;
import GameOfLife.Model.GameBoard.BoardContainer;



public class DynamicGameBoardTest {

	
	//DENNE TESTEN BLIE FEIL BTW
	public void enlargeRightAndLeft(){
		DynamicGameBoard dynamicBoard = DynamicGameBoard.getInstance();
		dynamicBoard.setNewWidthAndHeight(7, 4);
		
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
		DynamicGameBoard dynamicBoard = DynamicGameBoard.getInstance();
		dynamicBoard.setNewWidthAndHeight(4, 4);
		
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
	
	
	
	
	
}
