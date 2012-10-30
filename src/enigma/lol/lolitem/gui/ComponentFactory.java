package enigma.lol.lolitem.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import enigma.paradoxion.ui.*;
import enigma.paradoxion.ui.components.translucent.*;

public class ComponentFactory
{
	public static TranslucentCheckBox createFilterCheckBox(String text, String command, int alpha, ItemListener list)
	{
		TranslucentCheckBox checkBox = new TranslucentCheckBox(alpha);
		checkBox.setText(text);
		checkBox.setBackground(new Color(150, 150, 150));
		checkBox.setOpaque(false);
		checkBox.setFocusPainted(false);
		checkBox.setActionCommand(command);
		checkBox.addItemListener(list);
		return checkBox;
	}
	
	public static JLabel createShopLabel(String text)
	{
		JLabel label = new JLabel(text);
		label.setForeground(GUIUtil.addAlpha(Color.DARK_GRAY, 60));
		label.setFont(label.getFont().deriveFont(label.getFont().getStyle() | Font.BOLD, label.getFont().getSize()+4f));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		return label;
	}
	
	public static JButton createShopButton(String text, String command, int w, int h, ActionListener list)
	{
		JButton button = new JButton(text);
		button.setFont(button.getFont().deriveFont(13.0f));
		button.setPreferredSize(new Dimension(w, h));
		button.setMaximumSize(new Dimension(w, h));
		button.setOpaque(false);
		button.setFocusPainted(false);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setActionCommand(command);
		button.addActionListener(list);
		return button;
	}
	
	public static JButton createShopBackButton(String key)
	{
		JButton button = new JButton("Back");
		button.setOpaque(false);
		button.setFocusPainted(false);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setActionCommand("back-"+key);
		return button;
	}
}
