package enigma.lol.lolitem.gui.renderers;

import java.awt.*;
import javax.swing.*;

import enigma.paradoxion.ui.renderers.*;
import enigma.paradoxion.util.*;

public class SelectionListCellRenderer<E> extends AbstractStripedListCellRenderer<E>
{
	private JLabel label;
	
	private int selectedIndex = -1;
	private String selectionText;
	
	public SelectionListCellRenderer(String sT)
	{
		selectionText = sT;
		
		setLayout(new BorderLayout());
		
		label = new JLabel();
		label.setHorizontalAlignment(JLabel.LEADING);
		label.setVerticalAlignment(JLabel.CENTER);
		add(label, BorderLayout.CENTER);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus)
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		boolean selected = index == selectedIndex;
		ImageIcon icon = ResourceLoader.getImageIcon(selected ? "selected.png" : "not_selected.png");
		label.setIcon(icon);
		label.setText(value+(selected ? " ("+selectionText+")" : ""));
		setFont(list.getFont());
		
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
