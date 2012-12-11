package net.enigmablade.lol.lolitem.data.filter;

import java.util.*;
import javax.swing.event.*;
import net.enigmablade.lol.lollib.data.*;

import static net.enigmablade.paradoxion.util.Logger.*;


public class ChampionFilterModel
{
	private final ChampionTextFilter textFilter;
	private final ChampionPropertyFilter propertyFilter;
	private final List<ChampionFilter> allFilters;
	
	private final List<ChangeListener> filterChangedListeners;
		
	public ChampionFilterModel()
	{
		textFilter = new ChampionTextFilter("");
		propertyFilter = new ChampionPropertyFilter();
		allFilters = Arrays.asList(new ChampionFilter[]{textFilter, propertyFilter});
		
		filterChangedListeners = new ArrayList<ChangeListener>();
	}
	
	public boolean passesFilters(Champion champ)
	{
		for(ChampionFilter filter : allFilters)
			if(!filter.matches(champ))
				return false;
		return true;
	}
	
	//Add-remove filter methods
	
	public void addPropertyFilter(ChampionProperty p)
	{
		writeToLog("Adding champion property filter: "+p);
		propertyFilter.addProperty(p);
		notify(filterChangedListeners);
	}
	
	public void removePropertyFilter(ChampionProperty p)
	{
		writeToLog("Removing champion property filter: "+p);
		propertyFilter.removeProperty(p);
		notify(filterChangedListeners);
	}
	
	public void clearPropertyFilters()
	{
		writeToLog("Clearing champion property filters");
		propertyFilter.clear();
		notify(filterChangedListeners);
	}
	
	public void setTextFilter(String s)
	{
		writeToLog("Setting champion text filter to: \""+s+"\"");
		textFilter.setText(s);
		notify(filterChangedListeners);
	}
	
	//Listeners

	public void addFilterChangedListener(ChangeListener listener)
	{
		filterChangedListeners.add(listener);
	}
	
	private void notify(List<ChangeListener> listeners)
	{
		for(ChangeListener listener : listeners)
			listener.stateChanged(new ChangeEvent(this));
	}
}
