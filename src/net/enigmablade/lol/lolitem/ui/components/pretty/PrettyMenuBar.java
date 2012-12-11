package net.enigmablade.lol.lolitem.ui.components.pretty;

import java.awt.*;
import javax.swing.*;
import net.enigmablade.lol.lolitem.ui.*;
import org.jdesktop.swingx.painter.*;


public class PrettyMenuBar extends JMenuBar
{
	private GlossPainter shader;
	
	public PrettyMenuBar()
	{
		setUI(null);
		
		shader = new GlossPainter();
		shader.setPaint(UIUtil.addAlpha(Color.white, 10));
		shader.setAntialiasing(true);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(UIUtil.COMPONENT_BASE);
		g.fillRect(0, 0, getWidth(), getHeight());
		shader.paint((Graphics2D)g, this, getWidth(), getHeight());
	}
	
	@Override
	public void paintBorder(Graphics g)
	{
		if(isBorderPainted())
		{
			g.setColor(UIUtil.COMPONENT_BASE.brighter());
			g.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
		}
	}
}
