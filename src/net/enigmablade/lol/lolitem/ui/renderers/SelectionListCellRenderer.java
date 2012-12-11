package net.enigmablade.lol.lolitem.ui.renderers;

import java.awt.*;
import javax.swing.*;
import net.enigmablade.paradoxion.ui.renderers.*;
import net.enigmablade.paradoxion.util.*;


public class SelectionListCellRenderer<E> extends AbstractStripedListCellRenderer<E>
{
	private JPanel panel;
	
	private int selectedIndex = -1;
	private String selectionText;
	
	public SelectionListCellRenderer(String sT)
	{
		selectionText = sT;
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		setHorizontalAlignment(JLabel.LEADING);
		setVerticalAlignment(JLabel.CENTER);
		panel.add(this, BorderLayout.CENTER);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		boolean selected = index == selectedIndex;
		ImageIcon icon = ResourceLoader.getImageIcon(selected ? "selected.png" : "not_selected.png");
		setIcon(icon);
		setText(value+(selected ? " ("+selectionText+")" : ""));
		
		return this;
	}
	
	public void setSelected(int index)
	{
		selectedIndex = index;
	}
	
	public void setSelectionText(String sT)
	{
		selectionText = sT;
	}
}
