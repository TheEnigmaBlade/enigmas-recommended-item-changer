package net.enigmablade.lol.lolitem.ui;

import java.awt.*;

import static net.enigmablade.paradoxion.util.Logger.*;

public class SplashScreen
{
	private static java.awt.SplashScreen splash;
	private static Graphics2D graphics;
	private static int width, height;
	private static FontMetrics fontMetrics;
	
	public static void init()
	{
		splash = java.awt.SplashScreen.getSplashScreen();
		if(splash != null)
		{
			width = splash.getSize().width;
			height = splash.getSize().height;
			
			graphics = splash.createGraphics();
			graphics.setPaintMode();
			graphics.setColor(new Color(50, 220, 255));
			graphics.setFont(new Font("Ariel", Font.BOLD, 14));
			fontMetrics = graphics.getFontMetrics();
		}
	}
	
	public static void drawString(String text)
	{
		try
		{
			if(graphics != null && splash.isVisible())
			{
				int y = height-36;
				graphics.setComposite(AlphaComposite.Clear);
				graphics.fillRect(0, 0, width, height);
				graphics.setPaintMode();
				graphics.drawString(text, 34+((width-34)/2)-(fontMetrics.stringWidth(text)/2), y);
				splash.update();
			}
		}
		catch(Exception e)
		{
			writeStackTrace(e);
		}
	}
	
	public static void close()
	{
		try
		{
			if(splash != null && splash.isVisible())
				splash.close();
		}
		catch(Exception e)
		{
			writeToLog("SPLASH PROBLEM", LoggingType.WARNING);
		}
	}
}
