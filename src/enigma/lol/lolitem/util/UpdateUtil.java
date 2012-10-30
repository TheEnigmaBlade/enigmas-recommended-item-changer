package enigma.lol.lolitem.util;

import java.io.*;
import java.util.*;
import javax.swing.*;

import enigma.paradoxion.io.*;
import enigma.paradoxion.io.cache.*;
import enigma.paradoxion.localization.*;
import enigma.paradoxion.util.*;
import enigma.paradoxion.util.Logger.*;
import static enigma.paradoxion.util.Logger.*;

import enigma.lol.lolitem.*;

public class UpdateUtil
{
	private static final String cacheURL = "http://dl.dropbox.com/u/1240253/Software/LoLTools/cache/";
	private static final String[] cacheFiles = {"champions.xml", "items.xml", "default_items.xml", "game_version.txt"};
	
	public static void startUpdater(final String appKey, final String version, final String buildVersion, final boolean verbose)
	{
		try
		{
			Thread t = new Thread(new Runnable(){
				@Override
				public void run()
				{
					writeToLog("Starting updater...");
					String command = "Updater.exe -program "+appKey+" -version "+version+"."+buildVersion+" -exec Enigma Item Changer.exe"+(!verbose ? " -quiet" : "");
					ProcessBuilder builder = new ProcessBuilder(new String[]{"cmd.exe", "/C", command});
					boolean update = false;
					try
					{
						Process process = builder.start();
						InputStream in = process.getInputStream();
						Scanner scanner = new Scanner(in);
						boolean waitForClose = true;
						while(waitForClose && scanner.hasNext())
						{
							String line = scanner.nextLine().trim();
							writeToLog("Updater output: \""+line+"\"", 1);
							if(line.equals("!isUpdating!"))
							{
								update = true;
								waitForClose = false;
							}
							else if(line.equals("!notUpdating!"))
							{
								waitForClose = false;
							}
						}
						scanner.close();
					}
					catch(Exception e)
					{
						writeToLog("Error while starting updater", LoggingType.ERROR);
						writeStackTrace(e);
					}
					if(update)
						SystemUtil.exit(0);
				}
			});
			t.start();
		}
		catch(Exception e)
		{
			writeToLog("Failed to start updater", LoggingType.ERROR);
			writeStackTrace(e);
		}
	}
	
	public static void finishUpdate()
	{
		File dir = new File(System.getProperty("java.io.tmpdir")+"Enigma_Update_"+LoLItems.appKey);
		if(dir.exists() && dir.isDirectory())
		{
			writeToLog("Update not completed, copying remaining files");
			try
			{
				File destDir = new File(new File(".").getCanonicalPath());
				writeToLog("Source dir: "+dir.getAbsolutePath(), 1);
				writeToLog("Dest dir: "+destDir.getAbsolutePath(), 1);
				IOUtil.copyDirectory(dir, destDir);
				IOUtil.deleteDirectory(dir); //I accidentally used destDir when I first wrote this and proceeded to test.  BAD THINGS HAPPENED... ;-;
			}
			catch(IOException e)
			{
				writeToLog("Failed to complete the update", LoggingType.ERROR);
				writeStackTrace(e);
			}
		}
	}
	
	public static boolean startCacheUpdater(final boolean verbose)
	{
		try
		{
			boolean updated = CacheUpdater.updateCache(new File("cache.dat"), cacheURL, cacheFiles);
			if(verbose)
			{
				if(updated)
					JOptionPane.showMessageDialog(null, LocaleDatabase.getString("dialog.update.cache.updated"), LocaleDatabase.getString("dialog.update.cache.updatedTitle"), JOptionPane.INFORMATION_MESSAGE);
				else
					JOptionPane.showMessageDialog(null, LocaleDatabase.getString("dialog.update.cache.noUpdate"), LocaleDatabase.getString("dialog.update.cache.noUpdateTitle"), JOptionPane.INFORMATION_MESSAGE);
			}
			return updated;
		}
		catch(Exception e)
		{
			writeToLog("Error while updating cache");
			writeStackTrace(e);
			return false;
		}
	}
}
