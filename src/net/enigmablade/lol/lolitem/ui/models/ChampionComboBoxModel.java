package net.enigmablade.lol.lolitem.ui.models;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import net.enigmablade.lol.lolitem.data.filter.*;
import net.enigmablade.lol.lollib.data.*;

import static net.enigmablade.paradoxion.util.Logger.*;



public class ChampionComboBoxModel extends DefaultComboBoxModel<String>
{
	private ChampionFilterModel championFilterModel;
	private List<String> favoriteChampions, remainingChampions;
	
	public ChampionComboBoxModel(ChampionFilterModel model)
	{
		championFilterModel = model;
		championFilterModel.addFilterChangedListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e)
			{
				updateModel();
			}
		});
		updateModel();
	}
	
	public void updateLists(List<String> favorites, List<String> remaining)
	{
		favoriteChampions = new ArrayList<String>();
		if(favorites != null)
			favoriteChampions.addAll(favorites);
		remainingChampions = new ArrayList<String>(remaining);
		updateModel();
	}
	
	private void updateModel()
	{
		writeToLog("Updating champion combo box model");
		
		ListDataListener[] savedListeners = getListDataListeners();
		for(ListDataListener list : savedListeners)
			removeListDataListener(list);
		
		Object selected = getSelectedItem();
		removeAllElements();
		
		boolean anyAdded = false;
		if(favoriteChampions != null && favoriteChampions.size() > 0)
		{
			addElement("---Favorites");
			boolean added = addToModel(favoriteChampions, false);
			anyAdded |= added;
			if(added)
			{
				addElement("---");
				addElement(null);
			}
			else
			{
				removeAllElements();
			}
		}
		boolean added = addToModel(remainingChampions, true);
		anyAdded |= added;
		if(!added && anyAdded)
		{
			removeElement(getSize()-1);
		}
		
		if(!anyAdded)
		{
			removeAllElements();
			addElement("---No champions");
		}
		
		setSelectedItem(selected != null ? selected : getElementAt(0));
		
		for(ListDataListener list : savedListeners)
		{
			addListDataListener(list);
			list.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
		}
	}
	
	private boolean addToModel(List<String> addList, boolean addAlphaSep)
	{
		boolean added = false;
		if(addList != null)
		{
			char currentChar = '@';
			for(String champ : addList)
				if(championFilterModel.passesFilters(ChampionDatabase.getChampion(ChampionDatabase.getChampionKey(champ))))
				{
					if(addAlphaSep)
					{
						boolean charChanged = false;
						while(currentChar != champ.charAt(0))
						{
							charChanged = true;
							currentChar++;
						}
						if(charChanged)
						{
							addElement("---"+currentChar);
							addElement(null);
						}
					}
					addElement(champ);
					added = true;
				}
		}
		return added;
	}
}
