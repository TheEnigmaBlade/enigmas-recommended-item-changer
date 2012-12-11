package net.enigmablade.lol.lolitem.data.filter;

import net.enigmablade.lol.lollib.data.*;

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
	public boolean matches(Champion champion)
	{
		if(champion != null && text != null)
			return champion.getName().toLowerCase().contains(text.toLowerCase());
		return false;
	}

}
