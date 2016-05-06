package GameOfLife.Model;

/**
 * This class ensures that only one instance of a GameBoard subclass can exist
 * and provides a global access point to that instance. Enables polymorphic
 * behaviour for classes that required an instance of either DyanmicaGameBoard
 * or BitGameBoard, like CanvasDrawer or ExecutionController
 * 
 * @see GameOfLife.Controller.CanvasDrawer
 * @see GameOfLife.Controller.ExecutionControl
 * @author Markus Hellestveit
 *
 */
public class GameBoardFactorySingleTon {

	public enum GameBoardType {
		DynamicGameBoard, BitGameBoard
	}

	static GameBoard board = null;

	/**
	 * creates an instance of the game board specified by the GameBoardType enum if none exists,
	 * or returns the exiting GameBoard instance if one exists
	 * @param gameBoard enum that specifies which game board to get an instance of
	 * @return instance of a GameBoard subclass.
	 */
	public static GameBoard getInstance(GameBoardType gameBoard) {
		if (board == null) {
			if (gameBoard == GameBoardType.DynamicGameBoard) {
				board = DynamicGameBoard.getInstance();

			} else if (gameBoard == GameBoardType.BitGameBoard) {
				board = BitGameBoard.getInstance();

			}

		}

		return board;
	}

	/**
	 * if no parameters are specified this method will return an instance of
	 * DynamicGameBoard or create on if it doesn't exist
	 * 
	 * @return
	 */
	public static GameBoard getInstance() {
		if (board == null)
			board = DynamicGameBoard.getInstance();

		return board;

	}

}