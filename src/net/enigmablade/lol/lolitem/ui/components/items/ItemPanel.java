package net.enigmablade.lol.lolitem.ui.components.items;

import javax.swing.*;
import net.enigmablade.lol.lolitem.ui.tooltip.*;
import net.enigmablade.lol.lollib.data.*;



public class ItemPanel extends JPanel
{
	protected Item item;
	
	@Override
	public JToolTip createToolTip()
	{
		return new ItemTooltip(item);
	}
	
	public void setToolTipText()
	{
		if(item != null)
			setToolTipText(item.getToolTip());
		else
			setToolTipText(null);
	}
	
	//Accessor methods
	
	public Item getItem()
	{
		return item;
	}
}
