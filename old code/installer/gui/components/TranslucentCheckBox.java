package enigma.installer.gui.components;

import java.awt.*;
import javax.swing.*;

public class TranslucentCheckBox extends JCheckBox
{
	private TranslucentBackground bg;
	
	public TranslucentCheckBox(int a)
	{
		bg = new TranslucentBackground(this, a);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		bg.paintBackground(g);
		super.paintComponent(g);
	}
}
