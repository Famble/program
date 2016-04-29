package GameOfLife;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.*;

import GameOfLife.Controller.Controller;
import GameOfLife.Controller.EditorController;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Game of Life");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("View/GUI.fxml"));
		VBox root = (VBox)loader.load();
		Controller controller = loader.getController();
		Scene scene = new Scene(root, 1000, 700);
		scene.getStylesheets().add(getClass().getResource("Stylesheet.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
		controller.setDimensions();
		
	}

}