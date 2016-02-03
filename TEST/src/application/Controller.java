package application;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javafx.scene.text.*;
import javafx.scene.shape.*;
import java.lang.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class Controller implements Initializable{
	
	public Button button;
	
	public void handleStartClick(){
		System.out.println("Starting game");
	}
	
	public void handlePauseClick(){
		System.out.println("Pausing game");
	}
	
	public void handleResetClick(){
		System.out.println("Reseting game");
	}
	
	public void handleColChngClick(){
		System.out.println("Cell-color changed");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Starter opp..");
	}

}