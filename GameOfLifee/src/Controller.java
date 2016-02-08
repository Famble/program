import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Controller implements Initializable
{
	
    @FXML private Canvas canvas;
    @FXML private ColorPicker colorPicker;
    private GameOfLife GOL;
   
    
    @Override
	public void initialize(URL location, ResourceBundle resources)
	{

		Matrix model = new Matrix(80, 50);
		
		GOL = new GameOfLife(model, new CanvasDrawer(model, canvas.getGraphicsContext2D()));	
	}
    
    public void handleSliderChnge()
	{
    	
	}
    
	public void handleStartClick()
	{	
		GOL.startGame();
	}
	
	public void mouseClicked(MouseEvent event) 
	{	
		GOL.selectCell(event);
	}
	
	public void handlePauseClick()
	{
		GOL.stop();
	}
	
	public void handleResetClick()
	{
		GOL.resetGame();
	}
	
	public void handleColChngClick()
	{
		
	}
	
	public void handleSaveClick()
	{
	}
}