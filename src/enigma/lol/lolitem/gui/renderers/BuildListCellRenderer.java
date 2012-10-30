package enigma.lol.lolitem.gui.renderers;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import enigma.paradoxion.ui.renderers.*;

public class BuildListCellRenderer extends AbstractStripedListCellRenderer<String>
{
	private JLabel textLabel;
	
	private String noBuildsText = "No existing builds", selectBuildText = "Select build";
		
	public BuildListCellRenderer()
	{
		setLayout(new BorderLayout(2, 0));
		setBorder(new EmptyBorder(2, 2, 2, 2));
			
		textLabel = new JLabel();
		textLabel.setFont(getFont());
		add(textLabel, BorderLayout.CENTER);
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
			textLabel.setText(key.equals("empty") ? noBuildsText : key.equals("select") ? selectBuildText : "");
			textLabel.setEnabled(false);
		}
		else
		{
			textLabel.setText(build);
			textLabel.setEnabled(true);
		}
		
			//Layout
		if(index != -1)
		{
			((BorderLayout)getLayout()).setHgap(4);
			textLabel.setHorizontalAlignment(SwingConstants.LEFT);
		}
		else
		{
			((BorderLayout)getLayout()).setHgap(0);
			textLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
