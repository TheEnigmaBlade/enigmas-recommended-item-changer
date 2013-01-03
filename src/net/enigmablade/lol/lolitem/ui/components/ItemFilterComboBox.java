package net.enigmablade.lol.lolitem.ui.components;

import java.awt.*;
import javax.swing.*;

import net.enigmablade.lol.lollib.ui.pretty.*;
import net.enigmablade.lol.lolitem.ui.renderers.*;

public class ItemFilterComboBox extends PrettyComboBox<String>
{
	private ItemFilterListCellRenderer renderer;
	
	public ItemFilterComboBox()
	{
		super(null, NORTH);
		
		setRenderer(renderer = new ItemFilterListCellRenderer());
		
		setModel(new DefaultComboBoxModel<String>(new String[]{"Name"}));
		
		JPanel widthComponent = new JPanel();
		widthComponent.setPreferredSize(new Dimension(200, 0));
		setComponent(widthComponent);
	}
	
	public void reloadText()
	{
		renderer.reloadText();
	}
}
