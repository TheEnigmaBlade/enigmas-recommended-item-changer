package net.enigmablade.lol.lolitem.ui.tooltip;

import java.awt.*;
import javax.swing.*;
import net.enigmablade.paradoxion.util.*;
import net.enigmablade.lol.lollib.io.*;


public class ItemTooltipImagePanel extends JPanel
{
	private Image image;
	private int size;
	
	private int[] cornerX1, cornerX2, cornerX3, cornerX4;
	private int[] cornerY1, cornerY2, cornerY3, cornerY4;
	
	public ItemTooltipImagePanel(int s)
	{
		size = s;
		initCorners();
		
		setImage("null");
		
		setPreferredSize(new Dimension(size, size));
	}
	
	private void initCorners()
	{
		int sizeX = (int)Math.ceil(size/12.0);
		cornerX1 = new int[]{0, sizeX, 0};
		cornerX2 = new int[]{size, size-sizeX, size};
		cornerX3 = new int[]{size, size-sizeX, size};
		cornerX4 = new int[]{0, sizeX, 0};
		int sizeY = (int)Math.ceil(size/12.0);
		cornerY1 = new int[]{0, 0, sizeY};
		cornerY2 = new int[]{0, 0, sizeY};
		cornerY3 = new int[]{size-1, size-1, size-sizeY-1};
		cornerY4 = new int[]{size-1, size-1, size-sizeY-1};
	}
	
	public void setImage(String name)
	{
		image = null;
		if(name != null)
			image = GamePathUtil.getItemImage(name);
		if(name != null && image == null)
			image = ResourceLoader.getImage("null.png");
		
		if(image != null)
			image = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
		
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.black);
		g.fillRect(0, 0, size, size);
		
		g.drawImage(image, 0, 0, this);
		
		g.setColor(Color.black);
		g.fillPolygon(cornerX1, cornerY1, 3);
		g.fillPolygon(cornerX2, cornerY2, 3);
		g.fillPolygon(cornerX3, cornerY3, 3);
		g.fillPolygon(cornerX4, cornerY4, 3);
	}
}
