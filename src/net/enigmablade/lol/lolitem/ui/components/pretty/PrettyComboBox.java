package net.enigmablade.lol.lolitem.ui.components.pretty;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import net.enigmablade.lol.lolitem.ui.*;
import org.jdesktop.swingx.painter.*;
import net.enigmablade.paradoxion.ui.components.*;
import net.enigmablade.paradoxion.util.*;


public class PrettyComboBox<T> extends ComponentComboBox<T>
{
	private PrettyComponentFader fader;
	
	public PrettyComboBox(Component c, String pos)
	{
		super(c, pos);
		
		setOpaque(false);
		setBorder(null);
		
		fader = new PrettyComponentFader(this);
		
		setEditor(new PrettyComboBoxEditor());
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(UIUtil.BACKGROUND);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		super.paintComponent(g);
		if(isEnabled() && fader.isHovered())
		{
			g.setColor(fader.getOverlay());
			g.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 4, 4);
		}
	}
	
	protected class PrettyComboBoxUI extends BasicComboBoxComponentUI
	{
		private GlossPainter shader;
		
		public PrettyComboBoxUI()
		{
			setOpaque(false);
			
			shader = new GlossPainter();
			shader.setPaint(UIUtil.addAlpha(Color.white, 10));
			shader.setAntialiasing(true);
		}
		
		@Override
		public void paint(Graphics g, JComponent c)
		{
			g.setColor(UIUtil.COMPONENT_BASE);
			g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 4, 4);
			g.setColor(UIUtil.COMPONENT_BORDER);
			g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 4, 4);
			
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Shape oldClip = g.getClip();
			g.setClip(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 3, 3));
			
			if(isEnabled())
				shader.paint((Graphics2D)g, PrettyComboBox.this, getWidth(), getHeight());
			
			g.setClip(oldClip);
			
			hasFocus = comboBox.hasFocus();
			paintCurrentValue(g, rectangleForCurrentValue(), hasFocus);
		}
		
		@Override
		public JButton createArrowButton()
		{
			JButton button = new JButton();
			button.setUI(new ArrowUI());
			button.setOpaque(false);
			fader.addComponent(button);
			return button;
		}
		
		private class ArrowUI extends BasicButtonUI
		{
			private Image arrow;
			
			public ArrowUI()
			{
				arrow = ResourceLoader.getImage("ui/combo-arrow.png");
			}
			
			public void paint(Graphics g, JComponent c)
			{
				AbstractButton button = (AbstractButton)c;
				
				int offset = -1;
				if(button.getModel().isPressed())
					offset += 1;
				
				g.drawImage(arrow, c.getWidth()/2 - arrow.getWidth(null)/2, c.getHeight()/2 - arrow.getHeight(null)/2 + offset, null);
			}
		}
	}
	
	protected class PrettyComboBoxEditor extends BasicComboBoxEditor
	{
		@Override
		protected JTextField createEditorComponent()
		{
			JTextField field =  new PrettyComboBoxEditorTextField();
			return field;
		}
		
		private class PrettyComboBoxEditorTextField extends PrettyTextField
		{
			public PrettyComboBoxEditorTextField()
			{
				setBorder(new EmptyBorder(4, 4, 4, 4));
				setUI(new PrettyComboBoxEditorTextFieldUI());
				
				setCaretColor(getForeground());
			}
			
			private class PrettyComboBoxEditorTextFieldUI extends BasicTextFieldUI
			{
				@Override
				protected void paintBackground(Graphics g)
				{
					g.setColor(UIUtil.BACKGROUND);
					g.fillRect(0, 0, getWidth(), getHeight());
					
					g.setColor(UIUtil.COMPONENT_BASE);
					g.fillRoundRect(0, 0, getWidth(), getHeight()-1, 4, 4);
					g.setColor(UIUtil.COMPONENT_BORDER);
					g.drawRoundRect(0, 0, getWidth(), getHeight()-1, 4, 4);
					
					g.setColor(getBackground());
					g.fillRect(2, 2, getWidth(), getHeight()-4);
				}
			}
		}
	}
	
	@Override
	public void setComponent(Component c)
	{
		component = c;
		if(c != null)
		{
			setupListeners(component);
			setUI(new PrettyComboBoxUI());
		}
	}
}
