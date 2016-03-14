import javafx.scene.paint.Color;

public class Matrix
{

	private long[][] currGenerationB;
	private long[][] newGenerationB;
	private long[][] inactiveCells; //is 0 if cell is active and 1 if its inactive(every cell starts as active)!
	private long[][] newInactiveCells; //is 0 if cell is active and 1 if its inactive(every cell starts as active)!

	private int x;
	private int y;
	private int[] survivalRules = {2, 3};
	private int[] birthRules = {3};
	private Rules rules;
	private Color color = Color.WHITE;

	
	
	public Matrix(int x, int y, Rules rules)
	{
		currGenerationB = new long[x][y];
		newGenerationB = new long[x][y];
		inactiveCells = new long[x][y];
		newInactiveCells = new long[x][y];

		this.rules = rules;


		this.x = x;
		this.y = y;
	}
	
	public long[][] getNewInactiveCells() {
		return newInactiveCells;
	}

	public void setNewInactiveCells(long[][] newInactiveCells) {
		this.newInactiveCells = newInactiveCells;
	}

	public long[][] getInactiveCells() {
		return inactiveCells;
	}

	public void setInactiveCells(long[][] inactiveCells) {
		this.inactiveCells = inactiveCells;
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
				this.getInactiveCells()[i][j] = this.getNewInactiveCells()[i][j];
			}
		}
		
	}
	
	/**
	 * A method to set the rules of the game. With the logic.
	 */
	
	 //0 if cell is active and 1 if its inactive(every cell starts as active)!
	private void determineNextGeneration()
	{
		int aliveNeighbours;
		
		for(int i = 0; i < this.x; i++)
		{
			for(int j = 0; j < this.y; j++)
			{
				for(int k = 0; k < 64; k++)
				{
					//if cell is active
					if(((this.getInactiveCells()[i][j] >> k) & 1L) == 1)
					{
						aliveNeighbours = countNeighbours(i, j, k, true);
						setCell(i, j, k, aliveNeighbours);
					}
					
						
				}
			}
		}
		
	}
	//en aktiv celle er en celle som har endret på seg
	 //is 0 if cell is active and 1 if its inactive(every cell starts as active)!
	private int countNeighbours(int i, int j, int k, boolean checkForInactive)
	{
		int aliveNeighbours = 0;
		
		if(i == 0 || i == this.x-1 || (j == 0 && k == 0) || (j == this.y-1 && k == 63)) // on the border
		{
			this.getNewGeneration()[i][j] &= ~(1L << k); // kill bit
			this.getInactiveCells()[i][j] |=  (1L << k); // make active
			return 0;
		}
		else //not on the border
		{
		
		for(int x = -1; x <= 1; x++)
			for(int y = -1; y <= 1; y++)
			{
				if(y == -1 && k == 0)
				{
					if(((this.getCurrentGeneration()[i+x][j-1] >> (63)) & 1L) == 1)
					{
						aliveNeighbours++;	
					}
					if(checkForInactive)
					{
						//finds the amount of nieghbours to the cell and changes the state of the cell if the rules are satisfied
						setCell(i+x, j-1, 63, countNeighbours((i+x), j-1, (63), false)); 
					}
				}
				else if(y == 1 && k == 63)
				{
					if(((this.getCurrentGeneration()[i+x][j+1] >> (0)) & 1L) == 1) //if  neighbour cell is alive
					{
						aliveNeighbours++;
							
					}
					if(checkForInactive)
					{
						//finds the amount of neighbours to the cell and changes the state of the cell if the rules are satisfied
						setCell(i+x, j+1, 0, countNeighbours((i+x), j, (0), false)); 
					}
				
				}
				else
				{
					if(!( x == 0 && y == 0))
					{	
						if(((this.getCurrentGeneration()[i+x][j] >> (y+k)) & 1L) == 1) //if  neighbour cell is alive
						{
							aliveNeighbours++;
						}
						if(checkForInactive)
						{
							//finds the amount of neighbours to the cell and changes the state of the cell if the rules are satisfied
							setCell(i+x, j, y+k, countNeighbours((i+x), j, (y+k), false)); 
						}
					}
				}
			}
	
			
		}
		
		return aliveNeighbours;
			
	}
	
	 //is 0 if cell is active and 1 if its inactive(every cell starts as active)!
	private void setCell(int i, int j, int k, int aliveNeighbours)
	{
		boolean alive = (((this.getCurrentGeneration()[i][j] >> k) & 1) == 1);	
		
		if(!alive) //if cell is dead
		{				
			
			boolean birth = false; 						
			
			for(int l = 0; l < rules.getBirthRules().length && birth == false; l++)
				if(aliveNeighbours == rules.getBirthRules()[l])
					birth = true;
			
			if(birth)
			{
				this.getNewInactiveCells()[i][j] |= (1L << k); //make active(set bit to 0)
				this.getNewGeneration()[i][j] |= (1L << k);
			}
			else
			{
				this.getNewInactiveCells()[i][j] &= ~(1L << k); //add as inactive
				this.getNewGeneration()[i][j] &= ~(1L << k);
			}
		}
		else //if cell is alive
		{
			boolean survive = false;
			
			for(int l = 0; l < rules.getSurvivalRules().length && survive == false; l++)
				if(aliveNeighbours == rules.getSurvivalRules()[l])
					survive = true;
			
			if(!survive)
			{
				this.getNewGeneration()[i][j] &= ~(1L << k); // kill bit*/
				this.getNewInactiveCells()[i][j] |= (1L << k); //add to active
			}
			else
			{
				this.getNewGeneration()[i][j] |= (1L << k);
				this.getNewInactiveCells()[i][j] &= ~(1L << k); //add as inactive

			}


		}
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
