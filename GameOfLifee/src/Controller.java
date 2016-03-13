import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

import javax.rmi.ssl.SslRMIClientSocketFactory;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;


public class Controller implements Initializable
{
	
    @FXML private Canvas canvas;
    @FXML private Slider sliderSpeed;
    @FXML private Slider sliderZoom;
    @FXML private HBox canvasParent;
    @FXML private Button startButton;
    @FXML private TextField survival;
    @FXML private TextField birth;
    @FXML private ColorPicker colorPicker;
    private GameOfLife GOL;
    boolean start = true;
    int offsetX = 0;
    int offsetY = 0;
    Matrix model;
   
    
    @Override
	public void initialize(URL location, ResourceBundle resources)
	{
		model = new Matrix(1000, 125);//1mill celler
		
		GOL = new GameOfLife(model, new CanvasDrawer(model, canvas.getGraphicsContext2D()));
			
		
		canvas.setOnZoom(new EventHandler<ZoomEvent>() {
            @Override public void handle(ZoomEvent event) {
               System.out.println("hey");
            }
        });
		
		survival.textProperty().addListener((observable, oldValue, newValue) -> 
		{
		   int[] array = new int[newValue.length()];
		   for(int l = 0; l < newValue.length(); l++)
		   {
			   array[l] = (int)Character.getNumericValue(newValue.charAt(l));
		   }
		   
		   model.setSurvivalRules(array);
		   
		});
		
		birth.textProperty().addListener((observable, oldValue, newValue) -> 
		{
		   int[] array = new int[newValue.length()];
		   for(int l = 0; l < newValue.length(); l++)
		   {
			   array[l] = (int)Character.getNumericValue(newValue.charAt(l));
		   }
		   
		   model.setBirthRules(array);
		  
		   
		});
		
	
		
		canvasParent.widthProperty().addListener(new ChangeListener<Number>() 
		{

            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) 
	            {
	                canvas.setWidth((double) t1);
	      
	            }
	        });
				
		canvasParent.heightProperty().addListener(new ChangeListener<Number>() {
		
		

	     public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) 
	     {
	    	 canvas.setHeight((double) t1);
	     }
	     });
		
		
			
	}
    
    
    
    public void handleZoom(ZoomEvent event)
    {
    	System.out.println("hey");
    }
    public void sliderDragged()
    {
    	GOL.setDelay(Math.pow(10, 9)*(1/sliderSpeed.getValue()));
    	GOL.zoom((int) sliderZoom.getValue());
    	
    }
    
    public void changeColor()
    {
    	model.setColor(colorPicker.getValue());
    	GOL.getCanvasDrawer().drawNextGeneration();
    }
    
    
	public void handleStartClick()
	{	
		if(start)
		{
				
			GOL.startGame();
			startButton.setText("Stop");
		}
		else
		{
			GOL.stop();
			startButton.setText("Play");
		}
		start = !start;	
	}
	

	
	public void handleMouseEntered(MouseEvent event)
	{
		offsetX = (int)event.getX();
		offsetY = (int)event.getY();
	}
	
	public void mouseDragged(MouseEvent event)
	{
		if(event.isControlDown())
		{	
			
			GOL.movePosition(offsetX-(int)event.getX(), offsetY-(int)event.getY());
			offsetX = (int) event.getX();
			offsetY = (int) event.getY();
		}
		else
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
	
}