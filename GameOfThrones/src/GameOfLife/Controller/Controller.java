package GameOfLife.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import GameOfLife.FileHandler;
import GameOfLife.GameOfLife;
import GameOfLife.RleInterpreter;
import GameOfLife.Model.DynamicMatrix;
import GameOfLife.Model.Matrix.BoardContainer;
import GameOfLife.Model.PatternFormatException;
import GameOfLife.Model.RLEPattern;
import GameOfLife.Model.Rules;
import GameOfLife.Model.StaticMatrix;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class Controller implements Initializable {

	@FXML
	private VBox vBoxRoot;
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
	DynamicMatrix model;
	Pattern pattern;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		rules = new Rules();
		model = new DynamicMatrix(100, (100), rules);

		cd = new CanvasDrawer(model, canvas.getGraphicsContext2D());

		GOL = new GameOfLife(model, cd);

		canvas.setOnZoom((zoomEvent) -> {

		});

		colorPicker.setValue(Color.web("#42dd50"));

		survival.textProperty().addListener((observable, oldValue, survivalString) -> {
			this.rules.setUserDefinedSurvivalRules(survivalString);
		});

		birth.textProperty().addListener((observable, oldValue, birthString) -> {
			this.rules.setUserDefinedBirthRules(birthString);

		});
		canvasParent.widthProperty().addListener((a, b, c) -> {
			canvas.setWidth((double) c);
			cd.setWindowWidth((double) c);
		});

		canvasParent.heightProperty().addListener((a, b, c) -> {
			canvas.setHeight((double) c);
			cd.setWindowHeight((double) c);
		});

		comboBox.valueProperty().addListener((observable, oldValue, survivalString) -> {

			survival.setText("");
			birth.setText("");
			rules.setRules(comboBox.getValue().toString());

			for (int i : rules.getSurvivalRules())
				survival.setText(survival.getText() + i);

			for (int i : rules.getSurvivalRules())
				birth.setText(birth.getText() + i);

			for (int i = 0; i < model.getWidth(); i++)
				for (int j = 0; j < (model.getHeight() + 1)/64; j++)
					model.setCellState(i, j, BoardContainer.ACTIVEGENERATION, false);

			model.startNextGeneration();

		});

		amountOfCells.setText(String.format("%d Million Cells",
				((int) model.getWidth() * model.getHeight() / (int) Math.pow(10, 6))));
		position.setText(String.format("(x, y): (%d,%d)", cd.getCanvasDisplacedX(), cd.getCanvasDisplacedY()));

		cd.clearCanvas();
		
		Platform.runLater(new Runnable()
		  {
            @Override
            public void run() 
            {
                canvas.requestFocus();
            }
         });
	}

	/**
	 * Opens the window for file chooser, and interprets the file for then 
	 * draw the pattern from the file to the game board.
	 * 
	 * @see RleInterpreter
	 */
	
	public void handleOpen()  { 
		RLEPattern pattern = new RLEPattern();
		FileHandler file = new FileHandler(vBoxRoot.getScene().getWindow());
		String rleString = file.toString(); //returns a string representation of the rle or null.
		if(rleString != null){
			RleInterpreter rleInterp;
			try {
				rleInterp = new RleInterpreter(rleString, model.getWidth(), model.getHeight(), this.model instanceof DynamicMatrix);
				pattern.setPattern(rleInterp.getStartGeneration());
				pattern.setPatternWidth(rleInterp.getWidth());
				pattern.setPatternHeight(rleInterp.getHeight());
				model.setSettingPattern(true);
				model.setPattern(pattern);
				cd.setRLEPattern(pattern);
				cd.drawNextGeneration();
			} catch (PatternFormatException e) {
			
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("PatternFormatException");
				alert.setContentText(e.getMessage());
				alert.showAndWait();
			}
			
		}else{
			System.out.println("File was not found");
		}
	
	}
	

	public void onDrawClicked() {
	}

	public void handleZoom(ScrollEvent event) {

		if (event.getDeltaY() > 1) {
			cd.zoom(1, event);
		} else if (event.getDeltaY() < 1) {
			sliderZoom.setValue(cd.getCellSize() - 1);
			cd.zoom(-1, event);
		}
	}

	public void speedSliderDragged() {
		GOL.setDelay(Math.pow(10, 9) * (1 / sliderSpeed.getValue()));
	}

	public void zoomSliderDragged() {

		if (cd.getCellSize() - (int) sliderZoom.getValue() == -1)
			cd.zoom((int) 1);
		else if (cd.getCellSize() - (int) sliderZoom.getValue() == 1)
			cd.zoom(-1);

	}

	public void changeColor() {
		model.setColor(colorPicker.getValue());
		cd.drawNextGeneration();
	}

	public void handleStartClick() {
		if (start) {

			GOL.startGame();
			startButton.setText("Stop");

		} else {
			GOL.stop();
			startButton.setText("Play");
		}
		start = !start;
	}

	public void handleMouseEntered(MouseEvent event) {
		offsetX = (int) event.getX();
		offsetY = (int) event.getY();
	}

	public void mouseDragged(MouseEvent event) {
		if (event.isControlDown()) {
			cd.movePosition(offsetX - (int) event.getX(), offsetY - (int) event.getY());
			offsetX = (int) event.getX();
			offsetY = (int) event.getY();
			
			position.setText(
					String.format("(x, y): (%d,%d)", (int) cd.getCanvasDisplacedX(), (int) cd.getCanvasDisplacedY()));

		} else {
			mouseClicked(event, drawDrag.isSelected());
		}

	}

	public void mouseClicked(MouseEvent event) {
		int cellX = ((int)event.getX() + cd.getCanvasDisplacedX()) / cd.getCellSize();
		int cellY = ((int)event.getY() + cd.getCanvasDisplacedY())/cd.getCellSize();
		
		cd.drawCell((int) event.getX(), (int) event.getY());
	}

	public void mouseClicked(MouseEvent event, boolean dragDraw) {
		int cellX = ((int)event.getX() + cd.getCanvasDisplacedX()) /cd.getCellSize();
		int cellY = ((int)event.getY() + cd.getCanvasDisplacedY())/cd.getCellSize();
		
		cd.drawCell((int) event.getX(), (int) event.getY(), dragDraw);

	}

	public void keyListener(KeyEvent event) throws IOException
	{
		RLEPattern pattern = model.getPattern();
		if(event.getCode() == KeyCode.RIGHT)
			pattern.setPatternStartPositionX(pattern.getPatternStartPositionX() + 10);
		else if(event.getCode() == KeyCode.LEFT)
			pattern.setPatternStartPositionX(pattern.getPatternStartPositionX() - 10);
		else if(event.getCode() == KeyCode.DOWN)
			pattern.setPatternStartPositionY(pattern.getPatternStartPositionY() + 10);
		else if(event.getCode() == KeyCode.UP)
			pattern.setPatternStartPositionY(pattern.getPatternStartPositionY() - 10);
		else if(event.getCode() == KeyCode.ENTER)
		{
			if(model.getSettingPattern())
			{
				model.transferPattern(pattern.getPatternStartPositionX(), pattern.getPatternStartPositionY());
				cd.drawNextGeneration();
				pattern.setPatternStartPositionX(0);
				pattern.setPatternStartPositionY(0);
				model.setSettingPattern(false);
			}
			
		
		}
		
		Platform.runLater(new Runnable()
		  {
    @Override
    public void run() 
    {
        canvas.requestFocus();
    }
 });
		
		
	
		cd.drawNextGeneration();
		
	}
	

	public void handlePauseClick() {
		GOL.stop();
	}

	
	public void handleResetClick() {
		
		for (int x = 0; x < model.getWidth(); x++)
			for (int y = 0; y < model.getHeight(); y++) {
				model.setCellState(x-model.getShiftedRightwards(), y-model.getShiftedDownwards(), BoardContainer.CURRENTGENERATION, false);
				model.setCellState(x-model.getShiftedRightwards(), y-model.getShiftedDownwards(), BoardContainer.ACTIVEGENERATION, false);
				model.setCellState(x-model.getShiftedRightwards(), y-model.getShiftedDownwards(), BoardContainer.NEXTGENERATION, false);
				model.setCellState(x-model.getShiftedRightwards(), y-model.getShiftedDownwards(), BoardContainer.NEXTACTIVEGENERATION, false);


			}

		cd.drawNextGeneration();
			
	}


}