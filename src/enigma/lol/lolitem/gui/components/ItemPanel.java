package enigma.lol.lolitem.gui.components;

import javax.swing.*;

import enigma.lol.lollib.data.*;

import enigma.lol.lolitem.gui.tooltip.*;

public class ItemPanel extends JPanel
{
	protected Item item;
	
	private boolean tooltipFixed = true;
	
	@Override
	public JToolTip createToolTip()
	{
		return new ItemTooltip(item);
	}
	
	public void setFixedTooltip(boolean set)
	{
		tooltipFixed = set;
		setToolTipText();
	}
	
	public void setToolTipText()
	{
		if(item != null)
			setToolTipText(tooltipFixed ? item.getToolTipFixed() : item.getToolTip());
		else
			setToolTipText(null);
	}
	
	//Accessor methods
	
	public Item getItem()
	{
		return item;
	}
}
