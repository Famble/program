package GameOfLife.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import GameOfLife.FileHandler;
import GameOfLife.RleInterpreter;
import GameOfLife.Model.BitGameBoard;
import GameOfLife.Model.DynamicGameBoard;
import GameOfLife.Model.GameBoard;
import GameOfLife.Model.GameBoard.BoardContainer;
import GameOfLife.Model.PatternFormatException;
import GameOfLife.Model.RLEPattern;
import GameOfLife.Model.Rules;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * <h1>Controller</h1>
 * This class controls how the view and model interacts.
 *
 * Event listener is all handel in controller.
 */


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
	private Toggle drawDrag;
	private ExecutionControl GOL;
	boolean start = true;
    int offsetY = 0;
	int offsetX = 0;
	CanvasDrawer cd;
	GameBoard gameBoard;
	Pattern pattern;
	

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		gameBoard = new DynamicGameBoard();

		cd = new CanvasDrawer(gameBoard, canvas.getGraphicsContext2D());

		GOL = new ExecutionControl(gameBoard, cd);		

		canvas.setOnZoom((zoomEvent) -> {

		});

		colorPicker.setValue(Color.web("#42dd50"));
		
		Rules rules = gameBoard.getRules();

		survival.textProperty().addListener((observable, oldValue, survivalString) -> {
			rules.setUserDefinedSurvivalRules(survivalString);
		});

		birth.textProperty().addListener((observable, oldValue, birthString) -> {
			rules.setUserDefinedBirthRules(birthString);

		});
		canvasParent.widthProperty().addListener((a, b, c) -> {
			canvas.setWidth((double) c-150);
			cd.setWindowWidth((double) c-150);
		});

		canvasParent.heightProperty().addListener((a, b, c) -> {
			canvas.setHeight((double) c);
			cd.setWindowHeight((double) c);
		});

		comboBox.valueProperty().addListener((observable, oldValue, survivalString) -> {

			int insertedColumnsFromLeft = 0;
			int insertedRowsFromTop = 0;
		
			
			
			
			survival.setText("");
			birth.setText("");
			rules.setRulesFromName(comboBox.getValue().toString());

			for (int i : rules.getSurvivalRules())
				survival.setText(survival.getText() + i);

			for (int i : rules.getSurvivalRules())
				birth.setText(birth.getText() + i);

			if (this.gameBoard instanceof BitGameBoard) {
				for (int i = 0; i < gameBoard.getWidth(); i++)
					for (int j = 0; j < gameBoard.getHeight(); j++)
						gameBoard.setCellState(i, j, BoardContainer.ACTIVEGENERATION, false);
			}
			

			gameBoard.nextGenerationConcurrent();

		});

		
		Platform.runLater(() -> canvas.requestFocus());

    } // end of Initialize

    /**
     * Opens the window for file chooser, and interprets the file, and then
     * draws the pattern from the file to the game board.
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
				pattern.setPattern(rleInterp.getInitialRleGeneration());
				pattern.setWidth(rleInterp.getWidth());
				pattern.setHeight(rleInterp.getHeight());
				pattern.setNameOfPattern(rleInterp.getNameOfRle());
				pattern.setAuthorOfPattern(rleInterp.getAuthorOfRle());
				pattern.setCommentOfPattern(rleInterp.getCommentOfRle());
				gameBoard.getRules().setUserDefinedBirthRules(rleInterp.getBirthOfRle());
				gameBoard.getRules().setUserDefinedSurvivalRules(rleInterp.getSurvivalOfRle());
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
	
	} // end of handleOpen


    /**
     * Uses the scroll function of the user to zoom on the canvas board
     *
     * @param event Used to track where the mouse of the user is.
     */
	public void handleZoom(ScrollEvent event) {

		if (event.getDeltaY() > 1) {
			cd.zoomOnCursor(1, (int)event.getX(), (int)event.getY());
		} else if (event.getDeltaY() < 1) {
			sliderZoom.setValue(cd.getCellSize() - 1);
			cd.zoomOnCursor(-1, (int)event.getX(), (int)event.getY());
		}
	}

	/**
	 * Listen to when drawDrag is toggled on and changs the lable of the toggel.
	 */
	public void handleDrawClick(){
		if(drawDrag.isSelected()){
			((Labeled) drawDrag).setText("Draw");
		} else {
			((Labeled) drawDrag).setText("Erase");
		}
	}

    /**
     *  Handel the slider which changes the speed of the game.
     */
    public void speedSliderDragged() {
		GOL.setDelay(Math.pow(10, 9) * (1 / sliderSpeed.getValue()));
	}

    /**
     * Handel the slider which zooms the gameboard
     */
    public void zoomSliderDragged() {

		if (cd.getCellSize() - (int) sliderZoom.getValue() == -1)
			cd.zoom(1);
		else if (cd.getCellSize() - (int) sliderZoom.getValue() == 1)
			cd.zoom(-1);

	}

    /**
     * Changes the color of the alive game cells
     */
	public void changeColor() {
		gameBoard.setColor(colorPicker.getValue());
		cd.drawNextGeneration();
	}

    /**
     * Starts and stops the game
     */
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

    /**
     * sets the offset of X and Y when the mouse enters the canvas
     * @param event takes the X and Y value of the mouse position
     */
    public void handleMouseEntered(MouseEvent event) {
		offsetX = (int) event.getX();
		offsetY = (int) event.getY();
	}

    /**
     * Listens to when the users drags the mouse over the canvas,
     * and drags the canvas if the user is also holding Control Down while dragging.
     *
     * @param event checks where the mouse is on the canvas
     */
    public void mouseDragged(MouseEvent event) {
		if (event.isControlDown()) {
			cd.movePosition(offsetX - (int) event.getX(), offsetY - (int) event.getY());
			offsetX = (int) event.getX();
			offsetY = (int) event.getY();
		} else {
			mouseClicked(event);
		}

	}

	/**
	 * Listens to when the user clicks on the board, and draws or removes the cell from the canvas.
	 *
	 * @param event checks where the mouse position is.
	 *
	 */
	public void mouseClicked(MouseEvent event) {
		cd.drawCell((int) event.getX(), (int) event.getY(),drawDrag.isSelected());
		//drawDrag.isSelected() decides on whenever the cells will be drawn or removed

	}

    /**
     * Makes an object of RLEPattern and controls how to move and set the object.
     *
     * Moves the object that user have opened with <i>ARROWKEY</i>,
     * and sets the object into the board when user presses <i>ENTER</i>.
     *
     * @param event Checks if the arrows key or enter is pressed
     *
     */
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
		
		Platform.runLater(() -> canvas.requestFocus());
		
		cd.drawNextGeneration();
		
	} // end of keyListener

    /**
     * Sets the <b>width</b> and <b>height</b> of the canvas,
     * and draw the an array of next generation.
     */

    public void setDimensions(){
		canvas.setWidth(vBoxRoot.getWidth()-150);
		canvas.setHeight(canvasParent.getHeight());
		cd.drawNextGeneration();
	}

    /**
     *Opens up a new window for a pattern editor,
     * where you can follow the next generation on a timeline.
     *
     * @throws IOException
     * @see EditorController
     */
    public void handleEditorClick() throws IOException{
		
		Stage editor = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("PatternEditor.fxml"));
		VBox root = loader.load();
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

    /**
     * Resets the Game Board by calling on <b>resetGameBoard</b>,
     * and then draws the board by calling on <b>drawNextGeneration</b>.
     *
     * @see GameBoard#resetGameBoard
     * @see CanvasDrawer#drawNextGeneration
     */
    public void handleResetClick() {
		
		gameBoard.resetGameBoard();
		cd.drawNextGeneration();
			
	}


}