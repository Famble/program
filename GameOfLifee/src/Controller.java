import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Controller implements Initializable
{
	
    @FXML private Canvas canvas;
    @FXML private ColorPicker colorPicker;
    @FXML private Pane pane;
    @FXML private Slider slider;

    private GameOfLife GOL;
    
   
    
    @Override
	public void initialize(URL location, ResourceBundle resources)
	{
		Matrix model = new Matrix(1000, 125);//1mill celler
		
		GOL = new GameOfLife(model, new CanvasDrawer(model, canvas.getGraphicsContext2D()));
		slider.setMin(-1);
		slider.setMax(0);
		slider.valueProperty().addListener((observable, oldV, newV) ->{
			
		}); 
	}
    
    public void handleSliderChnge()
	{
    	
	}
    
	public void handleStartClick()
	{	
		GOL.startGame();

	}
	
	public void mouseDragged(MouseEvent event)
	{
		GOL.selectCell(event);
	}
	
	public void keyListener(KeyEvent event)
	{
		  Platform.runLater(new Runnable()
		  {
              @Override
              public void run() 
              {
                  canvas.requestFocus();
              }
           });
              
		if(event.getCode() == KeyCode.RIGHT)
			GOL.movePosition(10, 0);
		else if(event.getCode() == KeyCode.DOWN)
			GOL.movePosition(0, 10);
		else if(event.getCode() == KeyCode.LEFT)
			GOL.movePosition(-10, 0);
		else if(event.getCode() == KeyCode.UP)
			GOL.movePosition(0, -10);
		else if(event.getCode() == KeyCode.PLUS)
			GOL.zoom(1);
		else if(event.getCode() == KeyCode.MINUS)
			GOL.zoom(-1);
		
		
			
	}
	
	public void mouseClicked(MouseEvent event) 
	{
		if(event.isControlDown())
			GOL.zoom(1, event);
		else if(event.isShiftDown())
		{
			GOL.zoom(-1, event);
		}
		else
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