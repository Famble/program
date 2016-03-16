package GameOfLife;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{

    public static void main(String[] args)
    {
	launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
	primaryStage.setTitle("Game of Life");
	Parent root = FXMLLoader.load(getClass().getResource("Hey.fxml"));
	primaryStage.setScene(new Scene(root, 1000, 700));
	primaryStage.show();
    }

}