package GameOfLife.Controller;

import GameOfLife.Model.DynamicGameBoard;
import GameOfLife.Model.GameBoard;
import GameOfLife.Model.RLEPattern;
import GameOfLife.Model.GameBoard.BoardContainer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

public class StripDrawer extends CanvasDrawer{

	private GraphicsContext gcStrip;
	RLEPattern pattern;
	private double stripWidth;
	private double stripHeight;

	public double getStripHeight() {
		return stripHeight;
	}


	public void setStripHeight(double stripHeight) {
		this.stripHeight = stripHeight;
	}


	public StripDrawer(GameBoard model, GraphicsContext gc, GraphicsContext gcStrip) {
		super(model, gc);
		this.gcStrip = gcStrip;
	}
	
	
	public void drawStrip(GameBoard stripModel){
		
		Affine transform = new Affine();
		double tx = 0;
		setPattern(stripModel);
		RLEPattern pattern = stripModel.getPattern();
		pattern.trim();
		
		stripHeight = 0;
		
		stripWidth +=tx;
		
		double cellSize = 90 / Math.min(pattern.getHeight(), pattern.getWidth());
		System.out.printf("cellsize, patternHeight, patternWidth", cellSize, pattern.getHeight(), pattern.getWidth());

		int height = (int) (pattern.getHeight()*cellSize + 20*cellSize);
		for(int i = 0; i < 20; i++){
			for(int x = 0; x < pattern.getWidth(); x++) {
				for(int y = 0; y < pattern.getHeight(); y++){
					transform.setTx(tx);
					gcStrip.setTransform(transform);
					if(pattern.getPattern()[x][y]){
						gcStrip.setFill(Color.BLACK);
						gcStrip.strokeLine(0, 0, 0, height);
						//gcStrip.strokeRect(0, 0, pattern.getWidth()*cellSize, pattern.getHeight()*cellSize);
						gcStrip.fillOval(x*cellSize, y*cellSize, cellSize, cellSize);
					}
				}
			}
			

			
			stripHeight = Math.max(stripHeight, pattern.getHeight()*cellSize);
			tx+= 20 + cellSize*pattern.getWidth();
			stripWidth+=tx;
			stripModel.nextGenerationConcurrent();;
			setPattern(stripModel);
			pattern = stripModel.getPattern();
			pattern.trim();
			
		}
		
		
		
	}
	
	public double getStripWidth() {
		return stripWidth;
	}


	public void setStripWidth(double stripWidth) {
		this.stripWidth = stripWidth;
	}


	public void setPattern(GameBoard board){
		int shiftedRightwards = 0;
		int shiftedDownwards = 0;
		if (board instanceof DynamicGameBoard) {
			shiftedRightwards = ((DynamicGameBoard) this.model).getInsertedColumnsFromLeft();
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
