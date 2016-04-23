package GameOfLife.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import GameOfLife.FileHandler;
import GameOfLife.RleInterpreter;
import GameOfLife.Model.DynamicGameBoard;
import GameOfLife.Model.GameBoard;
import GameOfLife.Model.GameBoard.BoardContainer;
import GameOfLife.Model.PatternFormatException;
import GameOfLife.Model.RLEPattern;
import GameOfLife.Model.Rules;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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
import javafx.stage.Stage;

public class EditorController implements Initializable {

	@FXML
	private VBox vBoxRoot;
	@FXML
	private Canvas canvas;
	@FXML
	private Slider sliderSpeed;
	@FXML 
	Canvas stripCanvas;
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
	private ExecutionControl GOL;
	boolean start = true;
	int offsetX = 0;
	StripDrawer sd;
	int offsetY = 0;
	private GameBoard board;
	private GameBoard twentyGenBoard;
	RLEPattern pattern;
	RLEPattern twentyGenPattern;
	
	public void handleSavePattern(){
		
		RLEPattern pattern = new RLEPattern(board.getWidth(), board.getHeight());
		
		for(int x = 0; x < board.getWidth(); x++){
			for( int y = 0; y < board.getHeight(); y++){
				pattern.getPattern()[x][y] = board.getCellState(x, y, BoardContainer.CURRENTGENERATION);
			}
		}

		this.board.setPattern(pattern);
		
		pattern.trim();
		
	}

	public void setGameBoard(GameBoard board){
		
		
	}
	
	public void drawStrip(){
		System.out.println("HEY");
		DynamicGameBoard board = (DynamicGameBoard)this.board.clone();
		sd.drawStrip(board);
	}
	
	public void drawStrip(GameBoard board){
		sd.drawStrip(board);

	}
	
	
	
	public void initialize(GameBoard board){
		

		this.board = board;
	
		this.sd = new StripDrawer(board, canvas.getGraphicsContext2D(), stripCanvas.getGraphicsContext2D());
		this.GOL = new ExecutionControl(board, sd);
		
		canvasParent.widthProperty().addListener((a, b, c) -> {
			canvas.setWidth((double) c-250);
			sd.setWindowWidth((double) c);
		});

		canvasParent.heightProperty().addListener((a, b, c) -> {
			canvas.setHeight((double) c);
			sd.setWindowHeight((double) c);
		});

		comboBox.valueProperty().addListener((observable, oldValue, survivalString) -> {

			survival.setText("");
			birth.setText("");
			rules.setRules(comboBox.getValue().toString());

			for (int i : rules.getSurvivalRules())
				survival.setText(survival.getText() + i);

			for (int i : rules.getSurvivalRules())
				birth.setText(birth.getText() + i);

			for (int i = 0; i < board.getWidth(); i++)
				for (int j = 0; j < (board.getHeight() + 1)/64; j++)
					board.setCellState(i, j, BoardContainer.ACTIVEGENERATION, false);

			board.startNextGeneration();

		});

		

		
		Platform.runLater(new Runnable()
		  {
            @Override
            public void run() 
            {
                canvas.requestFocus();
            }
         });
		
	}
	
	public void setDimensions(){
		canvas.setWidth(vBoxRoot.getWidth()-250);
		canvas.setHeight(canvasParent.getHeight());
		sd.drawNextGeneration();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		survival.textProperty().addListener((observable, oldValue, survivalString) -> {
			this.rules.setUserDefinedSurvivalRules(survivalString);
		});

		birth.textProperty().addListener((observable, oldValue, birthString) -> {
			this.rules.setUserDefinedBirthRules(birthString);

		});
		
	}

	public void handleZoom(ScrollEvent event) {

		if (event.getDeltaY() > 1) {
			sd.zoomOnCursor(1, (int)event.getX(), (int)event.getY());
			sliderZoom.setValue(sd.getCellSize() + 1);
		} else if (event.getDeltaY() < 1) {
			sliderZoom.setValue(sd.getCellSize() - 1);
			sd.zoomOnCursor(-1, (int)event.getX(), (int)event.getY());
		}
	}

	public void speedSliderDragged() {
		GOL.setDelay(Math.pow(10, 9) * (1 / sliderSpeed.getValue()));
	}

	public void zoomSliderDragged() {

		if (sd.getCellSize() - (int) sliderZoom.getValue() == -1)
			sd.zoom((int) 1);
		else if (sd.getCellSize() - (int) sliderZoom.getValue() == 1)
			sd.zoom(-1);

	}



	public void handleMouseEntered(MouseEvent event) {
		offsetX = (int) event.getX();
		offsetY = (int) event.getY();
	}

	public void mouseDragged(MouseEvent event) {
		if (event.isControlDown()) {
			sd.movePosition(offsetX - (int) event.getX(), offsetY - (int) event.getY());
			offsetX = (int) event.getX();
			offsetY = (int) event.getY();
			
		} else {
			mouseClicked(event, drawDrag.isSelected());
		}

	}

	public void mouseClicked(MouseEvent event) {
		sd.drawCell((int) event.getX(), (int) event.getY());
	}

	public void mouseClicked(MouseEvent event, boolean dragDraw) {
		sd.drawCell((int) event.getX(), (int) event.getY(), dragDraw);

	}





}