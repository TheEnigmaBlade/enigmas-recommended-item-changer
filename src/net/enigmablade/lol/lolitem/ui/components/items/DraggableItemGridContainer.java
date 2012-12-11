package net.enigmablade.lol.lolitem.ui.components.items;

import java.awt.*;
import java.awt.event.*;
import net.enigmablade.lol.lolitem.ui.models.*;


public class DraggableItemGridContainer extends	DraggableItemContainer
{
	public DraggableItemGridContainer(DraggableItemContainerModel m, ActionListener list, ActionListener returnList)
	{
		super(m, list, returnList);
		
		setLayout(new FlowLayout(FlowLayout.CENTER, itemGap, itemGap));
	}
	
	@Override
	protected void addComponent(Component c)
	{
		((DraggableItem)c).setRenderMode(DraggableItem.RenderMode.GRID);
		super.addComponent(c);
	}
	
	@Override
	protected void updateSize()
	{
		((FlowLayout)getLayout()).setHgap(itemGap);
		((FlowLayout)getLayout()).setVgap(itemGap);
		
		int width = getParent() != null ? getParent().getWidth() : 0;
		if(width > 0)
		{
			int inRow = (width-itemGap-(width%itemGap))/(itemSize+itemGap);
			int size = model.getSize();
			int numFullRows = size/inRow;
			int modRows = size%inRow;
			setPreferredSize(new Dimension(0, ((numFullRows+(modRows > 0 ? 1 : 0))*(itemSize+itemGap))+itemGap));
		}
	}
}
