package GameOfLife;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;

import GameOfLife.Model.BitGameBoard;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

import java.nio.charset.Charset;

public class FileHandler{
	
	BitGameBoard model;
	String rleString;

	public FileHandler() {

	}

	/*
	public String loadStringOfFile(Window window){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose file");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("RLE Files", "*.rle"));
		File rleFile = fileChooser.showOpenDialog(window);//waits until file is selected or user cancels the dialogbox
		if(rleFile != null){
			rleString = this.readGameBoardFromDisk(rleFile);
		}
		return rleString;
	}

	public String loadStringOfUrl(String url) throws IOException {
		if (url != null) {
			rleString = readGameBoardFromUrl(url);
		}
		return rleString;

	}
	*/

	public String readDisk(Window window) throws FileNotFoundException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose file");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("RLE Files", "*.rle"));
		File rleFile = fileChooser.showOpenDialog(window);//waits until file is selected or user cancels the dialogbox
		if(rleFile != null){
			rleString = this.readGameBoard(new FileReader(rleFile));
		}

		return  rleString;

	}

	public String readUrl(String url) throws IOException {
		URL destination = new URL(url);
		URLConnection conn = destination.openConnection();
		return readGameBoard(new InputStreamReader(conn.getInputStream()));
	}


	private String readGameBoard(AutoCloseable in){
		StringBuilder rleBuilder = new StringBuilder();
		try(BufferedReader reader	=	new	BufferedReader(
				(Reader) in)){
			String	line	=	null;
			while	((line	=	reader.readLine())	!=	null)	{
				rleBuilder.append(line).append("\n");
			}
		}catch(IOException ie)	{
			System.err.println("Problem	in	Web/File	IO");
		}

		return rleBuilder.toString();
	}
























	private String readGameBoardFromUrl(String webaddr) throws IOException {
		StringBuilder rleBuilder = new StringBuilder();

			URL url	=	new	URL(webaddr);
			URLConnection conn	=	url.openConnection();
			try(BufferedReader reader	=	new	BufferedReader(
					new InputStreamReader(conn.getInputStream()))){
				String	line	=	null;
				while	((line	=	reader.readLine())	!=	null)	{
					rleBuilder.append(line).append("\n");
				}
			}catch(IOException ie)	{
				System.err.println("Problem	in	Web/File	IO");
			}


		return rleBuilder.toString();

	}


	private String readGameBoardFromDisk(File file) {

		StringBuilder rleBuilder = new StringBuilder();
		Charset charset = Charset.forName("UTF-8");

		
		try(BufferedReader reader =Files.newBufferedReader(file.toPath(), charset)) {

			String text = null;

			while ((text = reader.readLine()) != null) {
				rleBuilder.append(text).append("\n");
			}

		} catch (FileNotFoundException e) {
			System.err.format("FileNotFoundException: %s%n", e);
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		} 
		return rleBuilder.toString();
	}

}