package net.enigmablade.lol.lolitem.ui.components.pretty;

import java.awt.event.*;
import javax.swing.*;
import net.enigmablade.paradoxion.ui.components.translucent.*;
import net.enigmablade.paradoxion.util.*;


public class PrettyRadioButton extends TranslucentRadioButton
{
	private boolean hovered = false;
	
	public PrettyRadioButton()
	{
		this("");
	}
	
	public PrettyRadioButton(String text)
	{
		super(text, 0);
		
		setFocusPainted(false);
		
		addMouseListener(new RolloverListener());
	}
	
	@Override
	public Icon getIcon()
	{
		return ResourceLoader.getImageIcon("ui/radio-unchecked"+(hovered ? "-hover" : "")+".png");
	}
	
	@Override
	public Icon getSelectedIcon()
	{
		return ResourceLoader.getImageIcon("ui/radio-checked"+(hovered ? "-hover" : "")+".png");
	}
	
	private class RolloverListener extends MouseAdapter
	{
		public void mouseEntered(MouseEvent e)
		{
			if(!SwingUtilities.isLeftMouseButton(e))
			{
				hovered = true;
			}
		}
		
		public void mouseExited(MouseEvent e)
		{
			hovered = false;
		}
	}
}
