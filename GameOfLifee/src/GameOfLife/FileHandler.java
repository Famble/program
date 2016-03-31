package GameOfLife;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

import java.nio.*;
import java.nio.charset.Charset;

public class FileHandler extends Stage
{
    Path path = Paths.get(".");

    File f = path.toFile();

    public FileHandler()
    {

	this.setTitle("File");
	FileChooser fileChooser = new FileChooser();
	fileChooser.setTitle("Choose file");
	fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));

	File selectedFile = fileChooser.showOpenDialog(this);
		if (selectedFile != null)
		{
		    System.out.println(selectedFile.toPath());
		    System.out.println(readRle(selectedFile));
		}
		
    }

	
    private String readRle(File file){
    	
    	StringBuilder rleString = new StringBuilder();
    	Charset charset = Charset.forName("UTF-8");
		try( BufferedReader reader = Files.newBufferedReader(file.toPath(),charset);)
			{
	    	
			String text = null;
			
				while((text = reader.readLine()) != null)
				{
					rleString.append(text);
				}
			
		    }
		catch(FileNotFoundException ex){
			System.err.format("FileNotFoundException: %s%n", ex);
		}
		catch(IOException x){
	    	System.err.format("IOException: %s%n", x);
	    }
		return rleString.toString();
    }
}