package net.enigmablade.lol.lolitem.ui.components.pretty;

import java.awt.*;
import javax.swing.*;
import net.enigmablade.paradoxion.util.*;


public class PrettyBackgroundPanel extends JPanel
{
	private Image image;
	private int imageWidth, imageHeight;
	private double imageRatio;
	
	public PrettyBackgroundPanel()
	{
		loadBackground();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		if(image != null)
		{
			int imageDrawW = imageWidth;
			int imageDrawH = imageHeight;
			double givenRatio = (double)getWidth()/getHeight();
			if(givenRatio < imageRatio)
				imageDrawW = (int)(imageDrawH*givenRatio);
			else if(givenRatio > imageRatio)
				imageDrawH = (int)(imageDrawW/givenRatio);
			
			int imageX = (imageWidth-imageDrawW)/2;
			int imageY = (imageHeight-imageDrawH)/2;
			
			g.drawImage(image,
					0, 0, getWidth(), getHeight(),
					imageX, imageY, imageX+imageDrawW, imageY+imageDrawH,
					this);
		
		}
		else
			super.paintComponent(g);
	}
	
	public void loadBackground()
	{
		image = ResourceLoader.getImage("ui/background.png");
		if(image != null)
		{
			imageWidth = image.getWidth(null);
			imageHeight = image.getHeight(null);
			imageRatio = (double)imageWidth/imageHeight;
		}
		
		repaint();
	}
}
