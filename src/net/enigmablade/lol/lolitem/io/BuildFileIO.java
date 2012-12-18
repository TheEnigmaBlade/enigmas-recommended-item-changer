package net.enigmablade.lol.lolitem.io;

import java.io.*;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.*;

import static net.enigmablade.paradoxion.util.Logger.*;

import net.enigmablade.lol.lolitem.data.*;
import net.enigmablade.lol.lollib.data.*;
import net.enigmablade.lol.lollib.io.*;
import net.enigmablade.lol.lollib.io.raf.*;

public class BuildFileIO
{
	private static File defaultItemsDir;
	
	public static boolean initDefaultItems(String uniqueName)
	{
		defaultItemsDir = RafUtil.extractPathsMatching(GamePathUtil.getDir().getFile(), uniqueName+"_DefaultItems", "^DATA/Characters/[A-za-z]+/Recommended/.*(DM|PG|SR|TT)\\.json$");
		return defaultItemsDir != null;
	}
	
	//Loading
	
	public static List<ItemBuild> loadCustomBuilds(Champion champion, GameMode mode)
	{
		writeToLog("BuildIO # Loading custom builds: champion="+champion+", mode="+mode);
		return loadBuilds(GamePathUtil.getChampionDir(), champion, mode);
	}
	
	public static ItemBuild loadDefaultBuild(Champion champion, GameMode mode)
	{
		writeToLog("BuildIO # Loading default builds: champion="+champion+", mode="+mode);
		if(defaultItemsDir != null)
		{
			List<ItemBuild> builds = loadBuilds(defaultItemsDir.getAbsolutePath()+"/DATA/Characters", champion, mode);
			if(builds.size() > 1)
				writeToLog("Something isn't right, more than one default items file was found", 1, LoggingType.WARNING);
			if(builds != null && builds.size() > 0)
			{
				ItemBuild build = builds.get(0);
				build.setType("default");
				return build;
			}
		}
		else
			writeToLog("BuildIO # Default items not initialized", 1, LoggingType.WARNING);
		return null;
	}
	
	
	public static List<ItemBuild> loadBuilds(String basePath, Champion champion, GameMode mode)
	{
		if(champion == null || mode == null)
			throw new IllegalArgumentException("Champion and mode cannot be null");
		
		writeToLog("BuildIO # Loading builds: champion="+champion+", mode="+mode, 1);
		
		//Check if the recommended item folder exists
		File baseDir = getBuildsDir(basePath, champion);
		if(!baseDir.exists())
		{
			writeToLog("BuildIO # Recommended item folder doesn't exist", 2);
			return null;
		}
		
		//Get all build files
		File[] buildFiles = baseDir.listFiles();
		
		//Load build files
		writeToLog("BuildIO # Loading build files", 2);
		List<ItemBuild> builds = new ArrayList<ItemBuild>(buildFiles.length);
		
		for(File buildFile : buildFiles)
		{
			ItemBuild build = loadBuild(buildFile, champion, mode);
			if(build != null)
				builds.add(build);
		}
		
		//No builds were loaded
		if(builds.size() == 0)
		{
			writeToLog("BuildIO # No builds were successfully loaded", 2, LoggingType.WARNING);
			return null;
		}
		
		Collections.sort(builds);
		
		////////////////asdffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff
		
		//Filter out build files to match the mode
		//String buildFileBase = "EnigmaItem_"+mode.getMapID()+"_"+mode.getModeID();
		//writeToLog("BuildIO # Filtering build files with base: "+buildFileBase, 1);
		
		/*List<File> filteredBuildFiles = new ArrayList<File>(buildFiles.length);
		for(File buildFile : buildFiles)
			if(buildFile.getName().startsWith(buildFileBase) && buildFile.getName().endsWith(".json"))
			{
				//writeToLog("BuildIO # "+buildFile.getName(), 2);
				filteredBuildFiles.add(buildFile);
			}
		
		//Check if the mode has build files
		if(filteredBuildFiles.size() == 0)
		{
			writeToLog("BuildIO # No build files exist for mode", 1);
			return null;
		}
		
		//Make sure build files are in order
		Collections.sort(filteredBuildFiles);*/
		
		return builds;
	}
	
