package net.enigmablade.lol.lolitem.ui.components.pretty;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import net.enigmablade.lol.lolitem.ui.*;
import org.jdesktop.swingx.painter.*;

public class PrettyScrollBar extends JScrollBar
{
	public PrettyScrollBar()
	{
		this(JScrollBar.VERTICAL);
	}
	
	public PrettyScrollBar(int orientation)
	{
		super(orientation);
		
		setUI(new PrettyScrollBarUI());
	}
	
	private class PrettyScrollBarUI extends BasicScrollBarUI
	{
		//private NinePatchRenderer renderer;
		private GlossPainter shader;
		
		public PrettyScrollBarUI()
		{
			//renderer = new NinePatchRenderer("button");
			shader = new GlossPainter(GlossPainter.GlossPosition.BOTTOM);
			shader.setPaint(UIUtil.addAlpha(Color.black, 15));
			shader.setAntialiasing(true);
		}
		
		@Override
		protected void configureScrollBarColors() 
		{
			super.configureScrollBarColors();
			trackColor = UIUtil.adjust(UIUtil.BACKGROUND, 4);
			trackHighlightColor = UIUtil.adjust(UIUtil.BACKGROUND, -4);
		}
		
		protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)  
	    {
			g.translate(thumbBounds.x, thumbBounds.y);
			//renderer.draw(g, thumbBounds.width, thumbBounds.height);
			
			g.setColor(UIUtil.COMPONENT_BASE);
			g.fillRoundRect(0, 0, thumbBounds.width-1, thumbBounds.height-1, 4, 4);
			g.setColor(UIUtil.COMPONENT_BORDER);
			g.drawRoundRect(0, 0, thumbBounds.width-1, thumbBounds.height-1, 4, 4);
			
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Shape oldClip = g.getClip();
			g.setClip(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 3, 3));
			
			g.translate(-thumbBounds.width/2, 0);
			
			shader.paint((Graphics2D)g, PrettyScrollBar.this, thumbBounds.width*2, thumbBounds.height);
			
			g.setClip(oldClip);
			
			g.translate(-thumbBounds.x+thumbBounds.width/2, -thumbBounds.y);
	    }
		
		@Override
		public JButton createIncreaseButton(int orientation)
		{
			return new PrettyArrowButton(orientation, 
					UIManager.getColor("ScrollBar.thumbShadow"),
					UIManager.getColor("ScrollBar.thumbDarkShadow"),
					UIManager.getColor("ScrollBar.thumbHighlight"));
		}
		
		@Override
		public JButton createDecreaseButton(int orientation)
		{
			return new PrettyArrowButton(orientation, 
					UIManager.getColor("ScrollBar.thumbShadow"),
					UIManager.getColor("ScrollBar.thumbDarkShadow"),
					UIManager.getColor("ScrollBar.thumbHighlight"));
		}
		
		private class PrettyArrowButton extends PrettyButton
		{
			private int direction;
			private Color shadow, darkShadow, highlight;
			
			public PrettyArrowButton(int direction, Color shadow, Color darkShadow, Color highlight)
			{
				super();
				
				this.direction = direction;
				this.shadow = shadow;
				this.darkShadow = darkShadow;
				this.highlight = highlight;
				
				setPreferredSize(new Dimension(18, 18));
			}
			
			@Override
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				int w = getSize().width;
				int h = getSize().height;
				int size = Math.min((h - 4) / 3, (w - 4) / 3);
				size = Math.max(size, 2);
				paintTriangle(g, (getWidth() - size) / 2, (h - size) / 2, size, direction, isEnabled());
			}
			
			private void paintTriangle(Graphics g, int x, int y, int size, int direction, boolean isEnabled)
			{
				Color oldColor = g.getColor();
				int mid, i, j;
				
				j = 0;
				size = Math.max(size, 2);
				mid = (size / 2) - 1;
				
				g.translate(x+1, y);
				if(isEnabled)
					g.setColor(darkShadow);
				else
					g.setColor(shadow);
				
				switch(direction) {
					case NORTH:
						for(i = 0; i < size; i++) {
							g.drawLine(mid-i, i, mid+i, i);
						}
						if(!isEnabled)  {
							g.setColor(highlight);
							g.drawLine(mid-i+2, i, mid+i, i);
						}
						break;
					case SOUTH:
						if(!isEnabled) {
							g.translate(1, 1);
							g.setColor(highlight);
							for(i = size-1; i >= 0; i--) {
								g.drawLine(mid-i, j, mid+i, j);
								j++;
							}
							g.translate(-1, -1);
							g.setColor(shadow);
						}
						
						j = 0;
						for(i = size-1; i >= 0; i--) {
							g.drawLine(mid-i, j, mid+i, j);
							j++;
						}
						break;
					case WEST:
						for(i = 0; i < size; i++) {
							g.drawLine(i, mid-i, i, mid+i);
						}
						if(!isEnabled)  {
							g.setColor(highlight);
							g.drawLine(i, mid-i+2, i, mid+i);
						}
						break;
					case EAST:
						if(!isEnabled) {
							g.translate(1, 1);
							g.setColor(highlight);
							for(i = size-1; i >= 0; i--) {
								g.drawLine(j, mid-i, j, mid+i);
								j++;
							}
							g.translate(-1, -1);
							g.setColor(shadow);
						}
						
						j = 0;
						for(i = size-1; i >= 0; i--) {
							g.drawLine(j, mid-i, j, mid+i);
							j++;
						}
						break;
				}
				g.translate(-x-1, -y);    
				g.setColor(oldColor);
			}
		}
	}
}
