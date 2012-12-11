package net.enigmablade.lol.lolitem.ui.components.pretty;

import javax.swing.*;
import javax.swing.border.*;
import net.enigmablade.lol.lolitem.ui.*;


public class PrettyTextField extends JTextField
{
	public PrettyTextField()
	{
		this("");
	}
	
	public PrettyTextField(String text)
	{
		setBorder(new CompoundBorder(new LineBorder(UIUtil.BORDER), new EmptyBorder(2, 2, 2, 2)));
		setBackground(UIUtil.BACKGROUND);
		setCaretColor(getForeground());
	}
}
