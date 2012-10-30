package enigma.lol.lolitem.data.filter;

import java.util.*;

import enigma.lol.lollib.data.*;

public class ChampionPropertyFilter implements ChampionFilter
{
	private Set<ChampionProperty> properties;
	
	public ChampionPropertyFilter()
	{
		properties = new HashSet<ChampionProperty>();
	}
	
	public void addProperty(ChampionProperty p)
	{
		properties.add(p);
	}
	
	public void removeProperty(ChampionProperty p)
	{
		properties.remove(p);
	}
	
	@Override
	public boolean matches(Champion champ)
	{
		for(ChampionProperty property : properties)
			if(!champ.hasProperty(property))
				return false;
		return true;
	}
	
	public void clear()
	{
		properties.clear();
	}
}
