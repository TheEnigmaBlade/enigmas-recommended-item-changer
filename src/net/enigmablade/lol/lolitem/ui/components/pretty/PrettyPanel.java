package net.enigmablade.lol.lolitem.ui.components.pretty;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class PrettyPanel extends JPanel
{
	private NinePatchRenderer renderer;
	
	public PrettyPanel()
	{
		renderer = new NinePatchRenderer("panel");
		
		setBorder(new EmptyBorder(9, 9, 9, 9));
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		renderer.draw(g, getWidth(), getHeight());
	}
}
