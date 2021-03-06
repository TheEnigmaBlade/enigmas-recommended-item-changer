package net.enigmablade.lol.lolitem;

import javax.swing.*;

import net.enigmablade.paradoxion.io.*;
import net.enigmablade.paradoxion.util.*;

import net.enigmablade.lol.lollib.data.*;
import net.enigmablade.lol.lollib.ui.*;

import static net.enigmablade.paradoxion.util.Logger.*;

public class EnigmaItemsMain
{
	public static void main(String[] args)
	{
		//Generic args
		boolean debugMode = false, consoleMode = false;
		String champion = null, code = null;
		//Program operation args
		boolean noRaf = false;
		
		//Parse command-line arguments
		for(int n = 0; n < args.length; n++)
		{
			String arg = args[n];
			if(arg.equals("-debug"))
			{
				debugMode = true;
			}
			else if(arg.equals("-console"))
			{
				consoleMode = true;
			}
			else if(arg.equals("-noraf"))
			{
				noRaf = true;
			}
			else if(arg.startsWith("eric://"))
			{
				int index = arg.lastIndexOf('/');
				if(index > 6)
				{
					champion = arg.substring(arg.indexOf('/')+2, index);
					code = arg.substring(index+1);
				}
			}
			else
			{
				System.out.println("Unknown argument \""+arg+"\"");
			}
		}
		
		SystemUtil.initSystem(debugMode, consoleMode);
		
		//Splash
		SplashScreen.init();
		SplashScreen.drawString("Loading...");
		
		//Look and feel
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//UIManager.setLookAndFeel(new PrettyLookAndFeel());
			//JFrame.setDefaultLookAndFeelDecorated(true);
			
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", EnigmaItems.appName);
		}
		catch(Exception e)
		{
			writeToLog("Failed to set the system look and feel", LoggingType.WARNING);
			writeStackTrace(e);
		}
		
		//Create a new instance and parse the build code if required
		try
		{
			EnigmaItems main = new EnigmaItems(!noRaf);
			
			if(champion != null && code != null)
			{
				SplashScreen.drawString("Champion: "+champion);
				if(ChampionDatabase.getChampion(champion) != null)
				{
					if(code.length() == 24)
					{
						writeToLog("Opening URI data: champion="+champion+", code="+code);
						//TODO: main.setChampion(ChampionDatabase.getChampion(champion));
						//main.importCode(code);
					}
					else
					{
						writeToLog("Invalid build code", LoggingType.ERROR);
						JOptionPane.showMessageDialog(null, "The build link is invalid: invalid build code", "Invalid build link", JOptionPane.ERROR_MESSAGE);
						//System.exit(0);
					}
				}
				else
				{
					writeToLog("Invalid champion", LoggingType.ERROR);
					JOptionPane.showMessageDialog(null, "The build link is invalid: invalid champion", "Invalid build link", JOptionPane.ERROR_MESSAGE);
					//System.exit(0);
				}
			}
			SplashScreen.close();
			main.open();
		}
		catch(Exception e)
		{
			writeToLog("Oh no! Unhandled exception!", LoggingType.ERROR);
			writeStackTrace(e);
			
			String text = e.getClass().toString();
			StackTraceElement[] trace = e.getStackTrace();
			for(int n = 0; n < Math.min(trace.length, 8); n++)
				text += "\n    "+trace[n].toString();
			Object[] options = {"Copy error", "Ok..."};
			if(JOptionPane.showOptionDialog(null, "Oh no! It broke!\n\nException thrown: "+text+"\n\nSee logs for more information.\nContact enigma@enigmablade.net if the problem continues.", "Am sad :(", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[1]) == JOptionPane.OK_OPTION)
				ClipboardUtil.setClipboardContents(text);
			SystemUtil.exit(1);
		}
	}
}
