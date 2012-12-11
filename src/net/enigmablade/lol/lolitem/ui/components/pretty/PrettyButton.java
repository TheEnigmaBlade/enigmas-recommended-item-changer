package net.enigmablade.lol.lolitem.ui.components.pretty;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import net.enigmablade.lol.lolitem.ui.*;
import org.jdesktop.swingx.painter.*;


public class PrettyButton extends JButton
{
	private PrettyComponentFader fader;
	private GlossPainter shader, pressedShader;
	
	public PrettyButton()
	{
		this("");
	}
	
	public PrettyButton(String text)
	{
		super(text);
		
		setUI(new PrettyButtonUI());
		setOpaque(false);
		
		fader = new PrettyComponentFader(this);
		
		shader = new GlossPainter();
		shader.setPaint(UIUtil.addAlpha(Color.white, 10));
		shader.setAntialiasing(true);
		
		pressedShader = new GlossPainter(GlossPainter.GlossPosition.BOTTOM);
		pressedShader.setPaint(UIUtil.addAlpha(Color.white, 8));
		pressedShader.setAntialiasing(true);
	}
	
	public class PrettyButtonUI extends BasicButtonUI
	{
		@Override
		public void paint(Graphics g, JComponent c)
		{
			g.setColor(UIUtil.COMPONENT_BASE);
			g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 4, 4);
			g.setColor(UIUtil.COMPONENT_BORDER);
			g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 4, 4);
			
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Shape oldClip = g.getClip();
			g.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 3, 3));
			
			if(model.isEnabled())
			{
				if(model.isArmed() || model.isPressed())
					pressedShader.paint((Graphics2D)g, null, getWidth(), getHeight());
				else
					shader.paint((Graphics2D)g, null, getWidth(), getHeight());
				
				if(fader.isHovered())
				{
					g.setColor(fader.getOverlay());
					g.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 3, 3);
				}
			}
			
			g.setClip(oldClip);
			
			super.paint(g, c);
		}
		
		@Override
		protected void paintText(Graphics g, AbstractButton c, Rectangle textRect, String text)
		{
			AbstractButton b = (AbstractButton)c;                       
			ButtonModel model = b.getModel();
			FontMetrics fm = g.getFontMetrics();
			
			if(model.isEnabled())
			{
				g.setColor(b.getForeground());
				g.drawString(text, textRect.x + getTextShiftOffset(), textRect.y + fm.getAscent() + getTextShiftOffset());
			}
			else
			{
				g.setColor(UIUtil.DISABLED_FOREGROUND.darker().darker());
				g.drawString(text, textRect.x, textRect.y + fm.getAscent());
				g.setColor(UIUtil.DISABLED_FOREGROUND);
				g.drawString(text, textRect.x - 1, textRect.y + fm.getAscent() - 1);
			}
		}
	}
}
