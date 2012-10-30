package enigma.lol.lolitem.data;

import java.util.*;

import enigma.lol.lollib.data.*;

public class ItemSet
{
	private String name;
	private Item[] items;
	private boolean isGlobal;
	
	public ItemSet(String n)
	{
		this(n, new Item[6]);
	}
	
	public ItemSet(String n, Item[] i)
	{
		this(n, i, false);
	}
	
	public ItemSet(String n, Item[] i, boolean g)
	{
		name = n;
		items = i;
		isGlobal = g;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String n)
	{
		name = n;
	}
	
	public Item getItem(int i)
	{
		return items[i];
	}
	
	public void setItem(Item item, int i)
	{
		items[i] = item;
	}
	
	public boolean isGlobal()
	{
		return isGlobal;
	}
	
	public void reset()
	{
		items = new Item[6];
	}
	
	//Overridden methods
	
	@Override
	public boolean equals(Object o)
	{
		if(o == null || !(o instanceof ItemSet))
			return false;
		ItemSet set = (ItemSet)o;
		if(!name.equals(set.name))
			return false;
		for(int n = 0; n < items.length; n++)
			if(!items[n].equals(set.items[n]))
				return false;
		return true;
	}
	
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
	
	@Override
	public String toString()
	{
		String text = "";
		for(int n = 0; n < items.length; n++)
			text += items[n]+(n != items.length-1 ? ", " : "");
		return name+" ["+text+"]";
	}
	
	@Override
	public ItemSet clone()
	{
		return new ItemSet(name, Arrays.copyOf(items, items.length));
	}
}
