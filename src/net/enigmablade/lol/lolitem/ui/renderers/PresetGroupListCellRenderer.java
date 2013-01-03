package net.enigmablade.lol.lolitem.ui.renderers;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import net.enigmablade.paradoxion.ui.renderers.*;
import net.enigmablade.lol.lolitem.data.*;

public class PresetGroupListCellRenderer extends AbstractStripedListCellRenderer<ItemGroup>
{
	@Override
	public Component getListCellRendererComponent(JList<? extends ItemGroup> list, ItemGroup value, int index, boolean isSelected, boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		setText(value.getName());
		setBorder(new EmptyBorder(2, 4, 2, 0));
		
		return this;
	}
}
