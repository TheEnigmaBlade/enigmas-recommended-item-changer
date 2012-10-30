package enigma.lol.lolitem.gui.models;

import static enigma.paradoxion.util.Logger.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.event.*;

import enigma.lol.lolitem.data.filter.*;
import enigma.lol.lolitem.gui.components.*;

public class DraggableItemContainerModel
{
	protected List<DraggableItem> allItems;
	protected List<DraggableItem> cachedFilteredItems;
	protected ItemFilterModel itemFilterModel;
	
	private List<ChangeListener> changeListeners;
	
	public DraggableItemContainerModel(ItemFilterModel filter)
	{
		allItems = new ArrayList<DraggableItem>();
		cachedFilteredItems = new ArrayList<DraggableItem>();
		itemFilterModel = filter;
		itemFilterModel.addFilterChangedListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent unused)
			{
				recacheFilteredItems();
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
	
	private void recacheFilteredItems()
	{
		writeToLog("Recaching items");
		cachedFilteredItems.clear();
		for(DraggableItem item : allItems)
			if(itemFilterModel.itemPassesFilters(item.getItem()))
				cachedFilteredItems.add(item);
		
		for(ChangeListener list : changeListeners)
			list.stateChanged(new ChangeEvent(this));
	}
	
	//Other
	
	public void setItemSize(int size)
	{
		for(DraggableItem item : allItems)
			item.setItemSize(size, size);
	}
	
	public void setFixedTooltip(boolean fixed)
	{
		for(DraggableItem i : allItems)
			i.setFixedTooltip(fixed);
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
