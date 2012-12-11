package net.enigmablade.lol.lolitem.ui.renderers;

import java.awt.*;
import javax.swing.*;
import net.enigmablade.paradoxion.ui.components.*;


public class ItemSetListCellRenderer extends SelectionListCellRenderer<String>
{
	public ItemSetListCellRenderer(String sT)
	{
		super(sT);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus)
	{
		String text = (String)value;
		
		//Return a separator
		if(index != -1 && text.startsWith("---"))
			return getSeparator(text);
		
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		return this;
	}
	
	private static Component getSeparator(String s)
	{
		String text = s.length() > 3 ? s.substring(3, s.length()) : null;
		if(text != null)
			return new TextSeparator(text);
		return new JSeparator();
	}
}