	private static ItemBuild loadBuild(File file, Champion actualChampion, GameMode actualMode)
	{
		writeToLog("BuildIO # Loading build: "+file.getName(), 2);
		//Parse JSON
		JSONParser parser = new JSONParser();
		JSONObject root = null;
		Reader in = null;
		try
		{
			root = (JSONObject)parser.parse(in = new FileReader(file));
		}
		catch(IOException e)
		{
			writeToLog("Could not find JSON file", 3, LoggingType.ERROR);
			writeStackTrace(e);
			return null;
		}
		catch(ParseException e)
		{
			writeToLog("Could not parse JSON", 3, LoggingType.ERROR);
			writeStackTrace(e);
			return null;
		}
		finally
		{
			if(in != null)
				try
				{
					in.close();
				}
				catch(IOException e){}
		}
		
		//Data
		String champion = null;
		String title = null;
		String type = null;
		String author = null;
		String description = null;
		String mapID = null;
		String modeID = null;
		boolean isPriority = false;
		List<ItemGroup> groups = null;
		
		//Load data
		try
		{
			for(Object obj : root.keySet())
			{
				String key = (String)obj;
				Object value = root.get(key);
				
				if("champion".equals(key))
				{
					champion = (String)value;
					//System.out.println("Champion = "+champion);
				}
				else if("title".equals(key))
				{
					title = (String)value;
					//System.out.println("Title = "+title);
				}
				else if("_author".equals(key))
				{
					author = (String)value;
					//System.out.println("Author = "+author);
				}
				else if("_notes".equals(key))
				{
					description = (String)value;
					//System.out.println("Notes = "+description);
				}
				else if("priority".equals(key))
				{
					isPriority = (Boolean)value;
					//System.out.println("Priority = "+isPriority);
				}
				else if("map".equals(key))
				{
					mapID = (String)value;
					//System.out.println("Map = "+mapID);
				}
				else if("mode".equals(key))
				{
					modeID = (String)value;
					//System.out.println("Mode = "+modeID);
				}
				else if("type".equals(key))
				{
					type = (String)value;
					//System.out.println("Type = "+type);
				}
				else if("blocks".equals(key))
				{
					JSONArray blocks = (JSONArray)value;
					groups = new ArrayList<ItemGroup>(blocks.size());
					for(Object o : blocks)
					{
						JSONObject block = (JSONObject)o;
						//System.out.println("Items:");
						
						String blockName = (String)block.get("type");
						//System.out.println("\tGroup = "+blockName);
						
						JSONArray itemsList = (JSONArray)block.get("items");
						List<Item> blockItems = new ArrayList<Item>(itemsList.size());
						List<Integer> blockItemCounts = new ArrayList<Integer>(itemsList.size());
						for(Object o2 : itemsList)
						{
							JSONObject item = (JSONObject)o2;
							
							String id = (String)item.get("id");
							Long count = (Long)item.get("count");
							//System.out.println("\t\tID = "+id+", count = "+count);
							
							Item i = ItemDatabase.getItem(id);
							blockItems.add(i);
							blockItemCounts.add(count.intValue());
						}
						
						ItemGroup group = new ItemGroup(blockName, blockItems, blockItemCounts);
						groups.add(group);
					}
				}
			}
		}
		catch(Exception e)
		{
			writeToLog("Failed to load build data", 3, LoggingType.ERROR);
			writeStackTrace(e);
			return null;
		}
		
		if(mapID == null || modeID == null || groups == null)
		{
			writeToLog("Map ID, Mode ID, and blocks must exist", 3, LoggingType.ERROR);
			return null;
		}
		
		boolean bad = false;
		if(bad |= !actualChampion.getKey().equals(champion))
			writeToLog("Champion keys do not match: given \""+champion+"\", expected \""+actualChampion.getKey()+"\"", 3, LoggingType.WARNING);
		if(bad |= !actualMode.getMapID().equals(mapID))
			writeToLog("Map IDs do not match: given \""+mapID+"\", expected \""+actualMode.getMapID()+"\"", 3, LoggingType.WARNING);
		if(bad |= !actualMode.getModeID().equals(modeID))
			writeToLog("Mode IDs do not match: given \""+modeID+"\", expected \""+actualMode.getModeID()+"\"", 3, LoggingType.WARNING);
		if(bad)
			return null;
		
		ItemBuild build = new ItemBuild(title, mapID, modeID, groups);
		build.setAuthor(author);
		build.setType(type);
		build.setDescription(description);
		build.setPriority(isPriority);
		
		return build;
	}
	
