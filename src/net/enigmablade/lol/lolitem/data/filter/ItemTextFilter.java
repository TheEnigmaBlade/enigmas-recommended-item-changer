package net.enigmablade.lol.lolitem.data.filter;

import net.enigmablade.lol.lollib.data.*;

public class ItemTextFilter implements ItemFilter
{
	private String text;
	
	public ItemTextFilter(String t)
	{
		text = t;
	}
	
	public void setText(String t)
	{
		text = t;
	}
	
	@Override
	public boolean matches(Item item)
	{
		return item.getName().toLowerCase().contains(text.toLowerCase());
	}

}
