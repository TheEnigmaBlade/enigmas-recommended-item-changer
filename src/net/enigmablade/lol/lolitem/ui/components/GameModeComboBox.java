package net.enigmablade.lol.lolitem.ui.components;

import java.awt.*;
import javax.swing.*;

import net.enigmablade.lol.lolitem.ui.renderers.*;

import net.enigmablade.lol.lollib.ui.pretty.*;
import net.enigmablade.lol.lollib.data.*;

public class GameModeComboBox extends PrettyComboBox<GameMode>
{
	private GameModeListCellRenderer renderer;
	
	public GameModeComboBox()
	{
		super(null, NORTH);
		
		setRenderer(renderer = new GameModeListCellRenderer());
		
		JPanel widthComponent = new JPanel();
		widthComponent.setPreferredSize(new Dimension(230, 0));
		setComponent(widthComponent);
	}

	public void reloadText()
	{
		renderer.reloadText();
	}
}
