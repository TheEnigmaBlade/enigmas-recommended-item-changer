package enigma.lol.lolitem.gui.components;

import java.awt.*;
import javax.swing.*;

public class ResetButton extends JButton
{
	public ResetButton()
	{
		super();
	}
	
	public ResetButton(String text)
	{
		super(text);
	}
	
	@Override
	public void setEnabled(boolean enable)
	{
		super.setEnabled(enable);
		
		if(enable)
			setForeground(new Color(140, 0, 0));
		else
			setForeground(new Color(180, 140, 140));
	}
}
