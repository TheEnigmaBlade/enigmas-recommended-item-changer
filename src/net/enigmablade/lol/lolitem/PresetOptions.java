package net.enigmablade.lol.lolitem;

import java.io.*;
import java.util.*;
import javax.swing.*;
import net.enigmablade.paradoxion.io.xml.*;
import net.enigmablade.paradoxion.util.Logger.*;
import net.enigmablade.lol.lollib.data.*;

import static net.enigmablade.paradoxion.util.Logger.*;

import net.enigmablade.lol.lolitem.data.*;

public class PresetOptions
{
	private static final File file = new File("presets.xml");
	
	public Map<String, ItemBuild> buildPresets;
	public Map<String, ItemGroup> groupPresets;
	
	public PresetOptions()
	{
		buildPresets = new HashMap<String, ItemBuild>();
		groupPresets = new HashMap<String, ItemGroup>();
	}
	
	public static PresetOptions loadPresets()
	{
		writeToLog("Loading presets");
		PresetOptions presets = new PresetOptions();
		
		if(file.exists())
		{
			try
			{
				XmlParser parser = new XmlParser(new FileInputStream(file), "xml", "1.0");
				XmlNode root = parser.getRoot();
				
				//Builds
				XmlNode buildsNode = root.getChild("builds");
				if(buildsNode != null)
				{
					List<XmlNode> buildNodes = buildsNode.getChildren();
					for(XmlNode buildNode : buildNodes)
					{
						if("build".equals(buildNode.getName()))
						{
							String buildName = buildNode.getAttribute("name");
							ItemBuild build = new ItemBuild(buildName, "any", "any");
							
							List<XmlNode> groupNodes = buildNode.getChildren();
							for(XmlNode groupNode : groupNodes)
							{
								if("group".equals(groupNode.getName()))
								{
									String groupName = groupNode.getAttribute("name");
									ItemGroup group = new ItemGroup(groupName);
									
									List<XmlNode> itemNodes = groupNode.getChildren();
									for(XmlNode itemNode : itemNodes)
									{
										if("item".equals(itemNode.getName()))
										{
											String id = itemNode.getAttribute("id");
											int count = itemNode.getIntAttribute("count");
											group.addItem(ItemDatabase.getItem(id), count);
										}
									}
									
									build.addGroup(group);
								}
							}
							
							presets.buildPresets.put(buildName, build);
						}
					}
				}
				
				//Groups
				XmlNode groupsNode = root.getChild("groups");
				if(groupsNode != null)
				{
					List<XmlNode> groupNodes = groupsNode.getChildren();
					for(XmlNode groupNode : groupNodes)
					{
						if("group".equals(groupNode.getName()))
						{
							String name = groupNode.getAttribute("name");
							ItemGroup group = new ItemGroup(name);
							
							List<XmlNode> itemNodes = groupNode.getChildren();
							for(XmlNode itemNode : itemNodes)
							{
								if("item".equals(itemNode.getName()))
								{
									String id = itemNode.getAttribute("id");
									int count = itemNode.getIntAttribute("count");
									group.addItem(ItemDatabase.getItem(id), count);
								}
							}
							
							presets.groupPresets.put(name, group);
						}
					}
				}
			}
			catch(Exception e)
			{
				writeToLog("Failed to load presets", LoggingType.ERROR);
				writeStackTrace(e);
				JOptionPane.showMessageDialog(null, "Error: Could not load presets", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else
		{
			writeToLog("Presets file doesn't exist, skipping", 1);
		}
		
		return presets;
	}
	
	public static boolean savePresets(PresetOptions o)
	{
		XmlNode root = new XmlNode("presets");
		
		//Builds
		XmlNode buildsNode = root.addChild("builds");
		for(String buildName : o.buildPresets.keySet())
		{
			ItemBuild build = o.buildPresets.get(buildName);
			
			XmlNode buildNode = buildsNode.addChild("build");
			buildNode.addAttribute("name", buildName);
			
			for(ItemGroup group : build.getGroups())
			{
				XmlNode groupNode = buildNode.addChild("group");
				groupNode.addAttribute("name", group.getName());
				
				for(int n = 0; n < group.getItems().size(); n++)
					groupNode.addChild("item").addAttribute("id", group.getItems().get(n).getID()).addAttribute("count", group.getItemCounts().get(n)).close();
				
				groupNode.close();
			}
			
			buildNode.close();
		}
		buildsNode.close();
		
		//Groups
		XmlNode groupsNode = root.addChild("groups");
		for(String groupName : o.groupPresets.keySet())
		{
			ItemGroup group = o.groupPresets.get(groupName);
			
			XmlNode groupNode = groupsNode.addChild("group");
			groupNode.addAttribute("name", groupName);
			
			for(int n = 0; n < group.getItems().size(); n++)
				groupNode.addChild("item").addAttribute("id", group.getItems().get(n).getID()).addAttribute("count", group.getItemCounts().get(n)).close();
			
			groupNode.close();
		}
		groupsNode.close();
		
		root.close();
		
		//Write
		XmlWriter writer = null;
		try
		{
			writer = new XmlWriter(file, "xml", "1.0");
			writer.write(root);
		}
		catch(Exception e)
		{
			writeToLog("Failed to save presets", LoggingType.ERROR);
			writeStackTrace(e);
		}
		finally
		{
			if(writer != null)
				writer.close();
		}
		
		return false;
	}
	
	//Accessor methods
	
	public List<String> getBuildNames()
	{
		List<String> names = new ArrayList<String>(buildPresets.size());
		for(String name : buildPresets.keySet())
			names.add(name);
		Collections.sort(names);
		return names;
	}
	
	public List<String> getGroupNames()
	{
		List<String> names = new ArrayList<String>(groupPresets.size());
		for(String name : groupPresets.keySet())
			names.add(name);
		Collections.sort(names);
		return names;
	}
}
