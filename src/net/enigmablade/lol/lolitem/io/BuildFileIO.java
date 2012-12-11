package net.enigmablade.lol.lolitem.io;

import java.io.*;
import java.util.*;
import net.enigmablade.lol.lolitem.data.*;
import net.enigmablade.lol.lollib.data.*;
import net.enigmablade.lol.lollib.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import static net.enigmablade.paradoxion.util.Logger.*;

public class BuildFileIO
{
	private static File getBuildsDir(Champion champion)
	{
		return new File(GamePathUtil.getChampionDir()+"/"+champion.getKey()+"/Recommended");
	}
	
	//Loading
	
	public static List<ItemBuild> loadBuilds(Champion champion, GameMode mode)
	{
		if(champion == null || mode == null)
			throw new IllegalArgumentException("Champion and mode cannot be null");
		
		writeToLog("BuildIO # Loading builds: champion="+champion+", mode="+mode);
		
		//Check if the recommended item folder exists
		File baseDir = getBuildsDir(champion);
		if(!baseDir.exists())
		{
			writeToLog("BuildIO # Recommended item folder doesn't exist", 1);
			return null;
		}
		
		//Get all build files
		File[] buildFiles = baseDir.listFiles();
		
		//Filter out build files to match the mode
		String buildFileBase = "EnigmaItem_"+mode.getMapID()+"_"+mode.getModeID();
		writeToLog("BuildIO # Filtering build files with base: "+buildFileBase, 1);
		
		List<File> filteredBuildFiles = new ArrayList<File>(buildFiles.length);
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
		Collections.sort(filteredBuildFiles);
		
		//Load build files
		writeToLog("BuildIO # Loading build files", 1);
		List<ItemBuild> builds = new ArrayList<ItemBuild>(filteredBuildFiles.size());
		
		for(File buildFile : filteredBuildFiles)
		{
			ItemBuild build = loadBuild(buildFile, champion, mode);
			if(build != null)
				builds.add(build);
		}
		
		//No builds were loaded
		if(builds.size() == 0)
		{
			writeToLog("BuildIO # No builds were successfully loaded", 1, LoggingType.WARNING);
			return null;
		}
		
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
						for(Object o2 : itemsList)
						{
							JSONObject item = (JSONObject)o2;
							
							String id = (String)item.get("id");
							Long count = (Long)item.get("count");
							//System.out.println("\t\tID = "+id+", count = "+count);
							
							Item i = ItemDatabase.getItem(id);
							for(int n = 0; n < count; n++)
								blockItems.add(i);
						}
						
						ItemGroup group = new ItemGroup(blockName, blockItems);
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
		
		if(!actualChampion.getKey().equals(champion))
			writeToLog("Champion keys do not match: given \""+champion+"\", expected \""+actualChampion.getKey()+"\"", 3, LoggingType.WARNING);
		if(!actualMode.getMapID().equals(mapID))
			writeToLog("Map IDs do not match: given \""+mapID+"\", expected \""+actualMode.getMapID()+"\"", 3, LoggingType.WARNING);
		if(!actualMode.getModeID().equals(modeID))
			writeToLog("Mode IDs do not match: given \""+modeID+"\", expected \""+actualMode.getModeID()+"\"", 3, LoggingType.WARNING);
		
		ItemBuild build = new ItemBuild(title, groups);
		build.setAuthor(author);
		build.setType(type);
		build.setDescription(description);
		build.setPriority(isPriority);
		
		return build;
	}
	
	public static int getPrimaryBuild(List<ItemBuild> builds)
	{
		return -1;
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
		File baseDir = getBuildsDir(champion);
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
			for(Item item : group.getItems())
			{
				JSONObject itemObject = new JSONObject();
				itemObject.put("id", item.getID());
				itemObject.put("count", 1);
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
}
