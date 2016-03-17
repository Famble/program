package GameOfLife;

import java.awt.Component;
import java.io.File;
import java.nio.file.*;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.nio.*;

public class FileHandeler {
	Path path = Paths.get(".");
	
	File f = path.toFile();
	JFileChooser	chooser	=	new	JFileChooser();	
	FileNameExtensionFilter	filter	= new FileNameExtensionFilter("Text	files",	"txt");	
	chooser.addChoosableFileFilter(filter);		
	chooser.addChoosableFileFilter(new			 			FileNameExtensionFilter("Image	files","jpg",	"gif",	"png"));	
	int returnVal	=	chooser.showOpenDialog(null);	
	if(returnVal	==	JFileChooser.APPROVE_OPTION)	{	
					System.out.println("You	have	chosen	the	file:	"	+	
								chooser.getSelectedFile().getName());
}