	//Saving
	
	public enum SaveError
	{
		NONE, UNKNOWN, DATA, WRITE;
	}
	
	public static SaveError saveBuilds(Champion champion, GameMode mode, List<ItemBuild> itemBuilds, int primaryItemSet)
	{
		if(champion == null || mode == null)
			throw new IllegalArgumentException("Champion and mode cannot be null");
		
		writeToLog("BuildIO # Saving builds: champion="+champion+", mode="+mode);
		
		//Check if the recommended item folder exists
		File baseDir = getBuildsDir(GamePathUtil.getChampionDir(), champion);
		if(!baseDir.exists())
		{
			writeToLog("BuildIO # Recommended item folder doesn't exist, creating", 1);
			baseDir.mkdirs();
		}
		
		//Get all build files
		File[] buildFiles = baseDir.listFiles();
		
		//Filter out build files to match the mode
		String buildFileBase = "EnigmaItem_"+mode.getMapID()+"_"+mode.getModeID();
		writeToLog("BuildIO # Filtering build files with base: "+buildFileBase, 1);
		
		List<File> filteredBuildFiles = new ArrayList<File>(buildFiles.length);
		for(File buildFile : buildFiles)
			if(buildFile.getName().startsWith(buildFileBase))
			{
				writeToLog("BuildIO # "+buildFile.getName(), 2);
				filteredBuildFiles.add(buildFile);
			}
		
		//Remove conflicting build files
		if(filteredBuildFiles.size() != 0)
		{
			writeToLog("BuildIO # Removing conflicting build files", 1);
			for(File buildFile : filteredBuildFiles)
				buildFile.delete();
		}
		
		//Save builds
		writeToLog("BuildIO # Saving build files", 1);
		SaveError error = SaveError.NONE;
		if(itemBuilds != null)
		{
			for(int n = 0; n < itemBuilds.size(); n++)
			{
				ItemBuild build = itemBuilds.get(n);
				File buildFile = new File(baseDir.getAbsolutePath()+"/"+buildFileBase+"-"+n+".json");
				writeToLog("BuildIO # Saving to file: "+buildFile.getAbsolutePath(), 2);
				SaveError newError = saveBuild(buildFile, champion, mode, build, n == primaryItemSet);
				if(error == SaveError.NONE)
					error = newError;
			}
		}
		
		return error;
	}
	
	@SuppressWarnings("unchecked")
	private static SaveError saveBuild(File file, Champion champion, GameMode mode, ItemBuild build, boolean isPrimary)
	{
		JSONObject root = new JSONObject();
		
		root.put("champion", champion.getKey());
		root.put("title", build.getName());
		root.put("type", build.getType());
		root.put("_author", build.getAuthor());
		root.put("_notes", build.getDescription().replaceAll("\n", "\\n"));
		root.put("map", mode.getMapID());
		root.put("mode", mode.getModeID());
		root.put("priority", isPrimary);
		
		//Add groups
		JSONArray blocks = new JSONArray();
		for(ItemGroup group : build.getGroups())
		{
			JSONObject groupObject = new JSONObject();
			groupObject.put("type", group.getName());
			
			//Add items
			JSONArray items = new JSONArray();
			List<Item> itemList = group.getItems();
			List<Integer> itemCountList = group.getItemCounts();
			for(int n = 0; n < itemList.size(); n++)
			{
				Item item = itemList.get(n);
				int count = itemCountList.get(n);
				JSONObject itemObject = new JSONObject();
				itemObject.put("id", item.getID());
				itemObject.put("count", count);
				items.add(itemObject);
			}
			groupObject.put("items", items);
			
			blocks.add(groupObject);
		}
		root.put("blocks", blocks);
		
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(file);
			root.writeJSONString(writer);
			
			return SaveError.NONE;
		}
		catch(IOException e)
		{
			writeToLog("Failed to write to file", 1, LoggingType.ERROR);
			writeStackTrace(e);
		}
		finally
		{
			if(writer != null)
				try
				{
					writer.close();
				}
				catch(IOException e){}
		}
		
		return SaveError.WRITE;
	}
	
	//Helpers
	
	private static File getBuildsDir(String base, Champion champion)
	{
		return new File(base+"/"+champion.getKey()+"/Recommended");
	}
}
