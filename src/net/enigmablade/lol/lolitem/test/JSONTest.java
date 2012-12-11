package net.enigmablade.lol.lolitem.test;

import java.io.*;

import org.json.simple.*;
import org.json.simple.parser.*;

public class JSONTest
{
	public static void main(String[] args)
	{
		InputStream in = JSONTest.class.getResourceAsStream("test.json");
		
		//Parse JSON
		JSONParser parser = new JSONParser();
		JSONObject root = null;
		try
		{
			root = (JSONObject)parser.parse(new InputStreamReader(in));
		}
		catch(IOException e)
		{
			System.out.println("Could not find JSON file");
			e.printStackTrace();
			return;
		}
		catch(ParseException e)
		{
			System.out.println("Could not parse JSON");
			e.printStackTrace();
			return;
		}
		
		//Get data from parsed JSON
		System.out.println("Input: "+root+"\n");
		
		for(Object obj : root.keySet())
		{
			String key = (String)obj;
			Object value = root.get(key);
			//System.out.println("Key: "+key+", value: "+value);
			if("champion".equals(key))
			{
				String champion = (String)value;
				System.out.println("Champion = "+champion);
			}
			else if("title".equals(key))
			{
				String title = (String)value;
				System.out.println("Title = "+title);
			}
			else if("priority".equals(key))
			{
				boolean priority = (Boolean)value;
				System.out.println("Priority = "+priority);
			}
			else if("map".equals(key))
			{
				String map = (String)value;
				if("1".equals(map))
					map = "Summoner's Rift";
				else if("3".equals(map))
					map = "The Proving Grounds";
				else if("8".equals(map))
					map = "The Crystal Scar";
				else if("10".equals(map))
					map = "The Twisted Treeline";
				System.out.println("Map = "+map);
			}
			else if("mode".equals(key))
			{
				String mode = (String)value;
				System.out.println("Mode = "+mode);
			}
			else if("type".equals(key))
			{
				String type = (String)value;
				System.out.println("Type = "+type);
			}
			else if("blocks".equals(key))
			{
				JSONArray blocks = (JSONArray)value;
				for(Object o : blocks)
				{
					JSONObject block = (JSONObject)o;
					System.out.println("Items:");
					
					String type = (String)block.get("type");
					System.out.println("\tGroup = "+type);
					
					JSONArray itemsList = (JSONArray)block.get("items");
					//System.out.println("\t\tItems = "+itemsList);
					for(Object o2 : itemsList)
					{
						JSONObject item = (JSONObject)o2;
						//System.out.println("\t\tItem = "+item);
						
						String id = (String)item.get("id");
						Long count = (Long)item.get("count");
						System.out.println("\t\tID = "+id+", count = "+count);
					}
				}
			}
		}
	}
}
