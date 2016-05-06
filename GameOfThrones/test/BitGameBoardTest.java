import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import GameOfLife.Model.Rules;
import GameOfLife.Model.BitGameBoard;
import GameOfLife.Model.GameBoardFactorySingleTon;
import GameOfLife.Model.GameBoardFactorySingleTon.GameBoardType;

@SuppressWarnings("deprecation")
public class BitGameBoardTest {

    long[][] board = {{0L}, {6L}, {6L}, {0L}};
    Rules rules = new Rules();
    BitGameBoard model = (BitGameBoard) GameBoardFactorySingleTon.getInstance(GameBoardType.BitGameBoard);

    @Test
    public void testStartNextGeneration() {

    	model.setNewWidthAndHeight(4, 4);
        model.setBoard(board);
        model.nextGeneration();
        assertEquals(model.toString(), "0000011001100000");

    }

    @Test
    public void testResetGameBoard() {
    	model.setNewWidthAndHeight(4, 4);
        model.setBoard(board);
        model.resetGameBoard();
        assertEquals(model.toString(), "0000000000000000");

    }

    @Test
    public void testCountNeighbours() {
    	model.setNewWidthAndHeight(4, 4);
        model.setBoard(board);
        String strgNeighbours = "";
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                strgNeighbours += model.countNeighbours(i, j);

            }
        }

        assertEquals(strgNeighbours,"1221233223321221");
    }
}
