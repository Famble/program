package GameOfLife;

import java.awt.Component;
import java.io.File;
import java.nio.file.*;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

import java.nio.*;

public class FileHandler extends Stage
{
    Path path = Paths.get(".");

    File f = path.toFile();

    public FileHandler()
    {

	this.setTitle("File");
	FileChooser fileChooser = new FileChooser();
	fileChooser.setTitle("Open Resource File");
	fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));

	File selectedFile = fileChooser.showOpenDialog(this);
	if (selectedFile != null)
	{
	    System.out.println(selectedFile.toPath());
	}
    }
    
    
}