public class Rules 
{
	private int[] survivalRules = {2, 3};
	private int[] birthRules = {3};

	public int[] getSurvivalRules() 
	{
		return survivalRules;
	}


	public void setSurvivalRules(String survivalRules) 
	{
		this.survivalRules = new int[survivalRules.length()];
		
		for(int i = 0; i < survivalRules.length(); i++)
		{
			this.survivalRules[i] = (int)Character.getNumericValue(survivalRules.charAt(i));
		}
		
	}


	public int[] getBirthRules()
	{
		return birthRules;
	}


	public void setBirthRules(String birthRules)
	{
		this.survivalRules = new int[birthRules.length()];
		
		for(int i = 0; i < birthRules.length(); i++)
		{
			this.survivalRules[i] = (int)Character.getNumericValue(birthRules.charAt(i));
		}
	}


	
}
