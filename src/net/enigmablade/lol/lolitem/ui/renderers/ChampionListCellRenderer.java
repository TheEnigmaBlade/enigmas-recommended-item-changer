package net.enigmablade.lol.lolitem.ui.renderers;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import net.enigmablade.paradoxion.localization.*;
import net.enigmablade.paradoxion.ui.components.*;
import net.enigmablade.paradoxion.ui.renderers.*;
import net.enigmablade.paradoxion.util.*;
import net.enigmablade.lol.lollib.data.*;
import net.enigmablade.lol.lollib.io.*;



public class ChampionListCellRenderer extends AbstractStripedListCellRenderer<String>
{
	private JPanel panel;
	
	private JLabel imageLabel;
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
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout(2, 0));
		panel.setBorder(new EmptyBorder(2, 2, 2, 2));
		panel.setOpaque(false);
		
		imageLabel = new JLabel();
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(imageLabel, BorderLayout.WEST);
		
		panel.add(this, BorderLayout.CENTER);
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
			setText(selectChampionText);
			//setEnabled(false);
		}
		else
		{
			setText(champion);
			setBorder(new EmptyBorder(0, 2, 0, 0));
			//setEnabled(true);
		}
			
			//Images and layout
		if(index != -1 && isChampion)
		{
			((BorderLayout)panel.getLayout()).setHgap(4);
			
			Icon championIcon = imageCache.get(ChampionDatabase.getChampionKey(champion));
			imageLabel.setIcon(championIcon);
			setHorizontalAlignment(SwingConstants.LEFT);
		}
		else
		{
			((BorderLayout)panel.getLayout()).setHgap(0);
			
			imageLabel.setIcon(null);
			setHorizontalAlignment(SwingConstants.CENTER);
		}
		
		return panel;
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
