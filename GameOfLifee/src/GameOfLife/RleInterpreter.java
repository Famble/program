package GameOfLife;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RleInterpreter 
{
	private BufferedReader br;
	private Pattern pattern;
	private Matcher matcher;
	private String name;
	private int height;
	private int width;
	private String ruleString;
	
	public RleInterpreter(BufferedReader rleFile) throws IOException
	{
		br = rleFile;
		readHeader();
	}
	
	public void readHeader() throws IOException
	{
		String line;
		while((line = br.readLine()) != null)
		{
			Pattern pattern = Pattern.compile("^#"); //starts with #(comment)
			Matcher matcher = pattern.matcher(line);
			
			if(matcher.matches())
			{
				String headerLetter = line.substring(1, 1);
				if(headerLetter.equals("N"))
					name = line.substring(2);
				else if(headerLetter.equals("R"))
					ruleString = line.substring(2);
			}
			
			line = line.replaceAll("\\s","");
			
			pattern = Pattern.compile("^x=[0-9]+,y=[0-9]+");
			matcher = pattern.matcher(line);
			System.out.println(line);
			if(matcher.find())
			{
				pattern = Pattern.compile("[0-9]+");
				matcher = pattern.matcher(line);
		        List<String> listMatches = new ArrayList<String>();

				while(matcher.find())
					listMatches.add(matcher.group());
				
				width = Integer.valueOf(listMatches.get(0));
				height = Integer.valueOf(listMatches.get(1));

					
	
				
				System.out.printf("height width:(%d,%d)", height, width);
				
			}
		}
		
		
		
	}
	
	
	
}
