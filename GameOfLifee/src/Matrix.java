

public class Matrix
{

	private byte[][] currGenerationB;
	private byte[][] newGenerationB;
	private int x;
	private int y;
	private int[] survivalRules = {2, 3};
	private int[] birthRules = {3};

	
	
	public Matrix(int x, int y)
	{

		currGenerationB = new byte[x][y];
		newGenerationB = new byte[x][y];	
		this.x = x;
		this.y = y;
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
				for(int k = 0; k < 8; k++)
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
							this.getNewGeneration()[i][j] |= (1 << k); // ressurct bit
	
					}
					else //if cell is alive
					{
						boolean survive = false;
						
						for(int l = 0; l < this.survivalRules.length; l++)
							if(aliveNeighbours == survivalRules[l])
								survive = true;
						
						if(!survive)
							this.getNewGeneration()[i][j] &= ~(1 << k); // kill bit*/

					}
						
				}
			}
		}
		
	}
	
	
	
	private int countNeighbours(int i, int j, int k)
	{
		int aliveNeighbours = 0;
		
		if(i == 0 || i == this.x-1 || (j == 0 && k == 0) || (j == this.y-1 && k == 7)) // on the border
		{
			this.getNewGeneration()[i][j] &= ~(1 << k); // kill bit
			return 0;
		}
		
		else //not on the border
		{
			if(k > 0 && k < 7)
			{
				for(int x = -1; x <= 1; x++)
					for(int y = -1; y <= 1; y++)
					{
						//does AND operator on the neighboring bits(will return and add one if cell is alive)
						if(!( x == 0 && y == 0))
							aliveNeighbours += ((this.getCurrentGeneration()[i+x][j] >> (y+k)) & 1);
					}
			}
			else if(k == 0)
			{
			
				for(int x = -1; x <= 1; x++)
					for(int y = -1; y <= 1; y++)
					{
						if(y == -1)
						{
							aliveNeighbours += ((this.getCurrentGeneration()[i+x][j-1] >> (7)) & 1);
						}
						else
						{
							//does AND operator on the neighboring bits(will return and add one if cell is alive)
							if(!( x == 0 && y == 0))
								aliveNeighbours += ((this.getCurrentGeneration()[i+x][j] >> (y+k)) & 1);
						}
						
					}
			}else//(k == 7)
			{
				for(int x = -1; x <= 1; x++)
					for(int y = -1; y <= 1; y++)
					{
						if(y == 1)
						{
							aliveNeighbours += ((this.getCurrentGeneration()[i+x][j+1] >> (0)) & 1);
						}
						else
						{
							//does AND operator on the neighboring bits(will return and add one if cell is alive)
							if(!( x == 0 && y == 0))
								aliveNeighbours += ((this.getCurrentGeneration()[i+x][j] >> (y+k)) & 1);
						}
						
					}
			}
			
		}
		
		return aliveNeighbours;
			
	}
		

	public byte[][] getCurrentGeneration()
	{
		return this.currGenerationB;
	}
	
	public byte[][] getNewGeneration()
	{
		return this.newGenerationB;
	}


	
	
	
}
