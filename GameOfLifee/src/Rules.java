import java.util.Arrays;
import java.util.HashMap;

public class Rules
{
    private int[] survivalRules = { 2, 3 };
    private int[] birthRules = { 3 };

    HashMap<String, int[][]> hmap = new HashMap<String, int[][]>();

    public Rules()
    {

	hmap.put("2x2", new int[][] { { 1, 2, 5 }, { 3, 6 } });
	hmap.put("34Life", new int[][] { { 3, 4 }, { 3, 4 } });
	hmap.put("Amoeba", new int[][] { { 1, 3, 5, 8 }, { 3, 5, 7 } });
	hmap.put("Assimilation", new int[][] { { 4, 5, 6, 7 }, { 3, 4, 5 } });
	hmap.put("Coagulations", new int[][] { { 2, 3, 5, 6, 7, 8 }, { 3, 7, 8 } });
	hmap.put("Conway's Life", new int[][] { { 2, 3 }, { 3 } });
	hmap.put("Coral", new int[][] { { 4, 5, 6, 7, 8 }, { 3 } });
	hmap.put("Day and Night", new int[][] { { 3, 4, 6, 7, 8 }, { 3, 6, 7, 8 } });
	hmap.put("Diamoeba", new int[][] { { 5, 6, 7, 8 }, { 3, 5, 6, 7, 8 } });
	hmap.put("Dot Life", new int[][] { { 0, 2, 3 }, { 3 } });
	hmap.put("Dry Life", new int[][] { { 2, 3 }, { 3, 7 } });
	hmap.put("Fredkin", new int[][] { { 0, 2, 4, 6, 8 }, { 1, 3, 5, 7 } });
	hmap.put("Gnarl", new int[][] { { 1 }, { 1 } });
	hmap.put("High Life", new int[][] { { 2, 3 }, { 3, 6 } });
	hmap.put("Life Without Death", new int[][] { { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, { 3 } });
	hmap.put("Live Free or Die", new int[][] { { 0 }, { 2 } });
	hmap.put("Long Life", new int[][] { { 5 }, { 3, 4, 5 } });
	hmap.put("Maze", new int[][] { { 1, 2, 3, 4, 5 }, { 3 } });
	hmap.put("Mazectric", new int[][] { { 1, 2, 3, 4 }, { 3 } });
	hmap.put("Move", new int[][] { { 2, 4, 5 }, { 3, 6, 8 } });
	hmap.put("Pseudo Life", new int[][] { { 2, 3, 8 }, { 3, 5, 7 } });
	hmap.put("Serviettes", new int[][] { { 9 }, // should be empty
		{ 2, 3, 4 } });
	hmap.put("Seeds", new int[][] { { 9 }, // should be empty but put 9 to
					       // prevent error
		{ 2 } });
	hmap.put("Stains", new int[][] { { 2, 3, 5, 6, 7, 8 }, { 3, 6, 7, 8 } });
	hmap.put("Vote 4/5", new int[][] { { 3, 5, 6, 7, 8 }, { 4, 6, 7, 8 } });
	hmap.put("Vote", new int[][] { { 4, 5, 6, 7, 8 }, { 5, 6, 7, 8 } });
	hmap.put("Replicator", new int[][] { { 1, 3, 5, 7 }, { 1, 3, 5, 7 } });
	hmap.put("Walled Cities", new int[][] { { 2, 3, 4, 5 }, { 4, 5, 6, 7, 8 } });

    }

    public int[] getSurvivalRules()
    {
	return survivalRules;
    }

    public void setRules(String name)
    {
	System.out.println(Arrays.toString(hmap.get(name)[1]));
	this.survivalRules = new int[hmap.get(name)[0].length];
	this.birthRules = new int[hmap.get(name)[1].length];

	for (int i = 0; i < hmap.get(name).length; i++)
	{
	    for (int j = 0; j < hmap.get(name)[i].length; j++)
	    {
		if (i == 0)
		    this.survivalRules[j] = hmap.get(name)[i][j];
		else
		    this.birthRules[j] = hmap.get(name)[i][j];

	    }
	}
    }

    public void setUserDefinedSurvivalRules(String survivalRules)
    {
	this.survivalRules = new int[survivalRules.length()];

	for (int i = 0; i < survivalRules.length(); i++)
	{
	    this.survivalRules[i] = (int) Character.getNumericValue(survivalRules.charAt(i));
	}

    }

    public int[] getBirthRules()
    {
	return birthRules;
    }

    public void setUserDefinedBirthRules(String birthRules)
    {
	this.survivalRules = new int[birthRules.length()];

	for (int i = 0; i < birthRules.length(); i++)
	{
	    this.survivalRules[i] = (int) Character.getNumericValue(birthRules.charAt(i));
	}
    }

}
