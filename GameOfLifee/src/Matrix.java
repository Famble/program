

public class Matrix
{
	private Cell[][] currGeneration;
	private Cell[][] newGeneration;
	private int width;
	private int height;
	
	public Matrix(int x, int y)
	{
		this.width = x;
		this.height = y;
		currGeneration = new Cell[x][y];
		newGeneration = new Cell[x][y];
	}
	
	public void copyNewGenToCurrGen()
	{
		for(int i = 0; i < 80; i++)
			for(int j = 0; j < 50; j++)
				this.currGeneration[i][j].setAlive(this.newGeneration[i][j].getAlive());

	}
	
	public void startNextGeneration()
	{
		determineNextGeneration();
		copyNewGenToCurrGen();
		
	}
	
	private void determineNextGeneration()
	{
		int x = width;
		int y = height;
		int aliveNeighbours;
		 
		for(int i = 0; i < x; i++)
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
	
	
	public Cell[][] getCurrentGeneration()
	{
		return this.currGeneration;
	}
	
	public Cell[][] getNewGeneration()
	{
		return this.newGeneration;
	}
	
	
	
}
