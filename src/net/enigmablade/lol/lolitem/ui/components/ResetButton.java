package net.enigmablade.lol.lolitem.ui.components;

import java.awt.*;

import net.enigmablade.lol.lollib.ui.*;
import net.enigmablade.lol.lollib.ui.pretty.*;

public class ResetButton extends PrettyButton
{
	private static String resetButtonText = "Reset";
	private static Color foreground = new Color(194, 137, 137);
	
	public ResetButton()
	{
		super(resetButtonText);
	}
	
	@Override
	public void setEnabled(boolean enable)
	{
		super.setEnabled(enable);
		
		if(enable)
			setForeground(foreground);
		else
			setForeground(UIUtil.adjust(foreground, 10));
	}
}
