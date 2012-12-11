package enigma.installer.gui.components;

import java.awt.*;
import javax.swing.*;
import enigma.installer.gui.*;


public class TranslucentBackground
{
	private JComponent component;
	private int alpha;
	
	public TranslucentBackground(JComponent c, int a)
	{
		component = c;
		alpha = a;
		
		component.setOpaque(false);
	}
	
	public void paintBackground(Graphics g)
	{
		g.setColor(GUIUtil.addAlpha(component.getBackground(), alpha));
		g.fillRect(0, 0, component.getWidth(), component.getHeight());
	}
}
