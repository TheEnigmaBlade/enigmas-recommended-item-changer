package enigma.lol.lolitem.gui.models;

import java.util.*;
import javax.swing.*;

import static enigma.paradoxion.util.Logger.*;

import enigma.lol.lolitem.data.*;

public class ItemSetComboBoxModel extends DefaultComboBoxModel<String>
{
	private List<ItemSet> globalBuilds, normalBuilds;
	
	public ItemSetComboBoxModel()
	{
		globalBuilds = new ArrayList<ItemSet>();
		normalBuilds = new ArrayList<ItemSet>();
	}
	
	public void updateLists(List<ItemSet> global, List<ItemSet> normal)
	{
		
		normalBuilds.clear();
		if(normal != null)
			normalBuilds.addAll(global);
		updateModel();
	}
	
	private void updateModel()
	{
		writeToLog("Rebuilding item set combo box model");
		System.out.println("Rebuilding item set model");
		
		Object selected = getSelectedItem();
		removeAllElements();
		
		boolean anyAdded = false;
		//Add global item sets
		if(globalBuilds.size() > 0)
		{
			System.out.println("\tAdding globals");
			addElement("---Global Builds");
			boolean added = addToModel(globalBuilds);
			anyAdded |= added;
			if(added)
			{
				addElement("---Champion Builds");
				addElement(null);
			}
			else
			{
				removeAllElements();
			}
		}
		//Add normal item sets
		System.out.println("\tAdding normals");
		boolean added = addToModel(normalBuilds);
		anyAdded |= added;
		if(!added && anyAdded)
		{
			removeElement(getSize()-1);
		}
		
		//If none added, add empty message
		if(!anyAdded)
		{
			removeAllElements();
			addElement("---No item sets");
		}
		
		setSelectedItem(selected != null ? selected : getElementAt(0));
	}
	
	private boolean addToModel(List<ItemSet> addList)
	{
		boolean added = false;
		for(ItemSet set : addList)
		{
			addElement(set.getName());
			added = true;
		}
		return added;
	}
	
	//Accessor methods
	
	public ItemSet getGlobalItemSet(int index)
	{
		return globalBuilds.get(index);
	}
	
	public ItemSet removeGlobalItemSet(int index)
	{
		ItemSet set = globalBuilds.remove(index);
		updateModel();
		return set;
	}
	
	public List<ItemSet> getGlobalItemSets()
	{
		return globalBuilds;
	}
	
	public void setGlobalItemSets(List<ItemSet> sets)
	{
		globalBuilds.clear();
		if(sets != null)
			globalBuilds.addAll(sets);
		updateModel();
	}
	
	public int getNumGlobalItemSets()
	{
		return globalBuilds.size();
	}
	
	
	public ItemSet getNormalItemSet(int index)
	{
		return normalBuilds.get(index);
	}
	
	public void addNormalItemSet(ItemSet set)
	{
		normalBuilds.add(set);
		updateModel();
	}
	
	public ItemSet removeNormalItemSet(int index)
	{
		ItemSet set = normalBuilds.remove(index);
		updateModel();
		return set;
	}
	
	public List<ItemSet> getNormalItemSets()
	{
		return normalBuilds;
	}
	
	public void setNormalItemSets(List<ItemSet> sets)
	{
		normalBuilds.clear();
		if(sets != null)
			normalBuilds.addAll(sets);
		updateModel();
	}
	
	public int getNumNormalItemSets()
	{
		return normalBuilds.size();
	}
}
