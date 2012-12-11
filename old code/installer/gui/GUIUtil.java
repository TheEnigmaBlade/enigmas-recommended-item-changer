package enigma.installer.gui;

import java.awt.*;
import java.net.*;

public class GUIUtil
{
	public static void openURL(String url)
	{
		if(Desktop.isDesktopSupported()) 
		{
			Desktop desktop = Desktop.getDesktop();
			try
			{
				desktop.browse(new URI(url));
			}
			catch (Exception e){}
		}
	}
	
	public static Color addAlpha(Color c, int alpha)
	{
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}
	
	public static Color adjustColor(Color c, int offset)
	{
		return new Color(capColorValue(c.getRed()+offset), capColorValue(c.getGreen()+offset), capColorValue(c.getBlue()+offset), c.getAlpha());
	}
	
	private static int capColorValue(int c)
	{
		return c < 0 ? 0 : c > 255 ? 255 : c;
	}
}
