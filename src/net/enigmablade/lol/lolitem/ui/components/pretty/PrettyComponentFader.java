package net.enigmablade.lol.lolitem.ui.components.pretty;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class PrettyComponentFader
{
	private LinkedList<Component> components;
	private MouseListener listener;
	private boolean hovered = false;
	
	private Color overlay;
	private float overlayAlpha;
	private float overlayAlphaMax = 25;
	private float overlayAlphaUpRate = 10, overlayAlphaDownRate = 10;
	
	private Timer overlayUpTimer, overlayDownTimer;
	
	public PrettyComponentFader(Component c)
	{
		if(c == null)
			throw new IllegalArgumentException("Component cannot be null");
		components = new LinkedList<Component>();
		components.add(c);
		
		overlay = Color.gray;
		overlayAlpha = 0.0f;
		
		overlayUpTimer = new Timer(75, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				overlayAlpha += overlayAlphaUpRate;
				if(overlayAlpha >= overlayAlphaMax)
				{
					overlayUpTimer.stop();
					overlayAlpha = overlayAlphaMax;
				}
				for(Component c : components)
					c.repaint();
			}
		});
		overlayDownTimer = new Timer(75, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				overlayAlpha -= overlayAlphaDownRate;
				if(overlayAlpha <= 0)
				{
					overlayDownTimer.stop();
					overlayAlpha = 0;
				}
				for(Component c : components)
				c.repaint();
			}
		});
		
		c.addMouseListener(listener = new RolloverListener());
	}
	
	private class RolloverListener extends MouseAdapter
	{
		public void mouseEntered(MouseEvent e)
		{
			if(!SwingUtilities.isLeftMouseButton(e))
			{
				hovered = true;
				
				//overlayAlpha = 0;
				if(overlayDownTimer.isRunning())
					overlayDownTimer.stop();
				overlayUpTimer.start();
			}
		}
		
		public void mouseExited(MouseEvent e)
		{
			hovered = false;
			
			if(overlayUpTimer.isRunning())
				overlayUpTimer.stop();
			overlayDownTimer.start();
		}
	}
	
	public boolean isHovered()
	{
		return hovered;
	}
	
	public Color getOverlay()
	{
		return new Color(overlay.getRed(), overlay.getGreen(), overlay.getBlue(), (int)overlayAlpha);
	}
	
	public void addComponent(Component c)
	{
		c.addMouseListener(listener);
		components.add(c);
	}
	
	public void removeComponent(Component c)
	{
		c.removeMouseListener(listener);
		components.remove(c);
	}
}
