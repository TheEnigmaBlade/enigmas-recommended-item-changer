package net.enigmablade.lol.lolitem.ui;

import java.util.*;

import static net.enigmablade.paradoxion.util.Logger.*;

public enum ItemDisplayMode
{
	GRID("LIST"), LIST("GRID");
	
	private String next;
	
	private ItemDisplayMode(String n)
	{
		next = n;
	}
	
	public ItemDisplayMode getNextMode()
	{
		return valueOf(next);
	}
	
	public static ItemDisplayMode getType(String s)
	{
		ItemDisplayMode type = ItemDisplayMode.valueOf(s.toUpperCase(Locale.ENGLISH));
		if(type != null)
			return type;
		writeToLog("ItemDisplayType - Invalid conversion: "+s, LoggingType.WARNING);
		return GRID;
	}
};
