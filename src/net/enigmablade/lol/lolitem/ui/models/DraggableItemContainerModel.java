package net.enigmablade.lol.lolitem.ui.models;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.event.*;
import net.enigmablade.lol.lolitem.data.filter.*;
import net.enigmablade.lol.lolitem.ui.components.items.*;

import static net.enigmablade.paradoxion.util.Logger.*;


public class DraggableItemContainerModel
{
	protected List<DraggableItem> allItems;
	protected List<DraggableItem> cachedFilteredItems;
	protected ItemFilterModel itemFilterModel;
	
	private List<ChangeListener> changeListeners;
	
	public enum SortMode {ASCENDING, DESCENDING};
	private SortMode sortMode = SortMode.DESCENDING;
	
	public DraggableItemContainerModel(ItemFilterModel filter)
	{
		allItems = new ArrayList<DraggableItem>();
		cachedFilteredItems = new ArrayList<DraggableItem>();
		itemFilterModel = filter;
		itemFilterModel.addFilterChangedListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent unused)
			{
				recacheItems();
			}
		});
		
		changeListeners = new LinkedList<ChangeListener>();
	}
	
	public void addItem(Component c)
	{
		DraggableItem item = (DraggableItem)c;
		allItems.add(item);
		if(itemFilterModel.itemPassesFilters(item.getItem()))
			cachedFilteredItems.add(item);
		
		for(ChangeListener list : changeListeners)
			list.stateChanged(new ChangeEvent(this));
	}
	
	private void recacheItems()
	{
		writeToLog("Recaching items");
		
		writeToLog("Sorting: "+sortMode, 1);
		Collections.sort(allItems, sortMode == SortMode.ASCENDING ? Collections.reverseOrder() : null);
		
		writeToLog("Caching", 1);
		cachedFilteredItems.clear();
		for(DraggableItem item : allItems)
			if(itemFilterModel.itemPassesFilters(item.getItem()))
				cachedFilteredItems.add(item);
		writeToLog("Cache size: "+cachedFilteredItems.size(), 2);
		
		for(ChangeListener list : changeListeners)
			list.stateChanged(new ChangeEvent(this));
	}
	
	//Other
	
	public void setItemSize(int size)
	{
		for(DraggableItem item : allItems)
			item.setItemSize(size, size);
	}
	
	public void setSortMode(SortMode mode)
	{
		sortMode = mode;
		recacheItems();
	}
	
	//Accessors
	
	public List<DraggableItem> getItems()
	{
		return cachedFilteredItems;
	}
	
	public int getSize()
	{
		return cachedFilteredItems.size();
	}
	
	public void addChangeListener(ChangeListener list)
	{
		changeListeners.add(list);
	}
}
