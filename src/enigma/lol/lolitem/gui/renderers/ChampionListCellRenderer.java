package enigma.lol.lolitem.gui.renderers;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import enigma.paradoxion.localization.*;
import enigma.paradoxion.ui.components.*;
import enigma.paradoxion.ui.renderers.*;
import enigma.paradoxion.util.*;

import enigma.lol.lollib.data.*;
import enigma.lol.lollib.io.*;

public class ChampionListCellRenderer extends AbstractStripedListCellRenderer<String>
{
	private JLabel imageLabel, textLabel;
	private boolean showSelected;
	
	private String selectChampionText = "Select Champion";
	
	private static HashMap<String, Icon> imageCache;
	
	static
	{
		imageCache = new HashMap<String, Icon>();
	}
	
	public static void loadImageCache()
	{
		Set<String> keys = ChampionDatabase.getChampions();
		for(String key : keys)
			loadImage(key);
	}
	
	public ChampionListCellRenderer(boolean showSelected)
	{
		this.showSelected = showSelected;
		
		setLayout(new BorderLayout(2, 0));
		setBorder(new EmptyBorder(2, 2, 2, 2));
		
		imageLabel = new JLabel();
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(imageLabel, BorderLayout.WEST);
		
		textLabel = new JLabel();
		add(textLabel, BorderLayout.CENTER);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus)
	{
		String champion = value;
		
		//Return a blank
		if(champion == null)
			return getBlankComponent();
		
		//Return a separator
		if(index != -1 && champion.startsWith("---"))
			return getSeparator(champion);
		
		//Return a Champion
		boolean isChampion = champion.length() > 0 && Character.isLetterOrDigit(champion.charAt(0));
		
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
			//Text
		if(index == -1 && (!showSelected || champion.startsWith("---")))
		{
			textLabel.setText(selectChampionText);
			textLabel.setEnabled(false);
		}
		else
		{
			textLabel.setText(champion);
			textLabel.setEnabled(true);
		}
		
		textLabel.setFont(list.getFont());
		
			//Images and layout
		if(index != -1 && isChampion)
		{
			((BorderLayout)getLayout()).setHgap(4);
			
			Icon championIcon = imageCache.get(ChampionDatabase.getChampionKey(champion));
			imageLabel.setIcon(championIcon);
			textLabel.setHorizontalAlignment(SwingConstants.LEFT);
		}
		else
		{
			((BorderLayout)getLayout()).setHgap(0);
			
			imageLabel.setIcon(null);
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
	
	private static Component getSeparator(String s)
	{
		String text = s.length() > 3 ? s.substring(3, s.length()) : null;
		if(text != null)
			return new TextSeparator(text);
		return new JSeparator();
	}
	
	private static Icon loadImage(String championKey)
	{
		Image image = GamePathUtil.getImage("champions/"+championKey+"_Square_0.png");
		if(image == null)
			image = ResourceLoader.getImage("null.png");
		image = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
		Icon icon = new ImageIcon(image);
		imageCache.put(championKey, icon);
		return icon;
	}
	
	public void reloadText()
	{
		selectChampionText = LocaleDatabase.getString("main.champion.combobox.select");
	}
}
