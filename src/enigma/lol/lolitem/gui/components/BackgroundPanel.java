package enigma.lol.lolitem.gui.components;

import java.awt.*;
import javax.swing.*;

import enigma.paradoxion.ui.*;
import enigma.paradoxion.util.*;

public class BackgroundPanel extends JPanel
{
	private Image image;
	private int imageWidth, imageHeight;
	private double imageRatio;
	
	private Color backgroundColor;
	private LinearGradientPaint backgroundGradient;
	
	private Color overlay;
	
	public BackgroundPanel()
	{
		setBackground("bg1.jpg");
		
		overlay = GUIUtil.addAlpha(getBackground(), 75);
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
		
			g.setColor(overlay);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		else if(backgroundGradient != null)
		{
			backgroundGradient = new LinearGradientPaint(
					0, 0, getWidth(), getHeight(),
					backgroundGradient.getFractions(), backgroundGradient.getColors());
			
			((Graphics2D)g).setPaint(backgroundGradient);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		else
		{
			g.setColor(backgroundColor != null ? backgroundColor : getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}
	
	public void setBackground(String imageFile)
	{
		if(imageFile != null)
		{
			if(imageFile.startsWith("color-"))
			{
				try
				{
					String value = imageFile.substring(imageFile.indexOf('-')+1);
					backgroundColor = new Color(Integer.parseInt(value));
				}
				catch(Exception e)
				{
					backgroundColor = null;
				}
				
				image = null;
				backgroundGradient = null;
			}
			else if(imageFile.startsWith("gradient-"))
			{
				try
				{
					int index = imageFile.indexOf('-');
					int value1 = Integer.parseInt(imageFile.substring(index+1, index = imageFile.indexOf('-', index+1)));
					int value2 = Integer.parseInt(imageFile.substring(index+1, index = imageFile.indexOf('-', index+1)));
					int value3 = Integer.parseInt(imageFile.substring(index+1));
					backgroundGradient = new LinearGradientPaint(0, 0, 20, 20, new float[]{0.0f, 0.5f, 1.0f}, new Color[]{new Color(value1), new Color(value2), new Color(value3)});
				}
				catch(Exception e)
				{
					backgroundGradient = null;
				}
				
				image = null;
				backgroundColor = null;
			}
			else
			{
				boolean success = false;
				backgroundColor = null;
				
				image = ResourceLoader.getImage("backgrounds/"+imageFile);
				success = image != null;
				
				if(!success)
				{
					image = new ImageIcon(imageFile).getImage();
					success = image != null;
				}
				
				if(success)
				{
					imageWidth = image.getWidth(null);
					imageHeight = image.getHeight(null);
					imageRatio = (double)imageWidth/imageHeight;
				}
			}
		}
		else
		{
			image = null;
			backgroundColor = null;
			backgroundGradient = null;
			imageWidth = 0;
			imageHeight = 0;
			imageRatio = 0;
		}
		
		repaint();
	}
}
