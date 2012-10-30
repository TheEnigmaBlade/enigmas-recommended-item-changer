package enigma.lol.lolitem.gui.components;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import enigma.paradoxion.ui.components.translucent.*;

import enigma.lol.lollib.data.*;

import enigma.lol.lolitem.gui.*;

public class ShopPanel extends TranslucentPanel
{
	private Set<Item> items;
	
	public ShopPanel(int a)
	{
		super(a);
		
		items = new HashSet<Item>();
	}

	public void showItemPanel(Item item)
	{
		String panelName = "item:"+item.getID();
		maybeCreateItemPanel(item, panelName);
		((CardLayout)getLayout()).show(this, panelName);
	}

	//Lazily init item panels
	private void maybeCreateItemPanel(Item item, String panelName)
	{
		if (items.contains(item))
			return;
		JPanel panel = createItemPanel(item);
		items.add(item);
		add(panel, panelName);
	}

	private JPanel createItemPanel(Item item)
	{
		JPanel itemPanel = new JPanel();
		itemPanel.setOpaque(false);
		itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
		
		Component glue25 = Box.createVerticalGlue();
		itemPanel.add(glue25);
		
		JLabel itemLabel = ComponentFactory.createShopLabel(item.getName());
		itemPanel.add(itemLabel);
		
		Component glue26 = Box.createVerticalGlue();
		itemPanel.add(glue26);
		
		return itemPanel;
	}
}
