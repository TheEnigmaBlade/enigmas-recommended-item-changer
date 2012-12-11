package net.enigmablade.lol.lolitem.ui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.*;

public class UIUtil
{
	public static final Color BACKGROUND = new Color(18, 19, 18);
	public static final Color COMPONENT_BASE = new Color(21, 33, 34);
	
	public static final Color COMPONENT_BORDER = adjust(COMPONENT_BASE, 5);
	public static final Color BORDER = new Color(174, 151, 110);
	
	public static final Color FOREGROUND = new Color(137, 194, 194);
	public static final Color DISABLED_FOREGROUND = scale(FOREGROUND, 0.3f);
	public static final Color ERROR_FOREGROUND = adjust(new Color(194, 137, 137), -50);
	
	//LAF Modification
	
	public static void setUIFont(Font f)
	{
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements())
		{
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if(value != null && value instanceof FontUIResource)
				UIManager.put(key, new FontUIResource(f.deriveFont((float)((FontUIResource)value).getSize())));
		}
	}
	
	public static void setUIForeground(Color c)
	{
		setColorDefault("foreground", c);
		setColorDefault("messageForeground", c);
	}
	
	public static void setUIDisabledForeground(Color c)
	{
		setColorDefault("disabledForeground", c);
	}
	
	public static void setUIBackground(Color c)
	{
		setColorDefault("background", c);
	}
	
	public static void setUISelectionBackground(Color c)
	{
		setColorDefault("selectionBackground", c);
	}
	
	private static void setColorDefault(String search, Color c)
	{
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while(keys.hasMoreElements())
		{
			Object key = keys.nextElement();
			if(key != null)
			{
				Object value = UIManager.get(key);
				if(value != null && value instanceof ColorUIResource && key != null && key instanceof String && ((String)key).endsWith("."+search))
					UIManager.put(key, new ColorUIResource(c));
			}
		}
	}
	
	//Color tools
	
	public static Color addAlpha(Color c, int a)
	{
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
	}
	
	public static Color adjust(Color c, int amt)
	{
		return new Color(cap(c.getRed()+amt), cap(c.getGreen()+amt), cap(c.getBlue()+amt), c.getAlpha());
	}
	
	public static Color scale(Color c, float scale)
	{
		return new Color(cap((int)(c.getRed()*scale)), cap((int)(c.getGreen()*scale)), cap((int)(c.getBlue()*scale)), c.getAlpha());
	}
	
	private static int cap(int n)
	{
		return n < 0 ? 0 : n > 255 ? 255 : n;
	}
}
