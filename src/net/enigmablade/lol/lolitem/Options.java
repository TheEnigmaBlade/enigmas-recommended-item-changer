package net.enigmablade.lol.lolitem;

import java.io.*;
import java.util.*;
import javax.swing.*;
import net.enigmablade.lol.lolitem.ui.*;
import net.enigmablade.lol.lollib.io.*;

import static net.enigmablade.paradoxion.util.Logger.*;



public class Options
{
	private static final File file = new File("options.txt");
	
	//Options data
	public String lolDirPath = null;
	public String lolDirRegion = null;
	public Deque<GamePath> lolDirHistory;
	
	public List<String> favoriteChampions;
	
	public boolean checkVersion = true;
	public static final String[] languages = {"en-US", "en-UK", "en-CA", "en-AU"};
	public String currentLanguage = languages[0];
	public int[] imageSizes = {60, 45, 30};
	public int imageSize = 45;
	public boolean tooltipsEnabled = true;
	public ItemDisplayMode itemDisplayMode = ItemDisplayMode.GRID;
	public boolean useDefaultItems = true;
	public boolean backupEnabled = false;
	public String backupLocation = "backup";
	public boolean minimizeToTray = false;
	public boolean systemTrayEnabled = false;
	
	public boolean changeMade;
	public boolean showSaveWarning = true;
	public int saveDefault = JOptionPane.YES_OPTION;
	
	public boolean firstStartup = false;
	
	public Options()
	{
		lolDirHistory = new ArrayDeque<GamePath>();
		favoriteChampions = new ArrayList<String>();
	}
	
	public static Options loadOptions()
	{
		writeToLog("Loading options");
		Options o = new Options();
		
		if(o.firstStartup = !file.exists())
			createOptions();
		
		Scanner scanner = null;
		String line = null;
		try
		{
			scanner = new Scanner(file);
			while(scanner.hasNext())
			{
				line = scanner.nextLine().trim();
				int index = line.indexOf(':');
				String key = line.substring(0, index);
				String value = line.substring(index+1);
				if(key.equals("lolpath"))
				{
					if("null".equals(value))
						o.lolDirPath = null;
					else
						o.lolDirPath = value;
				}
				else if(key.equals("issea"))
				{
					o.lolDirRegion = "sea";
				}
				else if(key.equals("lolregion"))
				{
					o.lolDirRegion = value;
				}
				else if(key.equals("language"))
				{
					for(String lang : languages)
						if(lang.equals(value))
						{
							o.currentLanguage = value;
							break;
						}
				}
				else if(key.equals("itemdisplay"))
				{
					o.itemDisplayMode = ItemDisplayMode.getType(value);
				}
				else if(key.equals("usedefaults"))
				{
					int bool = Integer.parseInt(value);
					o.useDefaultItems = bool == 1;
				}
				else if(key.equals("autobackup"))
				{
					int bool = Integer.parseInt(value);
					o.backupEnabled = bool == 1;
				}
				else if(key.equals("imagesize"))
				{
					int size = Integer.parseInt(value);
					if(size != 30 && size != 45 && size != 60)
						size = 45;
					o.imageSize = size;
				}
				else if(key.equals("ttenabled"))
				{
					int bool = Integer.parseInt(value);
					o.tooltipsEnabled = (bool == 1);
				}
				else if(key.equals("minimizetotray"))
				{
					int bool = Integer.parseInt(value);
					o.minimizeToTray = (bool == 1);
				}
				else if(key.equals("updatestartup"))
				{
					int bool = Integer.parseInt(value);
					o.checkVersion = bool == 1;
				}
				else if(key.equals("showsavewarning"))
				{
					int bool = Integer.parseInt(value);
					o.showSaveWarning = bool == 1;
				}
				else if(key.equals("saveaction"))
				{
					o.saveDefault = Integer.parseInt(value);
				}
				else if(key.equals("favorites"))
				{
					String[] favs = value.split(",");
					for(String s : favs)
						o.favoriteChampions.add(s);
				}
				else if(key.equals("globalitems"))
				{
					String[] setStrings = value.split(";");
					for(String setString : setStrings)
					{
						//String name = setString.substring(0, setString.indexOf(':'));
						String[] itemStrings = setString.substring(setString.indexOf(':'+1)).split(",");
						//ItemSet set = new ItemSet(name);
						for(int n = 0; n < itemStrings.length; n++)
						{
							//TODO
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			writeToLog("Failed to load options", LoggingType.ERROR);
			writeStackTrace(e);
			JOptionPane.showMessageDialog(null, "Error: Could not load options", "Error", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			if(scanner != null)
				scanner.close();
		}
		
		return o;
	}
	
	private static void createOptions()
	{
		PrintStream out = null;
		try
		{
			out = new PrintStream(file);
			out.println("lolpath:null");
			out.println("useadvanced:0");
			out.println("itemdisplay:grid");
			out.println("usedefaults:1");
			out.println("imagesize:1");
			out.println("ttenabled:1");
			out.println("minimizetotray:0");
			out.println("updatestartup:1");
		}
		catch(FileNotFoundException e)
		{
			writeToLog("Failed to create options file.", LoggingType.ERROR);
			writeStackTrace(e);
			JOptionPane.showMessageDialog(null, "Could not create options file", "Error", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			if(out != null)
				out.close();
		}
	}
	
	public static boolean saveOptions(Options o)
	{
		writeToLog("Saving options");
		File file = new File("options.txt");
		PrintStream out = null;
		try
		{
			out = new PrintStream(file);
			GamePath dir = GamePathUtil.getDir();
			out.println("lolpath:"+(dir != null ? dir.getPath() : null));
			if(dir != null)
				out.println("lolregion:"+dir.getRegion().toString().toLowerCase());
			out.println("language:"+o.currentLanguage);
			out.println("itemdisplay:"+(o.itemDisplayMode.toString().toLowerCase()));
			out.println("usedefaults:"+(o.useDefaultItems ? "1" : "0"));
			out.println("autobackup:"+(o.backupEnabled ? "1" : "0"));
			out.println("imagesize:"+o.imageSize);
			out.println("ttenabled:"+(o.tooltipsEnabled ? "1" : "0"));
			out.println("minimizetotray:"+(o.minimizeToTray ? "1" : "0"));
			out.println("updatestartup:"+(o.checkVersion ? "1" : "0"));
			if(!o.showSaveWarning)
			{
				out.println("showsavewarning:"+(o.showSaveWarning ? "1" : "0"));
				out.println("saveaction:"+o.saveDefault);
			}
			if(o.favoriteChampions.size() > 0)
			{
				String list = "";
				for(int n = 0; n < o.favoriteChampions.size(); n++)
				{
					list += o.favoriteChampions.get(n);
					if(n < o.favoriteChampions.size()-1)
						list += ",";
				}
				out.println("favorites:"+list);
			}
		}
		catch(FileNotFoundException e)
		{
			writeToLog("Failed to save options", LoggingType.ERROR);
			writeStackTrace(e);
			//TODO
			//JOptionPane.showMessageDialog(ui, getString("dialog.options.errorSave"), getString("dialog.options.errorTitle"), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		finally
		{
			if(out != null)
				out.close();
		}
		return true;
	}
}
