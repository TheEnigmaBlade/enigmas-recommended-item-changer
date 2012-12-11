package enigma.installer.gui.components;

import java.awt.*;

import javax.swing.*;

public class TranslucentPanel extends JPanel
{
	private TranslucentBackground bg;
	
	public TranslucentPanel(int a)
	{
		bg = new TranslucentBackground(this, a);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		bg.paintBackground(g);
	}
}
