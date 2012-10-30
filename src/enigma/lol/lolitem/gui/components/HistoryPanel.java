package enigma.lol.lolitem.gui.components;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class HistoryPanel extends JPanel
{
	private ActionListener buttonListener;
	
	private Stack<String> cardStack;
	private String oldCard;
	private JButton lastButton;
	
	public HistoryPanel(ActionListener list)
	{
		buttonListener = list;
		
		cardStack = new Stack<String>();
		
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		layout.setVgap(2);
		layout.setHgap(2);
		setLayout(layout);
	}
	
	public void pushCard(String key, String name)
	{
		if(lastButton != null)
			lastButton.setEnabled(true);
		
		add(lastButton = createButton(key, name));
		refresh();
		
		cardStack.push(key);
	}
	
	public String popCard()
	{
		if(cardStack.size() > 0)
		{
			remove(cardStack.size()-1);
			refresh();
			
			oldCard = cardStack.pop();
			if(cardStack.size() > 0)
			{
				(lastButton = (JButton)getComponent(cardStack.size()-1)).setEnabled(false);
				return cardStack.peek();
			}
		}
		return null;
	}
	
	private JButton createButton(String key, String text)
	{
		JButton button = new JButton(text);
		button.setOpaque(false);
		button.setBorder(new EmptyBorder(4, 4, 4, 4));
		button.addActionListener(buttonListener);
		button.setActionCommand("back-"+key);
		button.setEnabled(false);
		return button;
	}
	
	private void refresh()
	{
		revalidate();
		repaint();
	}
	
	public JButton getFirstButton()
	{
		for(int n = 0; n < getComponentCount(); n++)
			if(getComponent(n) instanceof JButton)
				return (JButton)getComponent(n);
		return null;
	}
	
	public String getLast()
	{
		return oldCard;
	}
	
	public String getCurrent()
	{
		return cardStack.peek();
	}

	public void reset()
	{
		while(cardStack.size() > 1)
			popCard();
	}
}
