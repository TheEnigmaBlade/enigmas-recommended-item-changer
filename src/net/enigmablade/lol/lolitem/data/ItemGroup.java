package net.enigmablade.lol.lolitem.data;

import java.util.*;
import net.enigmablade.lol.lollib.data.*;

public class ItemGroup
{
	private String name;
	private List<Item> items;
	private List<Integer> itemCounts;
	
	public ItemGroup(String n)
	{
		this(n, new ArrayList<Item>(), new ArrayList<Integer>());
	}
	
	public ItemGroup(String n, List<Item> i, List<Integer> iC)
	{
		name = n;
		items = i;
		itemCounts = iC;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String n)
	{
		name = n;
	}
	
	public void addItem(Item i, int count)
	{
		items.add(i);
		itemCounts.add(count);
	}
	
	public List<Item> getItems()
	{
		return items;
	}
	
	public List<Integer> getItemCounts()
	{
		return itemCounts;
	}
	
	//Overridden methods
	
	@Override
	public boolean equals(Object o)
	{
		if(o == null || !(o instanceof ItemGroup))
			return false;
		ItemGroup set = (ItemGroup)o;
		if(!name.equals(set.name))
			return false;
		for(int n = 0; n < items.size(); n++)
			if(!items.get(n).equals(set.items.get(n)))
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
		for(int n = 0; n < items.size(); n++)
			text += items.get(n)+(n != items.size()-1 ? ", " : "");
		return name+" ["+text+"]";
	}
	
	@Override
	public ItemGroup clone()
	{
		return new ItemGroup(name, new ArrayList<Item>(items), new ArrayList<Integer>(itemCounts));
	}
}
