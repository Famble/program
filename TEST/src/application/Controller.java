package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;


public class Controller implements Initializable{
	
	public Button button;
	
	public void handleButtonClick(){
		System.out.println("Hello there!");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Starter opp..");
	}

}