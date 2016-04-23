package GameOfLife.Controller;

import GameOfLife.Model.DynamicGameBoard;
import GameOfLife.Model.GameBoard;
import GameOfLife.Model.RLEPattern;
import GameOfLife.Model.GameBoard.BoardContainer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class StripDrawer extends CanvasDrawer{

	private GraphicsContext gcStrip;
	RLEPattern pattern;

	public StripDrawer(GameBoard model, GraphicsContext gc, GraphicsContext gcStrip) {
		super(model, gc);
		this.gcStrip = gcStrip;
	}
	
	
	public void drawStrip(GameBoard stripModel){
		
		setPattern(stripModel);
		stripModel.getPattern().trim();
		
		for(int i = 0; i < 20; i++){
			for(int x = 0; x < stripModel.getPattern().getPatternWidth(); x++) {
				for(int y = 0; y < stripModel.getPattern().getPatternHeight(); y++){
					if(stripModel.getPattern().getPattern()[x][y]){
						gcStrip.setFill(Color.BLACK);
						gcStrip.fillRect(i*100 +x*3, 20 + y*3, 3, 3);
					}
				}
			}
			
			System.out.printf("(width, height) = (%d,%d)\n", stripModel.getWidth(), stripModel.getHeight());
			System.out.println("shiftedRight, shiftedDown = (%d,%d)");
			
			
			stripModel.startNextGeneration();
			setPattern(stripModel);
			stripModel.getPattern().trim();
			
			
		}
		
		
		
	}
	
	public void setPattern(GameBoard board){
		int shiftedRightwards = 0;
		int shiftedDownwards = 0;
		if (board instanceof DynamicGameBoard) {
			shiftedRightwards = ((DynamicGameBoard) this.model).getShiftedRightwards();
			shiftedDownwards = ((DynamicGameBoard) this.model).getShiftedDownwards();
		}
		RLEPattern pattern = new RLEPattern(board.getWidth(), board.getHeight());
		for(int x = 0; x < board.getWidth();x++)
			for(int y = 0; y< board.getHeight(); y++){
				if(board.getCellState(x-shiftedRightwards, y-shiftedDownwards, BoardContainer.CURRENTGENERATION))
					pattern.getPattern()[x][y] = true;
			}
		
		board.setPattern(pattern);
	}
	
	
	public void setStripPattern(RLEPattern pattern){
		this.pattern = pattern;
	}

	public RLEPattern getStripPattern(){
		return this.pattern;
	}

}
