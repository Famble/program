package GameOfLife.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import GameOfLife.Model.BitGameBoard;
import GameOfLife.Model.DynamicGameBoard;
import GameOfLife.Model.GameBoard;
import GameOfLife.Model.GameBoard.BoardContainer;
import GameOfLife.Model.GameBoardFactorySingleTon;
import GameOfLife.Model.GameBoardFactorySingleTon.GameBoardType;
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
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;



/**
 * <h1>Controller</h1>
 * This class controls how the view and model interacts.
 *
 * Event listener is all handel in controller.
 * 
 * @author Markus Hellestveit, Dusan Jacovic have implime
 */


public class Controller implements Initializable {
	@FXML
	private TextArea descriptionText;
	@FXML
	private ToggleButton ShowBorder;
	@FXML
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
	private Toggle BorderSelected;
	@FXML
	private BorderPane borderPaneRoot;
	private ExecutionControl executionControl;
	boolean start = true;
    int offsetY = 0;
	int offsetX = 0;
	CanvasDrawer canvasDrawer;
	GameBoard gameBoard;
	Pattern pattern;
	

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		GameBoardFactorySingleTon GameBoardFactorySingleton = new GameBoardFactorySingleTon();
		
		//creates an instance of the game board
		gameBoard = GameBoardFactorySingleTon.getInstance(GameBoardType.DynamicGameBoard);

		//creates an instance of canvasdrawer, used to draw the game board.
		canvasDrawer = new CanvasDrawer(canvas.getGraphicsContext2D());

