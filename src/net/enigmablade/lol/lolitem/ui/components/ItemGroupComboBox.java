package net.enigmablade.lol.lolitem.ui.components;

import java.awt.*;
import javax.swing.*;

import net.enigmablade.lol.lollib.ui.pretty.*;

public class ItemGroupComboBox extends PrettyComboBox<String>
{
	public ItemGroupComboBox()
	{
		super(null, NORTH);
		
		JPanel widthComponent = new JPanel();
		widthComponent.setPreferredSize(new Dimension(200, 0));
		setComponent(widthComponent);
	}
}
