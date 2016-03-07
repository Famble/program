

public class Matrix
{

	private byte[][] currGenerationB;
	private byte[][] newGenerationB;
	
	
	public Matrix(int x, int y)
	{

		currGenerationB = new byte[x][y];
		newGenerationB = new byte[x][y];		
	}
	
	
	
	/**
	 * A method to hold the determination of next generation array.
	 */
	
	/**
	 *  Uses method dermineNextGeneration to decide what the next Generation should 
	 *  be like and copies it over to the current Generation. 
	 */
	public void startNextGeneration()
	{
		determineNextGeneration();	
		
		for(int i = 0; i < 1000; i++)
		{
			for(int j = 0; j < 125; j++)
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
		int counter = 0;
		
		for(int i = 0; i < 1000; i++)
		{
			for(int j = 0; j < 125; j++)
			{
				for(int k = 0; k < 8; k++)
				{
					aliveNeighbours = getAmountOfNeighbours(i, j, k);
					
					alive = (((this.getCurrentGeneration()[i][j] >> k) & 1) == 1);
					
					if(alive)
						counter++;
					
					if(!alive)
					{
						if(aliveNeighbours == 3)
							this.getNewGeneration()[i][j] |= (1 << k); // ressurct bit
					}
					else
					{
						if(aliveNeighbours < 2 || aliveNeighbours > 3)
							this.getNewGeneration()[i][j] &= ~(1 << k); // kill bit
						else if(aliveNeighbours == 3 || aliveNeighbours == 2)
						{
							this.getNewGeneration()[i][j] |= (1 << k); // ressurect bit
						}
					}
						
				}
			}
		}
		
		System.out.printf("counter: %d", counter);
	}
	
	
	
	public int getAmountOfNeighbours(int i, int j, int k)
	{
		int aliveNeighbours = 0;
		
		if(i == 0 || i == 999 || (j == 0 && k == 0) || (j == 124 && k == 7)) // on the border
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
