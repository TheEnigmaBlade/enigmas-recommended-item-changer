package enigma.installer.gui.components;

import java.awt.*;
import javax.swing.*;
import enigma.installer.gui.*;
import enigma.installer.util.*;


public class BackgroundPanel extends JPanel
{
	private Image image;
	private int imageWidth, imageHeight;
	private double imageRatio;
	private Color overlay;
	private int currentImage;
	
	public BackgroundPanel()
	{
		changeImage(1);
		
		overlay = GUIUtil.addAlpha(getBackground(), 125);
	}
	
	@Override
	public void paintComponent(Graphics g)
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
	
	public void changeImage(int i)
	{
		currentImage = i;
		image = ResourceLoader.getImage("backgrounds/bg"+currentImage+".jpg");
		if(image!= null)
		{
			imageWidth = image.getWidth(null);
			imageHeight = image.getHeight(null);
			imageRatio = (double)imageWidth/imageHeight;
		}
		repaint();
	}
}
