package net.enigmablade.lol.lolitem.data;

import java.util.*;

public class ItemBuild implements Comparable<ItemBuild>
{
	private String name;
	private String description;
	private String author;
	private String type;
	
	private String map, mode;
	
	private List<ItemGroup> itemSets;
	
	private boolean isPriority;
	
	public ItemBuild(String name, String map, String mode)
	{
		this(name, map, mode, new ArrayList<ItemGroup>());
	}
	
	public ItemBuild(String name, String map, String mode, List<ItemGroup> itemSets)
	{
		this.name = name;
		this.itemSets = itemSets;
		
		description = "";
		author = (author = System.getProperty("user.name")) == null ? "" : author;
		type = "custom";
		
		isPriority = false;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setAuthor(String author)
	{
		this.author = author;
	}
	
	public String getAuthor()
	{
		return author;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getMap()
	{
		return map;
	}
	
	public String getMode()
	{
		return mode;
	}
	
	public void setPriority(boolean isPriority)
	{
		this.isPriority = isPriority;
	}
	
	public boolean isPriority()
	{
		return isPriority;
	}
	
	public void addGroup(ItemGroup set)
	{
		itemSets.add(set);
	}
	
	public void removeGroup(int index)
	{
		itemSets.remove(index);
	}
	
	public List<ItemGroup> getGroups()
	{
		return itemSets;
	}
	
	//Overrides
	
	@Override
	public String toString()
	{
		return "ItemBuild[name=\""+name+"\", type=\""+type+"\", author=\""+author+"\"]";
	}

	@Override
	public int compareTo(ItemBuild o)
	{
		return name.compareTo(o.name);
	}
}
