package enigma.lol.lolitem.gui.models;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import static enigma.paradoxion.util.Logger.*;

import enigma.lol.lollib.data.*;

import enigma.lol.lolitem.data.filter.*;

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
		
		Object selected = getSelectedItem();
		removeAllElements();
		
		boolean anyAdded = false;
		if(favoriteChampions.size() > 0)
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
	}
	
	private boolean addToModel(List<String> addList, boolean addAlphaSep)
	{
		boolean added = false;
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
		return added;
	}
}
