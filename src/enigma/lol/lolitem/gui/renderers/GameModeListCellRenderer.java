package enigma.lol.lolitem.gui.renderers;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import enigma.paradoxion.ui.renderers.*;

import enigma.lol.lollib.data.*;

public class GameModeListCellRenderer extends AbstractStripedListCellRenderer<GameMode>
{
	private JPanel panel;
	private JLabel imageLabel;
	
	public GameModeListCellRenderer()
	{
		setBorder(new EmptyBorder(2, 2, 2, 2));
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		panel.setOpaque(false);
		
		imageLabel = new JLabel();
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(imageLabel, BorderLayout.WEST);
		
		panel.add(this, BorderLayout.CENTER);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends GameMode> list, GameMode value, int index, boolean isSelected, boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		if(value != null && value instanceof GameMode)
		{
			GameMode mode = (GameMode)value;
			if(index == -1)
			{
				setText(mode.getTitle()+" Mode");
				setHorizontalAlignment(SwingConstants.CENTER);
			}
			else
			{
				setText(mode.getTitle()+" ("+mode.getDetails()+")");
				setHorizontalAlignment(SwingConstants.LEFT);
			}
		}
		else
		{
			setText("");
			setHorizontalAlignment(SwingConstants.LEFT);
		}
		
		if(list != null)
			setFont(list.getFont());
		
		return panel;
	}
	
	public void reloadText()
	{
		//TODO
	}
}
