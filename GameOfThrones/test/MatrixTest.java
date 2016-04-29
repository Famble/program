import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import GameOfLife.Model.Rules;
import GameOfLife.Model.BitGameBoard;

public class MatrixTest {

	long[][] board = { { 0L }, { 6L }, { 2L }, { 0L } };
	Rules rules = new Rules();
	BitGameBoard model = new BitGameBoard(4, 4, rules);

	@Test
	public void testStartNextGeneration() {

		model.setBoard(board);
		model.startNextGeneration();
		assertEquals(model.toString(), "0000011001100000");

	}

	@Test
	public void testResetGameBoard() {
		model.setBoard(board);
		model.resetGameBoard();
		assertEquals(model.toString(), "0000000000000000");

	}

	@Test
	public void testCountNeighbours() {
		
	}
}
