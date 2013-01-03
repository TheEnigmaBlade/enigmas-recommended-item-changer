package net.enigmablade.lol.lolitem.ui.renderers;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import net.enigmablade.paradoxion.ui.renderers.*;

public class ItemFilterListCellRenderer extends AbstractStripedListCellRenderer<String>
{
	private JPanel panel;
	
	public ItemFilterListCellRenderer()
	{
		setBorder(new EmptyBorder(2, 2, 2, 2));
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		panel.setOpaque(false);
		
		panel.add(this, BorderLayout.CENTER);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		if(value != null)
		{
			if(index == -1)
			{
				setText("Sort: "+value);
				setHorizontalAlignment(SwingConstants.CENTER);
			}
			else
			{
				setText(value);
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
