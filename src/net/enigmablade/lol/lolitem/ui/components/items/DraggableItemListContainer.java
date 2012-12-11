package net.enigmablade.lol.lolitem.ui.components.items;

import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import net.enigmablade.lol.lolitem.ui.models.*;


public class DraggableItemListContainer extends DraggableItemContainer
{
	public DraggableItemListContainer(DraggableItemContainerModel m, ActionListener list, ActionListener returnList)
	{
		super(m, list, returnList);
		
		setLayout(new GridLayout(model.getSize(), 1, 0, itemGap/2));
		setBorder(new EmptyBorder(8, 8, 8, 8));
	}
	
	@Override
	protected void addComponent(Component c)
	{
		((DraggableItem)c).setRenderMode(DraggableItem.RenderMode.LIST);
		super.addComponent(c);
	}
	
	@Override
	protected void updateSize()
	{
		setBorder(new EmptyBorder(itemGap, itemGap, itemGap, itemGap));
		((GridLayout)getLayout()).setRows(model.getSize());
		
		int width = getWidth();
		if(width > 0)
		{
			int size = model.getSize();
			int gap = itemGap;
			setPreferredSize(new Dimension(0, size*(itemSize+gap)+gap/2+10));
		}
	}
}
