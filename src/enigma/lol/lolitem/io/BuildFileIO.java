package enigma.lol.lolitem.io;

import java.io.*;
import java.util.*;

import enigma.lol.lolitem.data.*;
//import enigma.lol.lolitem.gui.dialogs.*;

import enigma.paradoxion.io.xml.*;
import static enigma.paradoxion.util.Logger.*;

import enigma.lol.lollib.data.*;

public class BuildFileIO
{
	public static void exportBuilds(File file)
	{
		if(!file.getName().endsWith(".xml"))
			file = new File(file.getAbsolutePath()+".xml");
		writeToLog("Exporting to file: "+file.getAbsolutePath(), 1);
		
		//Generate XML tree of item builds
		XMLNode root = new XMLNode("builds");
		
		Set<String> champions = ChampionDatabase.getChampions();
		for(String championKey : champions)
		{
			Champion champion = ChampionDatabase.getChampion(championKey);
			if(ItemFileIO.championHasItems(champion))
			{
				writeToLog("Exporting champion: "+champion.getKey(), 1);
				XMLNode championNode = root.addChild("champion").addAttribute("name", champion.getKey());
				
				for(GameMode mode : GameMode.values())
				{
					List<ItemSet> itemsClassic = ItemFileIO.getItems(champion, mode);
					if(itemsClassic != null && itemsClassic.size() > 0)
					{
						XMLNode modeNode = championNode.addChild("gamemode").addAttribute("mode", mode.toString());
						for(int n = 0; n < itemsClassic.size(); n++)
						{
							ItemSet set = itemsClassic.get(n);
							XMLNode setNode = modeNode.addChild("set").addAttribute("name", set.getName());
							for(int i = 0; i < 6; i++)
							{
								Item item = set.getItem(i);
								setNode.addChild("item").addAttribute("slot", i+1).addAttribute("id", item != null ? item.getID() : "").close();
							}
							setNode.close();
						}
						modeNode.close();
					}
				}
			}
		}
		
		root.close();
		
		//Save XML to file
		XMLWriter out = null;
		try
		{
			out = new XMLWriter(file, "xml", "1.0");
			out.write(root);
		}
		catch(FileNotFoundException e)
		{
			writeToLog("Failed to export item builds");
			writeStackTrace(e);
		}
		finally
		{
			if(out != null)
				out.close();
		}
	}
	
	public static void importBuilds(File file)
	{
		//Load builds from file
		XMLParser parser;
		try
		{
			parser = new XMLParser(new FileInputStream(file), "xml", "1.0");
			
		}
		catch(FileNotFoundException e)
		{
			writeToLog("Failed to import item builds", LoggingType.ERROR);
			writeStackTrace(e);
			return;
		}
		
		XMLNode root = parser.getRoot();
		
		ItemBuilds builds = new ItemBuilds();
		
		ArrayList<XMLNode> championNodes = root.getChildren();
		for(XMLNode championNode : championNodes)
		{
			if(championNode.getName().equals("champion"))
			{
				//Each champion
				Champion champion = ChampionDatabase.getChampion(championNode.getAttribute("name"));
				ArrayList<XMLNode> modes = championNode.getChildren();
				for(XMLNode modeNode : modes)
				{
					if(modeNode.getName().equals("gamemode"))
					{
						//Each game mode
						GameMode mode = GameMode.stringToMode(modeNode.getAttribute("mode"));
						builds.addChampion(mode, champion);
						
						ArrayList<XMLNode> setNodes = modeNode.getChildren();
						for(XMLNode setNode : setNodes)
						{
							//Each item set
							if(setNode.getName().equals("set"))
							{
								String name = setNode.getAttribute("name");
								ItemSet set = new ItemSet(name);
								ArrayList<XMLNode> itemNodes = setNode.getChildren();
								for(XMLNode itemNode : itemNodes)
								{
									if(itemNode.getName().equals("item"))
									{
										String id = itemNode.getAttribute("id");
										int pos = Integer.parseInt(itemNode.getAttribute("slot"));
										set.setItem(ItemDatabase.getItem(id), pos-1);
									}
									else
									{
										writeToLog("Invalid item node name: "+itemNode.getName(), LoggingType.WARNING);
									}
								}
								builds.addBuild(mode, champion, set);
							}
							else
							{
								writeToLog("Invalid set node name: "+setNode.getName(), LoggingType.WARNING);
							}
						}
					}
					else
					{
						writeToLog("Invalid mode node name: "+modeNode.getName(), LoggingType.WARNING);
					}
				}
			}
			else
			{
				writeToLog("Invalid champion node name: "+championNode.getName(), LoggingType.WARNING);
			}
		}
		
		//Save builds with existing builds
		ItemBuilds chosen = builds;//BuildChooserDialog.openDialog(null, builds);
		for(GameMode mode : GameMode.values())
		{
			writeToLog("Mode: "+mode, 1);
			Set<Champion> champions = chosen.getChampions(mode);
			for(Champion champion : champions)
			{
				writeToLog("Champion: "+champion.getKey(), 2);
				List<ItemSet> existingSets = ItemFileIO.getItems(champion, mode);
				List<ItemSet> newSets = chosen.getItemSets(mode, champion);
				if(existingSets != null || newSets.size() > 0)
				{
					if(existingSets == null)
						existingSets = new ArrayList<ItemSet>(newSets);
					else
						existingSets.addAll(newSets);
					existingSets.addAll(newSets);
					ItemFileIO.saveItems(champion, mode, existingSets, 0);
				}
			}
		}
	}
}
