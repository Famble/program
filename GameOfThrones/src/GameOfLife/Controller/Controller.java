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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * <h1>Controller</h1>
 * This class controls how the view and model interacts.
 *
 * Event listener is all handel in controller.
 */


public class Controller implements Initializable {
	
	@FXML
	private Label descriptionText;
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
	@FXML
	private BorderPane borderPaneRoot;
	private ExecutionControl executionControl;
	boolean start = true;
    int offsetY = 0;
	int offsetX = 0;
	CanvasDrawer cd;
	GameBoard gameBoard;
	Pattern pattern;
	

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	canvas.requestFocus();
	        	System.out.println("REQUESTER FOCUS");
	        }
	    });

		
		//creates an instance of the gameboard
		gameBoard = new DynamicGameBoard();

		//creates an instance of canvasdrawer, used to draw the game board.
		cd = new CanvasDrawer(gameBoard, canvas.getGraphicsContext2D());

		//create an instance of ExecutionControl, used to control the execution of the game(start,stop,paus,speed)
		executionControl = new ExecutionControl(gameBoard, cd);		


		colorPicker.setValue(Color.web("#42dd50"));
		
		//gets the rules used by the game board instance
		Rules rules = gameBoard.getRules();
		
		//create aleart object to warn about wrong user input when user sets rules through the textFields.
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Invalid input");
	
		//called whenever the user inputs data into the survival text field.
		//Update survival condition for if the input is valid.
		survival.textProperty().addListener((observable, oldValue, survivalString) -> {
			if (survivalString.matches("[1-9]*"))
				rules.setUserDefinedSurvivalRules(survivalString);
			else
			{
				alert.setContentText("Please only enter integers");
				alert.showAndWait();
			}
		});

		//works as described just above, but with the birth conditions of a cell.
		birth.textProperty().addListener((observable, oldValue, birthString) -> {
			if(birthString.matches("[1-9]*"))
				rules.setUserDefinedBirthRules(birthString);
			else{
				alert.setContentText("Please only enter integers");
				alert.showAndWait();
			}

		});
		
		//the canvas is wrapped inside the canvasParent HBox and will fill the parent
		//whenever the canvasParent changes width this linstener ensure that
		//the width of the canvas also changes its width.
		borderPaneRoot.widthProperty().addListener((a, b, newWidthOfBorder) -> {
			canvas.setWidth((double) newWidthOfBorder-325);
			cd.setWindowWidth((double) newWidthOfBorder-325);
		});

		//same as above, but with the height of the canvas.
		borderPaneRoot.heightProperty().addListener((a, b, newHeightOfCanvas) -> {
			canvas.setHeight((double) newHeightOfCanvas-130);
			cd.setWindowHeight((double) newHeightOfCanvas-130);
		});
		

		comboBox.valueProperty().addListener((observable, oldValue, survivalString) -> {
						
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
		

    } // end of Initialize

    /**
     * Opens the window for file chooser, and interprets the file, and then
     * draws the pattern from the file to the game board.
     *
     * @see RleInterpreter
     */
	public void handleOpen()  { 
		RLEPattern pattern = new RLEPattern();
		FileHandler file = new FileHandler(borderPaneRoot.getScene().getWindow());
		//returns a string representation of the rle or null.
		String rleString = file.toString(); 
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
				System.out.println(rleInterp.getBirthOfRle());
				System.out.println(rleInterp.getSurvivalOfRle());
				gameBoard.getRules().setUserDefinedBirthRules(rleInterp.getBirthOfRle());
				gameBoard.getRules().setUserDefinedSurvivalRules(rleInterp.getSurvivalOfRle());
				gameBoard.setSettingPattern(true);
				gameBoard.setPattern(pattern);
				cd.setRLEPattern(pattern);
				cd.drawNextGeneration();
				descriptionText.setTextFill(Color.BLACK);
				descriptionText.setText(pattern.getCommentOfPattern());
			
			} catch (PatternFormatException e) {
			
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("PatternFormatException");
				alert.setContentText(e.getMessage());
				alert.showAndWait();
			}
			
		}else{
			System.out.println("File was not found");
		}
		
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	canvas.requestFocus();
	        }
	    });
    	
	
	
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
		executionControl.setDelay(Math.pow(10, 9) * (1 / sliderSpeed.getValue()));
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

			executionControl.startGame();
			startButton.setText("Stop");

		} else {
			executionControl.stop();
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
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	canvas.requestFocus();
	        }
	    });
		

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
		
		if(gameBoard.getSettingPattern())
		{
			if(event.getCode() == KeyCode.RIGHT)
			{
				pattern.setPatternTranslationX(pattern.getPatternTranslationX() + 5);
				System.out.println("RIGHT");
			}
			else if(event.getCode() == KeyCode.LEFT)
			{
				pattern.setPatternTranslationX(pattern.getPatternTranslationX() - 5);
				System.out.println("LEFT");
			}
			else if(event.getCode() == KeyCode.DOWN)
			{
				pattern.setPatternTranslationY(pattern.getPatternTranslationY() + 5);
				System.out.println("DOWN");
			}
			else if(event.getCode() == KeyCode.UP)
			{
				pattern.setPatternTranslationY(pattern.getPatternTranslationY() - 5);
				System.out.println("UP");
			}
			else if(event.getCode() == KeyCode.ENTER)
			{
				System.out.println("DET SKJEDDE");
				
					gameBoard.transferPattern(pattern.getPatternTranslationX(), pattern.getPatternTranslationY());
					cd.drawNextGeneration();
					//reset the translation.
					pattern.setPatternTranslationX(0);
					pattern.setPatternTranslationY(0);
					
					//pattern has been set to the main game board, so we set the settingPattern field to false
					gameBoard.setSettingPattern(false);
			
				
			
			}
			}
			Platform.runLater(new Runnable() {
		        @Override
		        public void run() {
		        	canvas.requestFocus();
		        }
		    });
	    	
		
	
		
		cd.drawNextGeneration();
		
	} // end of keyListener

    /**
     * Sets the <b>width</b> and <b>height</b> of the canvas,
     * and draw the an array of next generation.
     */

    public void setDimensions(){
    	/*
		canvas.setWidth(vBoxRoot.getWidth()-150);
		canvas.setHeight(canvasParent.getHeight());
		cd.drawNextGeneration();
		*/
	}

    /**
     *Opens up a new window for a pattern editor,
     * where you can follow the next generation on a timeline.
     *
     * @throws IOException
     * @see EditorController
     */
    public void handleEditorClick() throws IOException{
		
    	if(!(this.gameBoard instanceof DynamicGameBoard))
    	{
    		Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Pattern editor not supported");
			alert.setContentText("Pattern editor is only supported by using the dynamic game board");
			alert.showAndWait();
    	}
    	else
    	{
    		//stops the execution of the main game.
    		executionControl.stop();	
    		//creates a new stage for the pattern editor
    		Stage editor = new Stage();
    		//loads the fxml file containen the GUI.
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("PatternEditor.fxml"));
    		VBox root = loader.load();
    		//gets the controller of PatternEditor.fxml.
    		EditorController edController = loader.getController();
    		//initializes the controller with the copy of the game board
    		DynamicGameBoard gameBoardClone = (DynamicGameBoard)this.gameBoard.clone();
    		//sendes the clone of the game board to be initialized in the editor controller
    		edController.initialize(gameBoardClone);
    		Scene scene = new Scene(root, 1000, 700);
    		editor.setScene(scene);
    		editor.setTitle("Pattern Editor");
    		editor.show();
    		edController.setDimensions();
    		//draws the strip showing 20 subsequent generation of the pattern.
    		edController.drawStrip(gameBoardClone);
    	}
	



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