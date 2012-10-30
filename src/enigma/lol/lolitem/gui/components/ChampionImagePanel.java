package enigma.lol.lolitem.gui.components;

import java.awt.*;
import javax.swing.*;

import enigma.paradoxion.ui.*;
import enigma.paradoxion.util.*;

import enigma.lol.lollib.data.*;
import enigma.lol.lollib.io.*;

public class ChampionImagePanel extends JPanel
{
	private int size;
	
	private Image championImage;
	
	public ChampionImagePanel(Champion champion)
	{
		this(champion, 80);
	}
	
	public ChampionImagePanel(Champion champion, int s)
	{
		size = s;
		setChampion(champion);
		setBackground(GUIUtil.adjustColor(getBackground(), -25));
		setPreferredSize(new Dimension(size, size));
		setSize(new Dimension(size, size));
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(championImage, 0, 0, this);
	}
	
	public void setChampion(Champion champion)
	{
		if(champion != null)
			championImage = GamePathUtil.getChampionImage(champion.getKey());
		if(champion == null || championImage == null)
			championImage = ResourceLoader.getImage("null.png");
		championImage = championImage.getScaledInstance(size, size, Image.SCALE_SMOOTH);
		
		repaint();
	}
}
