package enigma.installer;

import java.util.*;
import enigma.installer.util.*;

public class InstallerSettings
{
	private static Map<String, String> settings;
	
	public static void loadSettings()
	{
		if(settings == null)
		{
			settings = new HashMap<String, String>();
			
			try
			{
				Scanner scanner = new Scanner(ResourceLoader.getResourceStream("installer/config.txt"));
				while(scanner.hasNext())
				{
					String line = scanner.nextLine().trim();
					String[] parts = line.split(":");
					settings.put(parts[0], parts[1]);
				}
			}
			catch(Exception e)
			{
				System.out.println("Error: Failed to load installer settings");
				e.printStackTrace();
			}
		}
	}
	
	public static String getSetting(String key)
	{
		return settings.get(key);
	}
}
