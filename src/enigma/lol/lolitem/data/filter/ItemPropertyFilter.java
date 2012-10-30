package enigma.lol.lolitem.data.filter;

import java.util.*;

import enigma.lol.lollib.data.*;

public class ItemPropertyFilter implements ItemFilter
{
	private Set<ItemProperty> properties;
	
	public ItemPropertyFilter()
	{
		properties = new HashSet<ItemProperty>();
	}
	
	public void addProperty(ItemProperty p)
	{
		properties.add(p);
	}
	
	public void removeProperty(ItemProperty p)
	{
		properties.remove(p);
	}
	
	public boolean hasProperty(ItemProperty p)
	{
		return properties.contains(p);
	}
	
	@Override
	public boolean matches(Item item)
	{
		boolean viktor = false;
		boolean rengar = false;
		for(ItemProperty property : properties)
		{
			if(property == ItemProperty.VIKTOR)
				viktor = true;
			else if(property == ItemProperty.RENGAR)
				rengar = true;
			else if(!item.hasProperty(property))
				return false;
				
		}
		if(!viktor && item.hasProperty(ItemProperty.VIKTOR))
			return false;
		else if(!rengar && item.hasProperty(ItemProperty.RENGAR))
			return false;
		return true;
	}

	public void clear()
	{
		properties.clear();
	}
}
