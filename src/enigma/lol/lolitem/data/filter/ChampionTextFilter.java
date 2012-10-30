package enigma.lol.lolitem.data.filter;

import enigma.lol.lollib.data.*;

public class ChampionTextFilter implements ChampionFilter
{
	private String text;
	
	public ChampionTextFilter(String t)
	{
		text = t;
	}
	
	public void setText(String t)
	{
		text = t;
	}
	
	@Override
	public boolean matches(Champion champ)
	{
		return champ.getName().toLowerCase().contains(text.toLowerCase());
	}

}
