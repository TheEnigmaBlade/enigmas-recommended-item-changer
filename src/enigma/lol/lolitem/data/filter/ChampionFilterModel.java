package enigma.lol.lolitem.data.filter;

import java.util.*;
import javax.swing.event.*;

import static enigma.paradoxion.util.Logger.*;

import enigma.lol.lollib.data.*;

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
		writeToLog("Adding item property filter: "+p);
		propertyFilter.addProperty(p);
		notify(filterChangedListeners, this);
	}
	
	public void removePropertyFilter(ChampionProperty p)
	{
		writeToLog("Removing champion property filter: "+p);
		propertyFilter.removeProperty(p);
		notify(filterChangedListeners, this);
	}
	
	public void clearPropertyFilters()
	{
		writeToLog("Clearing champion property filters");
		propertyFilter.clear();
		notify(filterChangedListeners, this);
	}
	
	public void setTextFilter(String s)
	{
		writeToLog("Setting champion text filter to: \""+s+"\"");
		textFilter.setText(s);
		notify(filterChangedListeners, this);
	}
	
	//Listeners

	public void addFilterChangedListener(ChangeListener listener)
	{
		filterChangedListeners.add(listener);
	}
	
	private void notify(List<ChangeListener> listeners, Object source)
	{
		for(ChangeListener listener : listeners)
			listener.stateChanged(new ChangeEvent(source));
	}
}
