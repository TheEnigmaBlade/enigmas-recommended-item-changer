package net.enigmablade.lol.lolitem.ui.components;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import net.enigmablade.lol.lolitem.ui.components.items.*;
import net.enigmablade.lol.lolitem.ui.components.pretty.*;


public class ItemGroupListPanel extends JPanel
{
	private ComponentListener listener;
	private ActionListener removeListener;
	
	private JButton addGroupButton;
	private JPanel spacer;
	
	private List<ItemGroupPanel> groupPanels;
	
	public ItemGroupListPanel()
	{
		listener = new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent evt)
			{
				updateSize();
			}
		};
		
		addGroupButton = new PrettyButton("Add Group");
		add(addGroupButton);
		
		spacer = new JPanel();
		spacer.setOpaque(true);
		spacer.setPreferredSize(new Dimension(0, 6));
		add(spacer);
		
		groupPanels = new LinkedList<ItemGroupPanel>();
	}
	
	public void initActions(ActionListener addGroupListener, ActionListener removeGroupListener)
	{
		addGroupButton.addActionListener(addGroupListener);
		removeListener = removeGroupListener;
	}
	
	public void addGroup(ItemGroupPanel group)
	{
		Component[] cs = group.getComponents();
		for(int n = 0; n < cs.length; n++)
		{
			Component c = cs[n];
			c.setSize(getWidth()-10, c.getPreferredSize().height);
			c.setPreferredSize(new Dimension(getWidth()-10, c.getPreferredSize().height));
			add(c, getComponentCount()-2);
		}
		group.addComponentListener(listener);
		group.addRemoveListener(removeListener);
		group.setIndex(groupPanels.size());
		
		groupPanels.add(group);
		
		updateSize();
	}
	
	public void removeGroup(int index)
	{
		ItemGroupPanel group = groupPanels.remove(index);
		
		Component[] cs = group.getComponents();
		for(int n = 0; n < cs.length; n++)
		{
			Component c = cs[n];
			remove(c);
		}
		group.removeComponentListener(listener);
		group.removeRemoveListener(removeListener);
		
		updateSize();
		
		//Update remaining group indices
		for(int n = 0; n < groupPanels.size(); n++)
			groupPanels.get(n).setIndex(n);
	}
	
	public void removeAllGroups()
	{
		removeAll();
		groupPanels.clear();
		
		for(ItemGroupPanel group : groupPanels)
		{
			group.removeComponentListener(listener);
			group.removeRemoveListener(removeListener);
		}
		
		add(addGroupButton);
		add(spacer);
		
		updateSize();
	}
	
	//Helper methods
	
	private void updateSize()
	{
		int height = 0;
		for(Component c : getComponents())
		{
			height += c.getHeight();
			//c.setSize(getWidth(), c.getHeight());
			if(c instanceof BuildPanel)
				((BuildPanel)c).refreshPanel();
		}
		setPreferredSize(new Dimension(0, height));
		
		revalidate();
		repaint();
	}
	
	//Accessor methods
	
	public List<ItemGroupPanel> getGroups()
	{
		return groupPanels;
	}
	
	public void setEnabled(boolean enable)
	{
		addGroupButton.setEnabled(enable);
	}
}
