package GameOfLife;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;

import GameOfLife.Model.BitGameBoard;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;

import java.nio.charset.Charset;

public class FileHandler{
	
	BitGameBoard model;
	String rleString;

	public FileHandler(Window window) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose file");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("RLE Files", "*.rle"));
		File rleFile = fileChooser.showOpenDialog(window);//waits until file is selected or user cancels the dialogbox
		if(rleFile != null){
			rleString = this.convertRleToString(rleFile);
		}

	}

	private String convertRleToString(File file) {

		StringBuilder rleString = new StringBuilder();
		Charset charset = Charset.forName("UTF-8");
		BufferedReader reader = null;
		
		try {

			String text = null;
			
			reader = Files.newBufferedReader(file.toPath(), charset);

			while ((text = reader.readLine()) != null) {
				rleString.append(text + "\n");
			}

		} catch (FileNotFoundException e) {
			System.err.format("FileNotFoundException: %s%n", e);
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		} 
		return rleString.toString();
	}
	@Override
	public String toString(){
		return rleString;
	}
}