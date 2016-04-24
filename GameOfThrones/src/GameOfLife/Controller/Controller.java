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
import GameOfLife.Model.BitGameBoard;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Controller implements Initializable {

	public FXCollections ting;
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
	private ExecutionControl GOL;
	boolean start = true;
	int offsetX = 0;
	CanvasDrawer cd;

	int offsetY = 0;
	GameBoard gameBoard;
	Pattern pattern;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		rules = new Rules();
		gameBoard = new DynamicGameBoard(100, (100), rules);

		cd = new CanvasDrawer(gameBoard, canvas.getGraphicsContext2D());

		GOL = new ExecutionControl(gameBoard, cd);		

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

			int shiftedRightwards = 0;
			int shiftedDownwards = 0;
			if (this.gameBoard instanceof DynamicGameBoard) {
				shiftedRightwards = ((DynamicGameBoard) this.gameBoard).getShiftedRightwards();
				shiftedDownwards = ((DynamicGameBoard) this.gameBoard).getShiftedDownwards();
			}
			
			survival.setText("");
			birth.setText("");
			rules.setRules(comboBox.getValue().toString());

			for (int i : rules.getSurvivalRules())
				survival.setText(survival.getText() + i);

			for (int i : rules.getSurvivalRules())
				birth.setText(birth.getText() + i);

			for (int i = 0; i < gameBoard.getWidth(); i++)
				for (int j = 0; j < (gameBoard.getHeight() + 1)/64; j++)
					gameBoard.setCellState(i-shiftedRightwards, j-shiftedDownwards, BoardContainer.ACTIVEGENERATION, false);

			gameBoard.startNextGeneration();

		});

		amountOfCells.setText(String.format("%d Million Cells",
				((int) gameBoard.getWidth() * gameBoard.getHeight() / (int) Math.pow(10, 6))));
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
				rleInterp = new RleInterpreter(rleString, gameBoard.getWidth(), gameBoard.getHeight(), this.gameBoard instanceof DynamicGameBoard);
				pattern.setPattern(rleInterp.getStartGeneration());
				pattern.setWidth(rleInterp.getWidth());
				pattern.setHeight(rleInterp.getHeight());
				gameBoard.setSettingPattern(true);
				gameBoard.setPattern(pattern);
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
	


	public void handleZoom(ScrollEvent event) {

		if (event.getDeltaY() > 1) {
			cd.zoomOnCursor(1, (int)event.getX(), (int)event.getY());
		} else if (event.getDeltaY() < 1) {
			sliderZoom.setValue(cd.getCellSize() - 1);
			cd.zoomOnCursor(-1, (int)event.getX(), (int)event.getY());
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
		gameBoard.setColor(colorPicker.getValue());
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
		cd.drawCell((int) event.getX(), (int) event.getY());
	}

	public void mouseClicked(MouseEvent event, boolean dragDraw) {
		cd.drawCell((int) event.getX(), (int) event.getY(), dragDraw);

	}

	public void keyListener(KeyEvent event) throws IOException
	{
		RLEPattern pattern = gameBoard.getPattern();
		if(event.getCode() == KeyCode.RIGHT)
			pattern.setPatternTranslationX(pattern.getPatternTranslationX() + 10);
		else if(event.getCode() == KeyCode.LEFT)
			pattern.setPatternTranslationX(pattern.getPatternTranslationX() - 10);
		else if(event.getCode() == KeyCode.DOWN)
			pattern.setPatternTranslationY(pattern.getPatternTranslationY() + 10);
		else if(event.getCode() == KeyCode.UP)
			pattern.setPatternTranslationY(pattern.getPatternTranslationY() - 10);
		else if(event.getCode() == KeyCode.ENTER)
		{
			if(gameBoard.getSettingPattern())
			{
				gameBoard.transferPattern(pattern.getPatternTranslationX(), pattern.getPatternTranslationY());
				cd.drawNextGeneration();
				pattern.setPatternTranslationX(0);
				pattern.setPatternTranslationY(0);
				gameBoard.setSettingPattern(false);
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
	
	public void setDimensions(){
		canvas.setWidth(vBoxRoot.getWidth());
		canvas.setHeight(canvasParent.getHeight());
		cd.drawNextGeneration();
	}
	
	public void handleEditorClick() throws IOException{
		
		Stage editor = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("PatternEditor.fxml"));
		VBox root = (VBox)loader.load();
		EditorController edController = loader.getController();
		GOL.stop();	
		edController.initialize(this.gameBoard);
		DynamicGameBoard gameBoardClone = (DynamicGameBoard)this.gameBoard.clone();
		Scene scene = new Scene(root, 1000, 700);
		editor.setScene(scene);
		editor.setTitle("Pattern Editor");
		editor.show();
		edController.setDimensions();
		edController.drawStrip(gameBoardClone);



	}

	
	public void handleResetClick() {
		
		gameBoard.resetGameBoard();
		cd.drawNextGeneration();
			
	}


}