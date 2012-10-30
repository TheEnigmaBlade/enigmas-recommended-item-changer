package enigma.lol.lolitem.io;

import java.io.*;
import java.util.*;

import static enigma.paradoxion.util.Logger.*;

import enigma.lol.lollib.data.*;
import enigma.lol.lollib.io.*;

import enigma.lol.lolitem.data.*;

public class ItemFileIO
{
	private static final String classicFile = "/RecItemsCLASSIC.ini";
	private static final String twistedFile = "/RecItemsCLASSICMap10.ini";
	private static final String dominionFile = "/RecItemsODIN.ini";
	private static final String allMidFile = "/RecItemsARAM.ini";
	
	public static List<ItemSet> getItems(Champion champion, GameMode mode)
	{
		if(champion != null)
		{
			File itemFile = new File(GamePathUtil.getItemDir()+champion.getKey()+modeToFile(mode));
			
			if(itemFile.exists())
			{
				List<ItemSet> itemSets = new ArrayList<ItemSet>(1);
				Scanner scanner = null;
				try
				{
					scanner = new Scanner(itemFile);
					boolean loadingItems = false;
					String setName = null;
					Item[] items = null;
					int itemsLoaded = 0;
								
					while(scanner.hasNext())
					{
						String line = scanner.nextLine().trim();
						if(line.length() > 0)
						{
							if(line.contains("#"))
							{
								line = line.substring(0, line.indexOf("#"));
							}
							
							if(loadingItems)
							{
								if(line.startsWith("RecItem"))
								{
									int itemIndex = Integer.parseInt(line.substring(7, 8));
									String itemID = line.substring(line.indexOf('=')+1).trim();
									items[itemIndex-1] = ItemDatabase.getItem(itemID);
									itemsLoaded++;
									loadingItems = itemsLoaded < 6;
								}
								else if(line.startsWith("SetName"))
								{
									setName = line.substring(line.indexOf('=')+1).trim();
								}
							}
							else
							{
								if(line.startsWith("[ItemSet"))
								{
									if(items != null)
										itemSets.add(new ItemSet(setName, items));
									
									items = new Item[6];
									loadingItems = true;
									itemsLoaded = 0;
								}
							}
						}
					}
					
					if(items != null)
						itemSets.add(new ItemSet(setName, items));
					
					return itemSets;
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				finally
				{
					if(scanner != null)
						scanner.close();
				}
			}
		}
		return null;
	}
	
	public static boolean saveItems(Champion champion, GameMode mode, List<ItemSet> list, int currentItemSet)
	{
		return saveItems(GamePathUtil.getItemDir(), champion, mode, list, currentItemSet);
	}
	
	public static boolean saveItems(String dir, Champion champion, GameMode mode, List<ItemSet> items, int currentItemSet)
	{
		File champsDir = new File(dir);
		File champDir = new File(champsDir.getAbsolutePath()+File.separator+champion.getKey());
		if(items.size() == 0)
		{
			removeItemFile(champion, mode);
			return true;
		}
		else
		{
			if(!champDir.exists())
				champDir.mkdirs();
			File itemFile = new File(champDir.getAbsolutePath()+File.separator+modeToFile(mode));
			writeToLog("Saving items to file: "+itemFile.getAbsolutePath());
			
			PrintStream out = null;
			try
			{
				itemFile.createNewFile();
				out = new PrintStream(itemFile);
				
				out.println("[ItemSet1]");
				ItemSet set = items.get(currentItemSet);
				out.println("SetName="+set.getName());
				for(int n = 0; n < 6; n++)
				{
					Item item = set.getItem(n);
					out.println("RecItem"+(n+1)+"="+(item != null ? item.getID() : -1));
				}
				out.println();
				
				int index = 2;
				for(int i = 0; i < items.size(); i++)
				{
					if(i != currentItemSet)
					{
						out.println("[ItemSet"+index+"]");
						set = items.get(i);
						out.println("SetName="+set.getName());
						for(int n = 0; n < 6; n++)
						{
							Item item = set.getItem(n);
							out.println("RecItem"+(n+1)+"="+(item != null ? item.getID() : -1));
						}
						if(index < items.size())
							out.println();
						
						index++;
					}
				}
				
				return true;
			}
			catch(FileNotFoundException e)
			{
				writeToLog("Could not open file for writting");
				writeStackTrace(e);
			}
			catch(IOException e)
			{
				writeToLog("Failed to save items");
				writeStackTrace(e);
			}
			finally
			{
				if(out != null)
					out.close();
			}
		}
		
		return false;
	}
	
	public static void removeItemFile(Champion champion, GameMode mode)
	{
		File itemFile = new File(GamePathUtil.getItemDir()+champion.getKey()+modeToFile(mode));
		if(itemFile.exists())
			itemFile.delete();
		if(!itemFileExists(champion, mode) && !itemFileExists(champion, mode))
		{
			File champDir = new File(GamePathUtil.getItemDir()+champion.getKey());
			if(champDir.exists() && champDir.list().length == 0)
				champDir.delete();
		}
	}
	
	public static boolean itemFileExists(Champion champion, GameMode mode)
	{
		File itemFile = new File(GamePathUtil.getItemDir()+champion.getKey()+modeToFile(mode));
		return itemFile.exists();
	}
	
	public static boolean championHasItems(Champion champion)
	{
		boolean hasItems = itemFileExists(champion, GameMode.CLASSIC);
		hasItems |= itemFileExists(champion, GameMode.DOMINION);
		return hasItems;
	}
	
	//Helper methods
	
	private static String modeToFile(GameMode mode)
	{
		switch(mode)
		{
			case CLASSIC: return classicFile;
			case TWISTED: return twistedFile;
			case DOMINION: return dominionFile;
			case ALL_MID: return allMidFile;
			
			default: return null;
		}
	}
}
