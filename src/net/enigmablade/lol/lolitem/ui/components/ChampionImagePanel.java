package net.enigmablade.lol.lolitem.ui.components;

import java.awt.*;
import javax.swing.*;
import net.enigmablade.paradoxion.ui.*;
import net.enigmablade.paradoxion.util.*;
import net.enigmablade.lol.lollib.data.*;
import net.enigmablade.lol.lollib.io.*;



public class ChampionImagePanel extends JPanel
{
	private int size;
	
	private Image championImage;
	
	public ChampionImagePanel(Champion champion)
	{
		this(champion, 84);
	}
	
	public ChampionImagePanel(Champion champion, int s)
	{
		size = s;
		setChampion(champion);
		setBackground(GUIUtil.adjustColor(getBackground(), -25));
		setPreferredSize(new Dimension(size, size));
		setSize(new Dimension(size, size));
		setMinimumSize(new Dimension(size, size));
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
			championImage = ResourceLoader.getImage("asuna.png");
		championImage = championImage.getScaledInstance(size, size, Image.SCALE_SMOOTH);
		
		repaint();
	}
}
