package enigma.lol.lolitem.data;

import java.util.*;

import enigma.lol.lollib.data.*;

public class ItemBuilds
{
	private Map<Champion, List<ItemSet>> normal, dominion;
	
	public ItemBuilds()
	{
		normal = new HashMap<Champion, List<ItemSet>>();
		dominion = new HashMap<Champion, List<ItemSet>>();
	}
	
	public ItemBuilds(Map<Champion, List<ItemSet>> normalBuilds, Map<Champion, List<ItemSet>> dominionBuilds)
	{
		normal = normalBuilds;
		dominion = dominionBuilds;
	}
	
	public void addChampion(GameMode mode, Champion champ)
	{
		normal.put(champ, new LinkedList<ItemSet>());
		dominion.put(champ, new LinkedList<ItemSet>());
	}
	
	public void addBuild(GameMode mode, Champion champ, ItemSet set)
	{
		List<ItemSet> sets = (mode == GameMode.DOMINION ? dominion : normal).get(champ);
		if(sets != null)
			sets.add(set);
	}
	
	public Set<Champion> getChampions(GameMode mode)
	{
		return (mode == GameMode.DOMINION ? dominion : normal).keySet();
	}
	
	public List<ItemSet> getItemSets(GameMode mode, Champion champ)
	{
		return (mode == GameMode.DOMINION ? dominion : normal).get(champ);
	}
}
