package enigma.lol.lolitem.data.filter;

import java.util.*;
import javax.swing.event.*;

import static enigma.paradoxion.util.Logger.*;

import enigma.lol.lollib.data.*;

public class ItemFilterModel
{
	private final ItemTextFilter textFilter;
	private final ItemPropertyFilter itemPropertyFilter;
	private final RelatedItemFilter relatedItemFilter;
	private final List<ItemFilter> allFilters;
	
	private final List<ChangeListener> filterChangedListeners, relatedItemFilterChangedListeners;
	
	public static final Object ITEM_CLEARED = new Object();
	
	public ItemFilterModel()
	{
		textFilter = new ItemTextFilter("");
		itemPropertyFilter = new ItemPropertyFilter();
		relatedItemFilter = new RelatedItemFilter();
		allFilters = Arrays.asList(new ItemFilter[]{textFilter, itemPropertyFilter, relatedItemFilter});
		
		filterChangedListeners = new ArrayList<ChangeListener>();
		relatedItemFilterChangedListeners = new ArrayList<ChangeListener>();
	}
	
	public boolean itemPassesFilters(Item item)
	{
		for(ItemFilter filter : allFilters)
			if(!filter.matches(item))
				return false;
		return true;
	}
	
	//Add-remove filter methods
	
	public void addItemPropertyFilter(ItemProperty p)
	{
		writeToLog("Adding item property filter: "+p);
		itemPropertyFilter.addProperty(p);
		notify(filterChangedListeners, this);
	}
	
	public void removeItemPropertyFilter(ItemProperty p)
	{
		if(itemPropertyFilter.hasProperty(p))
		{
			writeToLog("Removing item property filter: "+p);
			itemPropertyFilter.removeProperty(p);
			notify(filterChangedListeners, this);
		}
	}
	
	public void clearItemPropertyFilters()
	{
		writeToLog("Clearing item property filters");
		itemPropertyFilter.clear();
		notify(filterChangedListeners, this);
	}
	
	public void setRelatedItemFilter(Item item)
	{
		writeToLog("Setting related item filter: "+item);
		writeToLog("Components: "+item.getComponentItems(), 1);
		writeToLog("Builds-into: "+item.getParentItems(), 1);
		relatedItemFilter.setItem(item);
		notify(relatedItemFilterChangedListeners, item);
		notify(filterChangedListeners, this);
	}
	
	public void clearRelatedItemFilter()
	{
		writeToLog("Clearing related item filter");
		relatedItemFilter.clearItem();
		notify(relatedItemFilterChangedListeners, ITEM_CLEARED);
		notify(filterChangedListeners, this);
	}
	
	public void setTextFilter(String s)
	{
		writeToLog("Setting item text filter to: \""+s+"\"");
		textFilter.setText(s);
		notify(filterChangedListeners, this);
	}
	
	//Listeners

	public void addFilterChangedListener(ChangeListener listener)
	{
		filterChangedListeners.add(listener);
	}
	
	public void addRelatedItemFilterChangedListener(ChangeListener listener)
	{
		relatedItemFilterChangedListeners.add(listener);
	}
	
	private void notify(List<ChangeListener> listeners, Object source)
	{
		for(ChangeListener listener : listeners)
			listener.stateChanged(new ChangeEvent(source));
	}
}
