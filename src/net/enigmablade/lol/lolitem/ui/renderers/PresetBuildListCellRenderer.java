package net.enigmablade.lol.lolitem.ui.renderers;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import net.enigmablade.paradoxion.ui.renderers.*;
import net.enigmablade.lol.lolitem.data.*;

public class PresetBuildListCellRenderer extends AbstractStripedListCellRenderer<ItemBuild>
{
	@Override
	public Component getListCellRendererComponent(JList<? extends ItemBuild> list, ItemBuild value, int index, boolean isSelected, boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		setText(value.getName());
		setBorder(new EmptyBorder(1, 2, 1, 0));
		
		return this;
	}
}
