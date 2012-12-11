package net.enigmablade.lol.lolitem.ui.models;

import java.util.*;
import javax.swing.*;
import net.enigmablade.lol.lolitem.data.*;

import static net.enigmablade.paradoxion.util.Logger.*;


public class ItemSetComboBoxModel extends DefaultComboBoxModel<String>
{
	private List<ItemGroup> globalBuilds, normalBuilds;
	
	public ItemSetComboBoxModel()
	{
		globalBuilds = new ArrayList<ItemGroup>();
		normalBuilds = new ArrayList<ItemGroup>();
	}
	
	public void updateLists(List<ItemGroup> global, List<ItemGroup> normal)
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
	
	private boolean addToModel(List<ItemGroup> addList)
	{
		boolean added = false;
		for(ItemGroup set : addList)
		{
			addElement(set.getName());
			added = true;
		}
		return added;
	}
	
	//Accessor methods
	
	public ItemGroup getGlobalItemSet(int index)
	{
		return globalBuilds.get(index);
	}
	
	public ItemGroup removeGlobalItemSet(int index)
	{
		ItemGroup set = globalBuilds.remove(index);
		updateModel();
		return set;
	}
	
	public List<ItemGroup> getGlobalItemSets()
	{
		return globalBuilds;
	}
	
	public void setGlobalItemSets(List<ItemGroup> sets)
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
	
	
	public ItemGroup getNormalItemSet(int index)
	{
		return normalBuilds.get(index);
	}
	
	public void addNormalItemSet(ItemGroup set)
	{
		normalBuilds.add(set);
		updateModel();
	}
	
	public ItemGroup removeNormalItemSet(int index)
	{
		ItemGroup set = normalBuilds.remove(index);
		updateModel();
		return set;
	}
	
	public List<ItemGroup> getNormalItemSets()
	{
		return normalBuilds;
	}
	
	public void setNormalItemSets(List<ItemGroup> sets)
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
