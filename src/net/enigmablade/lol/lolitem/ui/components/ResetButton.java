package net.enigmablade.lol.lolitem.ui.components;

import java.awt.*;

import net.enigmablade.lol.lollib.ui.pretty.*;

public class ResetButton extends PrettyButton
{
	private static String resetButtonText = "Reset";
	
	public ResetButton()
	{
		super(resetButtonText);
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
