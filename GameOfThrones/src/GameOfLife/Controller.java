package GameOfLife;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

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
    @FXML
    private Toggle drawDrag;
    private GameOfLife GOL;
    boolean start = true;
    int offsetX = 0;
    CanvasDrawer cd;
    int offsetY = 0;
    Matrix model;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
	rules = new Rules();
	model = new Matrix(1000, (1000), rules);// 1000mill celler

	cd = new CanvasDrawer(model, canvas.getGraphicsContext2D());

	GOL = new GameOfLife(model, cd);
	

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
	    cd.setWindowWidth((double) c);
	});

	canvasParent.heightProperty().addListener((a, b, c) ->
	{
	    canvas.setHeight((double) c);
	    cd.setWindowHeight((double) c);
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

	    for (int i = 0; i < model.getWidth(); i++)
		for (int j = 0; j < model.getY(); j++)
		    model.getActiveCells()[i][j] = 0;

	    model.startNextGeneration();

	});

	amountOfCells.setText(
		String.format("%d Million Cells", ((int) model.getWidth() * model.getHeight() / (int) Math.pow(10, 6))));
	position.setText(String.format("(x, y): (%d,%d)", cd.getCanvasDisplacedX(), cd.getCanvasDisplacedY()));
	
	cd.clearCanvas();
    }

    public void handleOpen() throws IOException
    {
    	
    	FileHandler file = new FileHandler();
    	model.setPatternFromRle(file.readRle());
    	cd.drawPatternBackground(model.getPatternWidth(), model.getPatternHeight());
    	cd.drawNextGeneration();

    	
    }

    public void onDrawClicked()
    {
    	System.out.println(drawDrag.isSelected());
    }

    public void handleZoom(ScrollEvent event)
    {

		if (event.getDeltaY() > 1)
		{
		    cd.zoom(1, event);
		} else if (event.getDeltaY() < 1)
		{
		    sliderZoom.setValue(cd.getCellSize() - 1);
		    cd.zoom(-1, event);
		}
    }

    public void speedSliderDragged()
    {
    	GOL.setDelay(Math.pow(10, 9) * (1 / sliderSpeed.getValue()));
    }

    public void zoomSliderDragged()
    {
	
		if (cd.getCellSize() - (int) sliderZoom.getValue() == -1)
		    cd.zoom((int) 1);
		else if (cd.getCellSize() - (int) sliderZoom.getValue() == 1)
		    cd.zoom(-1);
	    
    }

    public void changeColor()
    {
		model.setColor(colorPicker.getValue());
		cd.drawNextGeneration();
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
	    cd.movePosition(offsetX - (int) event.getX(), offsetY - (int) event.getY());
	    offsetX = (int) event.getX();
	    offsetY = (int) event.getY();
	    position.setText(String.format("(x, y): (%d,%d)", (int) cd.getCanvasDisplacedX(), (int) cd.getCanvasDisplacedY()));
	    
	} else
	{
	    mouseClicked(event, drawDrag.isSelected());
	}

    }
    

    public void mouseClicked(MouseEvent event)
    {	
    	cd.drawCell((int)event.getX(), (int)event.getY());
	}
    
    public void mouseClicked(MouseEvent event, boolean dragDraw)
    {	
    	cd.drawCell((int)event.getX(), (int)event.getY(), dragDraw);
	}

    public void keyListener(KeyEvent event)
    {
	Platform.runLater(() ->
	{
	    canvas.requestFocus();
	});

    }

    

    public void handlePauseClick()
    {
	GOL.stop();
    }

    public void handleResetClick()
    {
		for (int i = 0; i < model.getWidth(); i++)
		    for (int j = 0; j < model.getY(); j++)
		    {
				model.getCurrentGeneration()[i][j] = 0;
				model.getNextGeneration()[i][j] = 0;
				model.getActiveCells()[i][j] = 0;
		    }
	
		cd.drawNextGeneration();
	    }

}