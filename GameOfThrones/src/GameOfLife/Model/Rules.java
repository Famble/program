package GameOfLife.Model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Rules implements Cloneable {
	private int[] survivalRules = { 2, 3 };
	private int[] birthRules = { 3 };
	int a = 3;

	   HashMap<String, int[][]> rules = new HashMap<String, int[][]>();

	    public Rules()
	    {

		rules.put("2x2", new int[][] { { 1, 2, 5 }, { 3, 6 } });
		rules.put("34Life", new int[][] { { 3, 4 }, { 3, 4 } });
		rules.put("Amoeba", new int[][] { { 1, 3, 5, 8 }, { 3, 5, 7 } });
		rules.put("Assimilation", new int[][] { { 4, 5, 6, 7 }, { 3, 4, 5 } });
		rules.put("Coagulations", new int[][] { { 2, 3, 5, 6, 7, 8 }, { 3, 7, 8 } });
		rules.put("Conway's Life", new int[][] { { 2, 3 }, { 3 } });
		rules.put("Coral", new int[][] { { 4, 5, 6, 7, 8 }, { 3 } });
		rules.put("Day and Night", new int[][] { { 3, 4, 6, 7, 8 }, { 3, 6, 7, 8 } });
		rules.put("Diamoeba", new int[][] { { 5, 6, 7, 8 }, { 3, 5, 6, 7, 8 } });
		rules.put("Dot Life", new int[][] { { 0, 2, 3 }, { 3 } });
		rules.put("Dry Life", new int[][] { { 2, 3 }, { 3, 7 } });
		rules.put("Fredkin", new int[][] { { 0, 2, 4, 6, 8 }, { 1, 3, 5, 7 } });
		rules.put("Gnarl", new int[][] { { 1 }, { 1 } });
		rules.put("High Life", new int[][] { { 2, 3 }, { 3, 6 } });
		rules.put("Life Without Death", new int[][] { { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, { 3 } });
		rules.put("Long Life", new int[][] { { 5 }, { 3, 4, 5 } });
		rules.put("Maze", new int[][] { { 1, 2, 3, 4, 5 }, { 3 } });
		rules.put("Mazectric", new int[][] { { 1, 2, 3, 4 }, { 3 } });
		rules.put("Move", new int[][] { { 2, 4, 5 }, { 3, 6, 8 } });
		rules.put("Pseudo Life", new int[][] { { 2, 3, 8 }, { 3, 5, 7 } });
		rules.put("Serviettes", new int[][] { { 9 }, // should be empty
			{ 2, 3, 4 } });
		rules.put("Seeds", new int[][] { { 9 }, // should be empty but put 9 to
						       // prevent error
			{ 2 } });
		rules.put("Stains", new int[][] { { 2, 3, 5, 6, 7, 8 }, { 3, 6, 7, 8 } });
		rules.put("Vote", new int[][] { { 4, 5, 6, 7, 8 }, { 5, 6, 7, 8 } });
		rules.put("Replicator", new int[][] { { 1, 3, 5, 7 }, { 1, 3, 5, 7 } });

	}//lol

	public int[] getSurvivalRules() {
		return survivalRules;
	}

	public void setRulesFromName(String name) {
		System.out.println(Arrays.toString(rules.get(name)[1]));
		this.survivalRules = new int[rules.get(name)[0].length];
		this.birthRules = new int[rules.get(name)[1].length];

		for (int i = 0; i < rules.get(name).length; i++) {
			for (int j = 0; j < rules.get(name)[i].length; j++) {
				if (i == 0)
					this.survivalRules[j] = rules.get(name)[i][j];
				else
					this.birthRules[j] = rules.get(name)[i][j];

			}
		}
	}

	public void setUserDefinedSurvivalRules(String survivalRules) {
		this.survivalRules = new int[survivalRules.length()];

		for (int i = 0; i < survivalRules.length(); i++) {
			this.survivalRules[i] = (int) Character.getNumericValue(survivalRules.charAt(i));
		}

	}

	public int[] getBirthRules() {
		return birthRules;
	}

	public void setUserDefinedBirthRules(String birthRules) {
		this.survivalRules = new int[birthRules.length()];

		for (int i = 0; i < birthRules.length(); i++) {
			this.survivalRules[i] = (int) Character.getNumericValue(birthRules.charAt(i));
		}
	}
	
	public Object clone(){
		
		Rules rulesCopy;
		try{
			rulesCopy = (Rules)super.clone();
			Map<String, int[][]> mapCopy = new HashMap<>();
			for(HashMap.Entry<String, int[][]> entry : rules.entrySet()){
				mapCopy.put(entry.getKey(), entry.getValue());
			}
			rulesCopy.rules = (HashMap<String, int[][]>) mapCopy;

			
		}catch(CloneNotSupportedException e){
			return null;
		}
		
		return rulesCopy;
		
	}

}
