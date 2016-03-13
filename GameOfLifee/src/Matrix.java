import javafx.scene.paint.Color;

public class Matrix
{

	private long[][] currGenerationB;
	private long[][] newGenerationB;
	private int x;
	private int y;
	private int[] survivalRules = {2, 3};
	private int[] birthRules = {3};
	private Color color = Color.WHITE;

	
	
	public Matrix(int x, int y)
	{

		currGenerationB = new long[x][y];
		newGenerationB = new long[x][y];	
		this.x = x;
		this.y = y;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public Color getColor()
	{
		return this.color;
	}
	
	public void setSurvivalRules(int[] array)
	{
		this.survivalRules = array;
	}
	
	public void setBirthRules(int[] array)
	{
		this.birthRules = array;
	}
	
	public int getX()
	{
		return this.x;
	}
	
	public int getY()
	{
		return this.y;
	}
	
	public void startNextGeneration()
	{
		determineNextGeneration();	
		
		for(int i = 0; i < this.x; i++)
		{
			for(int j = 0; j < this.y; j++)
			{
				this.getCurrentGeneration()[i][j] = this.getNewGeneration()[i][j];
			}
		}
		
	}
	
	/**
	 * A method to set the rules of the game. With the logic.
	 */
	
	private void determineNextGeneration()
	{
		int aliveNeighbours;
		boolean alive;
		
		for(int i = 0; i < this.x; i++)
		{
			for(int j = 0; j < this.y; j++)
			{
				for(int k = 0; k < 64; k++)
				{
					aliveNeighbours = countNeighbours(i, j, k);
					
					alive = (((this.getCurrentGeneration()[i][j] >> k) & 1) == 1);
					
					
					if(!alive) //if cell is dead
					{				
						
						boolean birth = false; 
						
						for(int l = 0; l < this.birthRules.length; l++ )
							if(aliveNeighbours == birthRules[l])
								birth = true;
						
						if(birth)
							this.getNewGeneration()[i][j] |= (1L << k); // ressurct bit
	
					}
					else //if cell is alive
					{
						boolean survive = false;
						
						for(int l = 0; l < this.survivalRules.length; l++)
							if(aliveNeighbours == survivalRules[l])
								survive = true;
						
						if(!survive)
							this.getNewGeneration()[i][j] &= ~(1L << k); // kill bit*/

					}
						
				}
			}
		}
		
	}
	
	
	
	private int countNeighbours(int i, int j, int k)
	{
		int aliveNeighbours = 0;
		
		if(i == 0 || i == this.x-1 || (j == 0 && k == 0) || (j == this.y-1 && k == 63)) // on the border
		{
			this.getNewGeneration()[i][j] &= ~(1L << k); // kill bit
			return 0;
		}
		
		else //not on the border
		{
			if(k > 0 && k < 63)
			{
				for(int x = -1; x <= 1; x++)
					for(int y = -1; y <= 1; y++)
					{
						//does AND operator on the neighboring bits(will return and add one if cell is alive)
						if(!( x == 0 && y == 0))
							aliveNeighbours += ((this.getCurrentGeneration()[i+x][j] >> (y+k)) & 1L);
					}
			}
			else if(k == 0)
			{
			
				for(int x = -1; x <= 1; x++)
					for(int y = -1; y <= 1; y++)
					{
						if(y == -1)
						{
							aliveNeighbours += ((this.getCurrentGeneration()[i+x][j-1] >> (63)) & 1L);
						}
						else
						{
							//does AND operator on the neighboring bits(will return and add one if cell is alive)
							if(!( x == 0 && y == 0))
								aliveNeighbours += ((this.getCurrentGeneration()[i+x][j] >> (y+k)) & 1L);
						}
						
					}
			}else//(k == 63)
			{
				for(int x = -1; x <= 1; x++)
					for(int y = -1; y <= 1; y++)
					{
						if(y == 1)
						{
							aliveNeighbours += ((this.getCurrentGeneration()[i+x][j+1] >> (0)) & 1L);
						}
						else
						{
							//does AND operator on the neighboring bits(will return and add one if cell is alive)
							if(!( x == 0 && y == 0))
								aliveNeighbours += ((this.getCurrentGeneration()[i+x][j] >> (y+k)) & 1L);
						}
						
					}
			}
			
		}
		
		return aliveNeighbours;
			
	}
		

	public long[][] getCurrentGeneration()
	{
		return this.currGenerationB;
	}
	
	public long[][] getNewGeneration()
	{
		return this.newGenerationB;
	}


	
	
	
}
