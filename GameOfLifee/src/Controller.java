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
    Rules rules;
    private GameOfLife GOL;
    boolean start = true;
    int offsetX = 0;
    int offsetY = 0;
    Matrix model;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
	rules = new Rules();

	model = new Matrix(1000, 16, rules);// 25mill celler
	GOL = new GameOfLife(model, new CanvasDrawer(model, canvas.getGraphicsContext2D()));

	canvas.setOnZoom(new EventHandler<ZoomEvent>()
	{
	    @Override
	    public void handle(ZoomEvent event)
	    {

	    }
	});

	survival.textProperty().addListener((observable, oldValue, survivalString) ->
	{
	    this.rules.setUserDefinedSurvivalRules(survivalString);

	});

	birth.textProperty().addListener((observable, oldValue, birthString) ->
	{
	    this.rules.setUserDefinedBirthRules(birthString);

	});

	canvasParent.widthProperty().addListener(new ChangeListener<Number>()
	{

	    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1)
	    {
		canvas.setWidth((double) t1);

	    }
	});

	canvasParent.heightProperty().addListener(new ChangeListener<Number>()
	{

	    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1)
	    {
		canvas.setHeight((double) t1);
	    }
	});

	comboBox.valueProperty().addListener((observable, oldValue, survivalString) ->
	{
	    {
		survival.setText("");
		birth.setText("");
		rules.setRules(comboBox.getValue().toString());

		for (int i : rules.getSurvivalRules())
		    survival.setText(survival.getText() + i);

		for (int i : rules.getSurvivalRules())
		    birth.setText(birth.getText() + i);

	    }
	});

	amountOfCells.setText(String.format("%.2f Million Cells", (model.getX() * model.getY() * 64) / 1000000.0));

    }

    public void handleZoom(ZoomEvent event)
    {

	if (event.getZoomFactor() > 1)
	{
	    GOL.zoom(1, event);
	} else
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
	GOL.zoom((int) sliderZoom.getValue());

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
	} else
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