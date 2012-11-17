package enigma.lol.lolitem.gui.components;

import java.awt.*;
import javax.swing.*;

import enigma.paradoxion.ui.components.*;
import enigma.lol.lollib.data.*;

import enigma.lol.lolitem.gui.renderers.*;

public class GameModeComboBox extends ComponentComboBox<GameMode>
{
	private GameModeListCellRenderer renderer;
	
	public GameModeComboBox()
	{
		super(null, NORTH);
		
		setRenderer(renderer = new GameModeListCellRenderer());
		
		//FIXME: better way to fit the popup so it's larger than the combo box
		JPanel widthComponent = new JPanel();
		widthComponent.setPreferredSize(new Dimension(230, 0));
		setComponent(widthComponent);
	}

	public void reloadText()
	{
		renderer.reloadText();
	}
}
