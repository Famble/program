package GameOfLife;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Arrays;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;

public class Controller implements Initializable
{

    @FXML
    private Canvas canvas;
    @FXML
    private Slider sliderSpeed;
    @FXML
    private Slider sliderZoom;
    @FXML
    private HBox canvasParent;
    @FXML
    private ComboBox<?> comboBox;
    @FXML
    private Button startButton;
    @FXML
    private TextField survival;
    @FXML
    private TextField birth;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Label amountOfCells;
    @FXML
    private Label position;
    @FXML
    Rules rules;
    MeshView mesh;
    private GameOfLife GOL;
    boolean start = true;
    int offsetX = 0;
    CanvasDrawer cd;
    int offsetY = 0;
    Box BOX;
    Matrix model;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
	rules = new Rules();
	model = new Matrix(10000, (10000), rules);// 100mill celler
	cd = new CanvasDrawer(model, canvas.getGraphicsContext2D());
	
	GOL = new GameOfLife(model, cd);
	
	
	
	
	canvas.setOnZoom((zoomEvent) ->
	{
	    
	});
	
	colorPicker.setValue(Color.web("#42dd50"));
	
	

	survival.textProperty().addListener((observable, oldValue, survivalString) ->
	{
	    this.rules.setUserDefinedSurvivalRules(survivalString);
	});

	birth.textProperty().addListener((observable, oldValue, birthString) ->
	{
	    this.rules.setUserDefinedBirthRules(birthString);

	});
	

	canvasParent.widthProperty().addListener((a, b, c) ->
	{
	    canvas.setWidth((double) c);
	    cd.setWindowWidth((double)c);
	});
	 
	
	
	canvasParent.heightProperty().addListener((a,b,c) ->
	{
	    canvas.setHeight((double) c);
	    cd.setWindowHeight((double)c);
	});

	comboBox.valueProperty().addListener((observable, oldValue, survivalString) ->
	{
	    
	    survival.setText("");
	    birth.setText("");
	    rules.setRules(comboBox.getValue().toString());

	    for (int i : rules.getSurvivalRules())
		survival.setText(survival.getText() + i);

	    for (int i : rules.getSurvivalRules())
		birth.setText(birth.getText() + i);
	    
	    
	    for(int i = 0; i < model.getX(); i++)
		for(int j = 0; j < model.getY(); j++)
		    model.getActiveCells()[i][j] = 0;
	    
	    model.startNextGeneration();

	    
	});

	amountOfCells.setText(String.format("%d Million Cells", ((int)model.getX()*model.getRealY()/(int)Math.pow(10, 6))));
	position.setText(String.format("(x, y): (%d,%d)", cd.getpositionX(), cd.getpositionY()));
    }

    public void handleZoom(ZoomEvent event)
    {

	if (event.getZoomFactor() > 2)
	{
	    GOL.zoom(1, event);
	} else if(event.getZoomFactor() < 0.7)
	{
	    GOL.zoom(-1, event);
	}

    }

    
    public void speedSliderDragged()
    {
	GOL.setDelay(Math.pow(10, 9) * (1 / sliderSpeed.getValue()));

    }

    public void zoomSliderDragged()
    {	
	if(cd.getCellSize() - (int)sliderZoom.getValue() == -1)
	    GOL.zoom((int) 1);
	else
	    if(cd.getCellSize() - (int)sliderZoom.getValue() == 1)
		GOL.zoom(-1);
    }

    public void changeColor()
    {
	model.setColor(colorPicker.getValue());
	GOL.getCanvasDrawer().drawNextGeneration();
    }

    public void handleStartClick()
    {
	if (start)
	{

	    GOL.startGame();
	    startButton.setText("Stop");
	    
	} else
	{
	    GOL.stop();
	    startButton.setText("Play");
	}
	start = !start;
    }

    public void handleMouseEntered(MouseEvent event)
    {
	offsetX = (int) event.getX();
	offsetY = (int) event.getY();
    }

    public void mouseDragged(MouseEvent event)
    {
	if (event.isControlDown())
	{

	    GOL.movePosition(offsetX - (int) event.getX(), offsetY - (int) event.getY());
	    offsetX = (int) event.getX();
	    offsetY = (int) event.getY();
	    position.setText(String.format("(x, y): (%d,%d)", (int)cd.getpositionX(), (int)cd.getpositionY()));    
	} 
	else
	  {
	    GOL.selectCell(event);
	  }

    }

    public void keyListener(KeyEvent event)
    {
	Platform.runLater(() ->
	{
		canvas.requestFocus();
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