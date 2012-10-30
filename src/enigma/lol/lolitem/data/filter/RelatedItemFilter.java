package enigma.lol.lolitem.data.filter;

import enigma.lol.lollib.data.*;

public class RelatedItemFilter implements ItemFilter
{
	private Item item;
	
	public RelatedItemFilter()
	{
		item = null;
	}
	
	public void setItem(Item i)
	{
		item = i;
	}
	
	public void clearItem()
	{
		item = null;
	}
	
	@Override
	public boolean matches(Item i)
	{
		if(item == null)
			return true;
		return item.equals(i) || item.getComponentItems().contains(i) || item.getParentItems().contains(i);
	}

}
