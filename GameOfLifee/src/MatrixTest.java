import static org.junit.Assert.*;

import org.junit.Test;

public class MatrixTest {

	@Test
	public void testDetermineNextGeneration() {
		
		long[][] board = {
		
		             { 0, 0, 0, 0 },
		             { 0, 1, 1, 0 },
		             { 0, 1, 1, 0 },
		             { 0, 0, 0, 0 }
		};
		
		Matrix gol = new Matrix(4,1, new Rules());
		gol.setBoard(board);
		
		//gol.startNextGeneration();
		System.out.println(gol.toString());
		//org.junit.Assert.assertEquals(gol.toString(),"0000011001100000");
		

		}
	}
