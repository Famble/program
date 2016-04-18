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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class Controller implements Initializable {

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
	public void initialize(URL location, ResourceBundle resources) {
		rules = new Rules();
		model = new Matrix(1000, (1000), rules);

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
				for (int j = 0; j < model.getY(); j++)
					model.getActiveCells()[i][j] = 0;

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

		FileHandler file = new FileHandler();
		String rleString = file.toString();
		if(rleString != null){
			RleInterpreter rleInterp;
			try {
				rleInterp = new RleInterpreter(rleString);
				model.setPattern(rleInterp.getStartGeneration());
				model.setPatternWidth(rleInterp.getWidth());
				model.setPatternHeight(rleInterp.getHeight());
				model.settingPattern = true;
				//model.setPatternFromRle(file.readRle());
				cd.drawNextGeneration();
			} catch (PatternFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		
		if(cellX < 0 || cellX > model.getWidth() || cellY < 0 || cellY > model.getHeight())
		{
	
		}
		else
			cd.drawCell((int) event.getX(), (int) event.getY());
	}

	public void mouseClicked(MouseEvent event, boolean dragDraw) {
		int cellX = ((int)event.getX() + cd.getCanvasDisplacedX()) /cd.getCellSize();
		int cellY = ((int)event.getY() + cd.getCanvasDisplacedY())/cd.getCellSize();
		
		if(cellX < 0 || cellX > model.getWidth() || cellY < 0 || cellY > model.getHeight())
		{
	
		}
		else
			cd.drawCell((int) event.getX(), (int) event.getY(), dragDraw);

	}

	public void keyListener(KeyEvent event) throws IOException
	{
		
		if(event.getCode() == KeyCode.RIGHT)
			cd.setPatternPositionX(cd.getPatternPositionX() + 10);
		else if(event.getCode() == KeyCode.DOWN)
			cd.setPatternPositionY(cd.getPatternPositionY() + 10);
		else if(event.getCode() == KeyCode.LEFT)
			cd.setPatternPositionX(cd.getPatternPositionX() - 10);
		else if(event.getCode() == KeyCode.UP)
			cd.setPatternPositionY(cd.getPatternPositionY() - 10);
		else if(event.getCode() == KeyCode.ENTER)
		{
			if(model.settingPattern)
			{
				model.transferPattern(cd.getPatternPositionX(), cd.getPatternPositionY());
				cd.drawNextGeneration();
				cd.setPatternPositionX(0);
				cd.setPatternPositionY(0);
				model.settingPattern = false;
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
		for (int i = 0; i < model.getWidth(); i++)
			for (int j = 0; j < model.getY(); j++) {
				model.getCurrentGeneration()[i][j] = 0;
				model.getNextGeneration()[i][j] = 0;
				model.getActiveCells()[i][j] = 0;
			}

		cd.drawNextGeneration();
	}

}