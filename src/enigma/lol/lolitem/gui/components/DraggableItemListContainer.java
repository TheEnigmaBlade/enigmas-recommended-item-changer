package enigma.lol.lolitem.gui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

import enigma.lol.lolitem.gui.models.*;

public class DraggableItemListContainer extends DraggableItemContainer
{
	public DraggableItemListContainer(DraggableItemContainerModel m, ActionListener list)
	{
		super(m, list);
		
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
