package enigma.lol.lolitem.gui.renderers;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import enigma.paradoxion.ui.renderers.*;

import enigma.lol.lollib.data.*;

@SuppressWarnings("rawtypes")
public class GameModeListCellRenderer extends AbstractStripedListCellRenderer
{
	private JLabel imageLabel, textLabel;
	
	public GameModeListCellRenderer()
	{
		setLayout(new BorderLayout(0, 0));
		setBorder(new EmptyBorder(2, 2, 2, 2));
		
		imageLabel = new JLabel();
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(imageLabel, BorderLayout.WEST);
		
		textLabel = new JLabel();
		add(textLabel, BorderLayout.CENTER);
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		if(value != null && value instanceof GameMode)
		{
			GameMode mode = (GameMode)value;
			if(index == -1)
			{
				textLabel.setText(mode.getTitle()+" Mode");
				textLabel.setHorizontalAlignment(SwingConstants.CENTER);
			}
			else
			{
				textLabel.setText(mode.getTitle()+" ("+mode.getDetails()+")");
				textLabel.setHorizontalAlignment(SwingConstants.LEFT);
			}
		}
		else
		{
			textLabel.setText("");
			textLabel.setHorizontalAlignment(SwingConstants.LEFT);
		}
		
		if(list != null)
			textLabel.setFont(list.getFont());
		
		return this;
	}
	
	public void reloadText()
	{
		//TODO
	}
}
