package GameOfLife.Controller;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;

import GameOfLife.Model.BitGameBoard;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

import java.nio.charset.Charset;

/**
 * This class reads from file and url.
 *
 * The method which reads is mainly {@link #readGameBoard(Reader) readGameBoard}, <br>
 * and is used in every other method which read from an stream.
 *
 * @author Johnny Lam
 */
public class FileHandler{

	String rleString;

	/**
	 * Default constructor of FileHandler
	 */
	public FileHandler() {

	}


	/**
	 * Gets the file from the FileChooser,<br>
	 * if the user cancel the file chooser it will return null and the readDisk will also return null
	 *
	 * @see FileChooser
	 *
	 * @param window the main stage of the game
	 * @return The file converts to a string
	 * @throws FileNotFoundException
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

	/**
	 * Reads a webpage, and the whole html context is converted to a string or null if the webpage is empty.
	 *
	 * @param url the webpage where the rle pattern is
	 * @return the whole webpage is return as a String
	 * @throws IOException
     */
	public String readUrl(String url) throws IOException {
			URL destination = new URL(url);
			URLConnection conn = destination.openConnection();
			rleString =readGameBoard(new InputStreamReader(conn.getInputStream()));


		return rleString;
	}

	/**
	 * The main reader is taking an instance of an input,<br>
	 * and iterates through the instance line by line until the end is reached.
	 *
	 * @param input the instance of of what should be read.
	 * @return A string of the what was read.
     */
	private String readGameBoard(Reader input){
		StringBuilder rleBuilder = new StringBuilder();
		try(BufferedReader reader	=	new	BufferedReader(
				 input)){
			String	line	=	null;
			while	((line	=	reader.readLine())	!=	null)	{
				rleBuilder.append(line).append("\n");
			}
		}catch(IOException ie)	{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("IO ERROR");
			alert.setContentText("Problem in Web/File IO");
			alert.showAndWait();
		}

		return rleBuilder.toString();
	}


	/*The 4 method under have the main function as the 3 over
	 The methods under is set to deprecated just in case the 3 method don't work properly.
	 The methods was change to not duplicate code.
	*/
	@Deprecated
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


	@Deprecated
	public String loadStringOfUrl(String url) throws IOException {
		if (url != null) {
			rleString = readGameBoardFromUrl(url);
		}
		return rleString;

	}

	@Deprecated
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

	@Deprecated
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