import javafx.scene.paint.Color;

public class Cell 
{
	private boolean alive;
	private Color color;
	
	public Cell(Color color)
	{
		alive = false;
		this.color = color;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public boolean getAlive()
	{
		return alive;
	}
	
	public Color getColor()
	{
		return color;
	}

	
	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}
	
	
	
}
