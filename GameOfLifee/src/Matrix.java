

public class Matrix
{
	private int width;
	private int height;
	private byte[][] currGenerationB;
	private byte[][] newGenerationB;

	
	/**
	 * 
	 * @param x width 
	 * @param y	height
	 * Setting up current generation array, and new generation.
	 */
	public Matrix(int x, int y)
	{
		this.width = x;
		this.height = y;
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
	}
	
	/**
	 * A method to set the rules of the game. With the logic.
	 */
	
	private int countAliveNeighbours()
	{
		return height;
		
	}
	private void determineNextGeneration()
	{
		int x = width;
		int y = height;
		int aliveNeighbours;
		
		for(int i = 0; i < 1000; i++)
		{
			for(int j = 0; j < 125; j++)
			{
				for(int k = 0; k < 8; k++)
				{
					this.getCurrentGeneration()[i][j] ^= (1 << k); // flip every bit
				}
			}
		}
	}
		
		
		
		/*for(int i = 0; i < x; i++)
		{
			for(int j = 0; j < y; j++)
			{
				aliveNeighbours = 0;
				Cell currCell = this.currGeneration[i][j];
				Cell newCell = this.newGeneration[i][j];
				
				if(( i == 0 || j == 0 || i == x-1 || j == y-1 )) //if the cell is next to a border and is alive
				{
					if(currCell.getAlive())
						newCell.setAlive(false);// kill cell
				}
				else
				{
					for(int k = -1; k <= 1; k++)
					{
						for(int l = -1; l <= 1; l++)
						{
							if(currGeneration[i+k][j+l].getAlive() && !(k == l && l == 0))
							{
								aliveNeighbours++;
							}
						}
					}
					
					if(!currCell.getAlive()) //if cell is dead
					{	if(aliveNeighbours == 3) //3 alive neighbours then ressurect
						{
							newCell.setAlive(true);
						}
					}
					else
					{
						if(aliveNeighbours < 2) // kill
							newCell.setAlive(false);
						else if(aliveNeighbours > 3)
							newCell.setAlive(false);
						else
							newCell.setAlive(true);
					}
				}
					
				
				
			}
		}
	
	}
	
	
	/**
	 * A get metode.
	 * 
	 * @return currGeneration
	 * 
	 */
			
	public byte[][] getCurrentGeneration()
	{
		return this.currGenerationB;
	}
	
	/**
	 * A get metode.
	 * 
	 * @return newGeneration
	 * 
	 */
	public byte[][] getNewGeneration()
	{
		return this.newGenerationB;
	}
	
	
	
}
