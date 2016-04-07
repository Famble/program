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
	private String rleString;
	private StringBuilder description = new StringBuilder();
	private long[][] startGeneration;
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public long[][] getStartGeneration() {
		return startGeneration;
	}

	public void setStartGeneration(long[][] startGeneration) {
		this.startGeneration = startGeneration;
	}

	public RleInterpreter(String rleFile) throws IOException
	{
		this.rleString = rleFile;
		readHeader();
		
		int cellsInLong = 64;
		for(int j = 0; j < this.height/64+1; j++)
		{
			if(j == this.height/64)
				cellsInLong = height%64;
			
			for(int bitPos = 0; bitPos < cellsInLong; bitPos++)
			{
				for(int i = 0; i < this.width;i++)
				{
				}
			}
		}
		
	}
	
	public void readHeader() throws IOException
	{
		
		Pattern regex = Pattern.compile("^#([N|C|O|P|R])\\s*(\\w.*)$", Pattern.MULTILINE);
		Matcher matcher = regex.matcher(this.rleString);
		
		
		while(matcher.find())
		{
			if(matcher.group(1).equalsIgnoreCase("N"))
			{
				name = matcher.group(2);
				System.out.println(name);
			}
			else if(matcher.group(1).equalsIgnoreCase("C"))
			{
				description.append(matcher.group(2)+ "\n");
				System.out.println(description.toString());
			}	
		}
		
		regex = Pattern.compile("x=([0-9]+),y=([0-9]+)(,rule=([A-Za-z]*[0-9]*/[A-Za-z]*[0-9]*))", Pattern.MULTILINE);

		//System.out.println(this.rleString);
		int amountOfSpaces = 0;
		for(int i = 0; i < this.rleString.length(); i++)
			if(rleString.charAt(i) == ' ')
				amountOfSpaces++;
				
		matcher = regex.matcher(this.rleString.replaceAll(" ",""));
		int lastIndexOfHeader = 0;
	
		while(matcher.find())
		{
			this.width = Integer.parseInt(matcher.group(1));
			this.height = Integer.parseInt(matcher.group(2));
			this.ruleString = matcher.group(3).replaceAll("[^/0-9]", "");
			lastIndexOfHeader = matcher.end() + amountOfSpaces;				
		}
		
			System.out.printf("width, height:(%d,%d)\n", this.width, this.height);
			System.out.println(this.ruleString);
		
		this.startGeneration = new long[width][height/64 + 1];
		
		
		String rlePattern[] = this.rleString.substring(lastIndexOfHeader).split("\\$");
		System.out.println(this.rleString.substring(lastIndexOfHeader));
		int aliveCells = 0;
		int deadCells = 0;
		int y = 0;
		int x = 0;
		int bitPos = 0;
		int yDiv64 = 0;
		
		for(String hey : rlePattern)
		{
			pattern = Pattern.compile("([0-9]*)([A-Za-z])");
			matcher = pattern.matcher(hey);
			
			while(matcher.find())
			{
				if(matcher.group(2).equals("b")) //dead cells
				{
					if(matcher.group(1).matches("[0-9]+"))
						deadCells = Integer.parseInt(matcher.group(1));
					else
						deadCells = 1;
					
					

					for(int i = 0;  i < deadCells; i++)
					{
						this.startGeneration[x + i][yDiv64] &= ~(1L << bitPos);
					}
					
					x += deadCells;

				}
				else if(matcher.group(2).equals("o"))
				{

					if(matcher.group(1).matches("[0-9]+"))
						aliveCells = Integer.parseInt(matcher.group(1));
					else
						aliveCells = 1;
					
					for(int i = 0; i < aliveCells; i++)
					{	
						this.startGeneration[x + i][yDiv64] |= (1L << bitPos);
					}	
					
					x += aliveCells;
				}
				
				if(x != width)
				{
					int remainingCells = width-x;
					for(int i = 0; i < remainingCells; i++)
						this.startGeneration[x + i][yDiv64] &= ~(1L << bitPos);

				}	
			}
			y++;
			x = 0;
			yDiv64 = y/64;
			bitPos = y%64;
			
		}
		
	
		
		

		
		
		
		
	}
	
	
	
}