		//create an instance of ExecutionControl, used to control the execution of the game(start,stop,paus,speed)
		executionControl = new ExecutionControl(canvasDrawer);		


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
			canvasDrawer.setWindowWidth((double) newWidthOfBorder-325);
		});

		//same as above, but with the height of the canvas.
		borderPaneRoot.heightProperty().addListener((a, b, newHeightOfCanvas) -> {
			canvas.setHeight((double) newHeightOfCanvas-130);
			canvasDrawer.setWindowHeight((double) newHeightOfCanvas-130);
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
			

			gameBoard.nextGeneration();
			
			Platform.runLater(() -> {
                canvas.requestFocus();
                System.out.println("REQUESTER FOCUS");
            });

		});
		

    } // end of Initialize

    /**
     * Opens the window so the user can give a URL, and interprets the Url, and then
     * draws the pattern from the file to the game board, if the connection was vaild.
     *
     * @see RleInterpreter
     */
	public void handleOpenUrlRle(){
		TextInputDialog inputDialog = new TextInputDialog("https://");
		inputDialog.setTitle("Text Input Dialog");
		inputDialog.setHeaderText("Get Rle pattern from Url");
		inputDialog.setContentText("Please enter the Url:");

		FileHandler file = new FileHandler();
		String rleString = null;


		Optional<String> result = inputDialog.showAndWait();
		if(result.isPresent()){
			try {
				rleString = result.get();

				if (!((rleString.equals("https://")) || (rleString.equals("http://")))) {
					System.out.println("fkml");
					rleString = file.readUrl(rleString);
					rleReading(rleString);
				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("ERROR");
					alert.setContentText("Please enter a valid URL");
					alert.showAndWait();
				}

			} catch (IOException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("ERROR");
				alert.setContentText("Something when wrong");
				alert.showAndWait();
			}


		}else{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("File not found");
			alert.setContentText("File was not found");
			alert.showAndWait();
		}

		Platform.runLater(() -> canvas.requestFocus());
	}







	/**
	 * Opens the window for file chooser, and interprets the file, and then
	 * draws the pattern from the file to the game board.
	 *
	 * @see RleInterpreter
	 */
	public void handleOpenFileRle()  {

		FileHandler file = new FileHandler();
		//returns a string representation of the rle or null.

		String rleString = null;
		try {
			rleString = file.readDisk(borderPaneRoot.getScene().getWindow());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(rleString != null){

			rleReading(rleString);

		}else{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("File not found");
			alert.setContentText("File was not found");
			alert.showAndWait();
		}

		Platform.runLater(() -> canvas.requestFocus());



	}

	/**
	 * This method handles the interaction between FileHandler and RleInterpreter.
	 * Sets the pattern into the board from what the FileHandler has read.
	 *
	 * @see FileHandler
	 * @see RleInterpreter
	 * @param rleString A String from a file/url that.
     */
	private void rleReading(String rleString){
		RLEPattern pattern = new RLEPattern();
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
			canvasDrawer.setRLEPattern(pattern);
			canvasDrawer.drawBoard();
			
			StringBuilder description = new StringBuilder();
			
			if(pattern.hasAuthor())
				description.append("Author: ").append(pattern.getAuthorOfPattern()).append("\n\n");
			else
				description.append("No author provided\n\n");
			
			if(pattern.hasComment()){
				description.append(pattern.getCommentOfPattern()).append("\n\n");
			}
			
			descriptionText.setText(description.toString());
			
				
			
		

		} catch (PatternFormatException e) {

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("PatternFormatException");
			alert.setContentText(e.getMessage());
			alert.showAndWait();
		}
	}

	
    /**
     * Uses the scroll function of the user to zoom on the canvas board
     *
     * @param event Used to track where the mouse of the user is.
     */
	public void handleZoom(ScrollEvent event) {

		if (event.getDeltaY() > 1) {
			canvasDrawer.zoomOnCursor(1, (int)event.getX(), (int)event.getY());
		} else if (event.getDeltaY() < 1) {
			sliderZoom.setValue(canvasDrawer.getCellSize() - 1);
			canvasDrawer.zoomOnCursor(-1, (int)event.getX(), (int)event.getY());
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

		if (canvasDrawer.getCellSize() - (int) sliderZoom.getValue() == -1)
			canvasDrawer.zoomInMiddleOfScreen(1);
		else if (canvasDrawer.getCellSize() - (int) sliderZoom.getValue() == 1)
			canvasDrawer.zoomInMiddleOfScreen(-1);

	}

    /**
     * Changes the color of the alive game cells
     */
	public void changeColor() {
		gameBoard.setColor(colorPicker.getValue());
		canvasDrawer.drawBoard();
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
			canvasDrawer.movePosition(offsetX - (int) event.getX(), offsetY - (int) event.getY());
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
		canvasDrawer.drawCell((int) event.getX(), (int) event.getY(),drawDrag.isSelected());
		Platform.runLater(() -> canvas.requestFocus());
		

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
					canvasDrawer.drawBoard();
					//reset the translation.
					pattern.setPatternTranslationX(0);
					pattern.setPatternTranslationY(0);
					
					//pattern has been set to the main game board, so we set the settingPattern field to false
					gameBoard.setSettingPattern(false);
			
				
			
			}
			}
			Platform.runLater(() -> canvas.requestFocus());
	    	
		
	
		
		canvasDrawer.drawBoard();
		
	} // end of keyListener

    
    
    public void handleShowBorder(){
    	canvasDrawer.setShowBorder(ShowBorder.isSelected());
    }

    
    public void handleHowToClick() throws IOException{
    		//creates a new stage for the pattern editor
    		Stage howTo = new Stage();
    		howTo.setResizable(false);
    		howTo.getIcons().add(new Image(getClass().getResourceAsStream("../View/golIcon.png")));
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/HowTo.fxml"));
    		VBox root = loader.load();
    		Scene scene = new Scene(root, 600, 400);
    		howTo.setScene(scene);
    		howTo.setTitle("HowTo");
    		howTo.show();
    	}
    
    /**
     * Resets the Game Board by calling on <b>resetGameBoard</b>,
     * and then draws the board by calling on <b>drawNextGeneration</b>.
     *
     * @see GameBoard#resetGameBoard
     */
    public void handleResetClick() {
		executionControl.stop();
		gameBoard.resetGameBoard();
		canvasDrawer.drawBoard();
		startButton.setText("Play");

			
	}


}