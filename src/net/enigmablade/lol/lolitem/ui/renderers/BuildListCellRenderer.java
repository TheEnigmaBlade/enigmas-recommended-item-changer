package net.enigmablade.lol.lolitem.ui.renderers;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import net.enigmablade.paradoxion.ui.renderers.*;


public class BuildListCellRenderer extends AbstractStripedListCellRenderer<String>
{
	private JPanel panel;
	
	private String noBuildsText = "No existing builds", selectBuildText = "Select build";
		
	public BuildListCellRenderer()
	{
		panel = new JPanel();
		panel.setLayout(new BorderLayout(2, 0));
		panel.setBorder(new EmptyBorder(2, 2, 2, 2));
		
		panel.add(this, BorderLayout.CENTER);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus)
	{
		String build = (String)value;
		
		//Return a blank
		if(build == null || (index != -1 && build.startsWith("---")))
			return getBlankComponent();
		
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
			//Text
		if(build.startsWith("---"))
		{
			String key = build.substring(3);
			setText(key.equals("empty") ? noBuildsText : key.equals("select") ? selectBuildText : "");
			setEnabled(false);
		}
		else
		{
			setText(build);
			setEnabled(true);
		}
		
			//Layout
		if(index != -1)
		{
			((BorderLayout)panel.getLayout()).setHgap(4);
			setHorizontalAlignment(SwingConstants.LEFT);
		}
		else
		{
			((BorderLayout)panel.getLayout()).setHgap(0);
			setHorizontalAlignment(SwingConstants.CENTER);
		}
		
		return this;
	}
	
	private static Component getBlankComponent()
	{
		JPanel blankPanel = new JPanel();
		blankPanel.setPreferredSize(new Dimension(0, 0));
		return blankPanel;
	}
	
	public void reloadText()
	{
		
	}
}
